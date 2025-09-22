package org.cloudfoundry.uaa.ratelimit;

import java.util.List;
import org.immutables.value.Value;

public interface PathSelectorModel {

    enum PathMatchType {
        equals,
        startsWith,
        other
    }

    @Value.Immutable
    interface _PathSelector {
        PathMatchType type();

        String path();

        default boolean matches(String id) {
            switch (type()) {
                case equals:
                    return path().equals(id);
                case startsWith:
                    return id.startsWith(path());
                case other:
                    return true;
                default:
                    System.err.println("unhandled enum value " + type());
            }
            return true;
        }
    }

    @Value.Immutable
    interface _LimiterMapping {
        /**
         * time until the ratelimit is reset in seconds.
         * @return
         */
        @Value.Default
        default int timeBase() {
            return 1;
        }
        ;

        String name();

        @Value.Default
        default int limit() {
            return 0;
        }
        ;

        List<PathSelector> pathSelectors();

        default boolean matches(String url) {
            for (PathSelector oneSelector : pathSelectors()) {
                if (oneSelector.matches(url)) {
                    return true;
                }
            }
            return false;
        }
    }
}
