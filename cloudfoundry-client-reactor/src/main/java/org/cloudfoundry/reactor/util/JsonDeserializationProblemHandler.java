package org.cloudfoundry.reactor.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDeserializationProblemHandler extends DeserializationProblemHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("cloudfoundry-client");
    private static Set<PropertyToIgnore> propertiesToIgnore = new HashSet<>();

    public static void addPropertyToIgnore(String className, String propertyName, String jsonPath) {
        PropertyToIgnore oneEntry =
                new PropertyToIgnore(className, propertyName, jsonPath.substring(1));
        propertiesToIgnore.add(oneEntry);
    }

    /**
     * only for unittests
     */
    public static void flush() {
        propertiesToIgnore = new HashSet<>();
    }

    @Override
    public boolean handleUnknownProperty(
            DeserializationContext ctxt,
            JsonParser jp,
            JsonDeserializer<?> deserializer,
            Object beanOrClass,
            String propertyName) {
        JsonPointer jsonPointer = toJsonPointer(jp);
        Class<?> rootType =
                (beanOrClass instanceof Class<?>)
                        ? (Class<?>) beanOrClass
                        : (beanOrClass != null ? beanOrClass.getClass() : null);
        String className = "unknown";
        if (rootType != null) {
            className = rootType.getCanonicalName();
        }
        LOGGER.info(
                "Unknown property "
                        + propertyName
                        + " at "
                        + jsonPointer
                        + " while deserializing "
                        + className);
        boolean shouldDelete = propertyShouldBeDroped(className, jsonPointer, propertyName);
        if (shouldDelete) {
            try {
                jp.skipChildren();
                LOGGER.info(
                        "Ignoring property "
                                + propertyName
                                + " at "
                                + jsonPointer
                                + " as configured.");
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public Object handleInstantiationProblem(
            DeserializationContext ctxt, Class<?> instClass, Object argument, Throwable t)
            throws IOException {
        JsonParser jp = ctxt.getParser();
        JsonPointer jsonPointer = toJsonPointer(jp);
        String className = instClass.getCanonicalName();
        LOGGER.info(
                "Unknown value "
                        + argument
                        + " at "
                        + jsonPointer
                        + " while deserializing "
                        + className);
        boolean shouldDelete = propertyShouldBeDroped(className, jsonPointer, argument.toString());
        if (shouldDelete) {
            LOGGER.info("Ignoring value " + argument + " at " + jsonPointer + " as configured.");
            throw new RetryException(jsonPointer, argument.toString());
        }
        return NOT_HANDLED;
    }

    public Object handleUnexpectedToken(
            DeserializationContext ctxt,
            JavaType targetType,
            JsonToken t,
            JsonParser p,
            String failureMsg)
            throws IOException {
        JsonPointer jsonPointer = toJsonPointer(p);
        String className = targetType.toCanonical();
        LOGGER.info(
                "Unknown Value "
                        + t.asString()
                        + " at "
                        + jsonPointer
                        + " while deserializing "
                        + className);
        boolean shouldDelete = propertyShouldBeDroped(className, jsonPointer, t.asString());
        if (shouldDelete) {
            LOGGER.info(
                    "Ignoring value " + t.asString() + " at " + jsonPointer + " as configured.");
            throw new RetryException(jsonPointer, t.asString());
        } else {
            jsonPointer = jsonPointer.appendProperty(t.asString());
            shouldDelete = propertyShouldBeDroped(className, jsonPointer, t.asString());
            if (shouldDelete) {
                LOGGER.info(
                        "Ignoring value "
                                + t.asString()
                                + " at "
                                + jsonPointer
                                + " as configured.");
                throw new RetryException(jsonPointer, t.asString());
            }
        }
        return NOT_HANDLED;
    }

    public Object handleMissingInstantiator(
            DeserializationContext ctxt,
            Class<?> instClass,
            ValueInstantiator valueInsta,
            JsonParser p,
            String msg)
            throws IOException {
        JsonPointer jsonPointer = toJsonPointer(p);
        String className = instClass.getTypeName();
        LOGGER.info(
                "Unknown instantiator "
                        + p.currentName()
                        + " value "
                        + p.getText()
                        + " at "
                        + jsonPointer
                        + " while deserializing "
                        + className);
        boolean shouldDelete = propertyShouldBeDroped(className, jsonPointer, p.currentName());
        if (shouldDelete) {
            LOGGER.info("Ignoring value " + p.getText() + " at " + jsonPointer + " as configured.");
            throw new RetryException(jsonPointer, p.getText());
        } else {
            jsonPointer = jsonPointer.appendProperty(p.getText());
            shouldDelete = propertyShouldBeDroped(className, jsonPointer, p.currentName());
            if (shouldDelete) {
                LOGGER.info(
                        "Ignoring value " + p.getText() + " at " + jsonPointer + " as configured.");
                throw new RetryException(jsonPointer, p.getText());
            }
        }
        return NOT_HANDLED;
    }

    // Get the pointer to the failing token from the parser.
    private static JsonPointer toJsonPointer(JsonParser p) {
        Deque<String> segments = new ArrayDeque<>();
        JsonStreamContext ctx = p.getParsingContext();

        while (ctx != null) {
            if (ctx.inArray()) {
                segments.push(String.valueOf(ctx.getCurrentIndex()));
            } else if (ctx.inObject()) {
                if (ctx.getCurrentName() != null) {
                    segments.push(ctx.getCurrentName());
                }
            }
            ctx = ctx.getParent();
        }
        StringBuilder pointer = new StringBuilder();
        while (!segments.isEmpty()) {
            pointer.append('/').append(segments.pop());
        }
        return JsonPointer.compile(pointer.toString());
    }

    // check if the current values are listed in the configuration for properties to be ignored.
    private static boolean propertyShouldBeDroped(
            String className, JsonPointer pointer, String property) {
        if (property == null) {
            property = "";
        }
        for (PropertyToIgnore oneProperty : propertiesToIgnore) {
            if (className.matches(oneProperty.className.replace("*", ".*"))
                    && pointer.toString().matches("/" + oneProperty.jsonPath.replace("*", ".*"))
                    && property.matches(oneProperty.propertyName.replace("*", ".*"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a json byte array, find the named property at the given path and remove it.
     * @param payload  the byte array containing the json message.
     * @param property  the property or value that caused the parsing error.
     * @param jsonPath the path where the property was found.
     * @return a json string without the named property or null in case of any error.
     */
    public static byte[] dropProperty(byte[] payload, String property, JsonPointer jsonPointer) {
        JsonNode tree;
        try {
            tree = new ObjectMapper().readTree(payload);
        } catch (IOException e) {
            throw new JsonParsingException(
                    e.getMessage(), e, new String(payload, Charset.defaultCharset()));
        }
        JsonNode found = dropProperty(tree, jsonPointer, property);
        if (found != null) {
            return found.toString().getBytes();
        }
        return null;
    }

    // drop the property or value with the given name at the given pointer from the tree. If that
    // fails, return null to indicate problem.
    private static JsonNode dropProperty(JsonNode tree, JsonPointer pointer, String propertyName) {
        JsonPointer parent = pointer.head();
        if ("".equals(parent.toString())) {
            LOGGER.warn(
                    "parsing error in root element, can't delete "
                            + propertyName
                            + " at pointer "
                            + pointer);

            return null; // can't remove root
        }
        JsonNode parentNode = tree.at(parent);
        if (parentNode.isMissingNode()) {
            LOGGER.warn(
                    "cannot find and delete property " + propertyName + " at pointer " + pointer);
            return null;
        }
        if (parentNode.isObject()) {
            String lastToken = pointer.last().getMatchingProperty();
            JsonNode found = ((ObjectNode) parentNode).remove(lastToken);
            if (found == null) {
                LOGGER.warn(
                        "Cannot find and delete property "
                                + propertyName
                                + " at pointer "
                                + pointer);
                return null;
            } else {
                LOGGER.info("ignoring " + pointer + " as configured");
                return tree;
            }
        } else {
            if (parentNode.isArray()) {
                int index = pointer.last().getMatchingIndex();
                if (index >= 0 && index < parentNode.size()) {
                    JsonNode found = ((ArrayNode) parentNode).remove(index);
                    if (found == null) {
                        LOGGER.warn(
                                "Cannot find and delete property "
                                        + propertyName
                                        + " at pointer "
                                        + pointer);
                        return null;
                    } else {
                        LOGGER.info("ignoring " + pointer + " as configured");
                        return tree;
                    }
                }
            } else {
                // Invalid type in ValueNode, delete the complete node. e.g. true, when "true" is
                // expected.
                String nodename = parent.last().toString().replaceFirst("/", "");
                return dropProperty(tree, parent, nodename);
            }
        }
        return null;
    }

    private static class PropertyToIgnore {
        public PropertyToIgnore(String className2, String propertyName2, String jsonPath2) {
            className = className2;
            propertyName = propertyName2;
            jsonPath = jsonPath2;
        }

        private String className;
        private String jsonPath;
        private String propertyName;
    }

    public static class RetryException extends IOException {
        private static final long serialVersionUID = 1L;
        private JsonPointer jsonPointer;
        private String property;

        public RetryException(JsonPointer jsonPointer, String property) {
            this.jsonPointer = jsonPointer;
            this.property = property;
        }

        public JsonPointer getJsonPointer() {
            return jsonPointer;
        }

        public String getProperty() {
            return property;
        }
    }
}
