/*
 * Copyright 2013-2020 the original author or authors.
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

package org.cloudfoundry.operations.applications;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class WordListRandomWords implements RandomWords {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public String getAdjective() {
        return Adjectives.WORDS.get(RANDOM.nextInt(Adjectives.WORDS.size()));
    }

    @Override
    public String getNoun() {
        return Nouns.WORDS.get(RANDOM.nextInt(Nouns.WORDS.size()));
    }

    private static BufferedReader getReader(String resourceName) {
        InputStream inputStream = WordListRandomWords.class.getClassLoader().getResourceAsStream(resourceName);
        return new BufferedReader(new InputStreamReader(inputStream));
    }

    private static List<String> getWordList(String resourceName) {
        try (Stream<String> stream = getReader(resourceName).lines()) {
            return stream
                .map(String::trim)
                .collect(Collectors.toList());
        }
    }

    private static final class Adjectives {

        private static List<String> WORDS = getWordList("adjectives.txt");

    }

    private static final class Nouns {

        private static List<String> WORDS = getWordList("nouns.txt");

    }

}
