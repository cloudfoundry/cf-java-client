/*
 * Copyright 2013-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.logcachetest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LogCacheTestApplication {

    private final ObjectMapper objectMapper;

    @Autowired
    public LogCacheTestApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogCacheTestApplication.class, args);
    }

    @RequestMapping("/counter")
    public String counter(@RequestParam("name") String name, @RequestParam("delta") Long delta) throws Exception {
        final String json = this.objectMapper.writeValueAsString(Counter.builder()
            .name(name)
            .delta(delta)
            .build());
        System.out.println(json);
        return json;
    }

    @RequestMapping("/event")
    public String event(@RequestParam("title") String title, @RequestParam("body") String body) throws Exception {
        final String json = this.objectMapper.writeValueAsString(Event.builder()
            .title(title)
            .body(body)
            .build());
        System.out.println(json);
        return json;
    }

    @RequestMapping("/gauge")
    public String gauge(@RequestParam("name") String name, @RequestParam("value") Double value) throws Exception {
        final String json = this.objectMapper.writeValueAsString(Gauge.builder()
            .name(name)
            .value(value)
            .build());
        System.out.println(json);
        return json;
    }

    @RequestMapping("/log")
    public String log(@RequestParam("message") String message) {
        System.out.println(message);
        return message;
    }

    @RequestMapping("/test")
    public String test() {
        final String output = "{\"type\":\"event\",\"title\":\"Test Title\",\"body\":\"Test Body\"}";
        System.out.println(output);
        return output;
    }

}
