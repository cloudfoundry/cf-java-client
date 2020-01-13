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

package org.cloudfoundry.util.tuple;

import org.junit.Test;
import reactor.util.function.Tuples;

import static org.assertj.core.api.Assertions.assertThat;

public final class TupleUtilsTest {

    @Test
    public void consumer2() {
        TupleUtils
            .consumer((first, second) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
            })
            .accept(Tuples.of(1, 2));
    }

    @Test
    public void consumer3() {
        TupleUtils
            .consumer((first, second, third) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
            })
            .accept(Tuples.of(1, 2, 3));
    }

    @Test
    public void consumer4() {
        TupleUtils
            .consumer((first, second, third, fourth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
            })
            .accept(Tuples.of(1, 2, 3, 4));
    }

    @Test
    public void consumer5() {
        TupleUtils
            .consumer((first, second, third, fourth, fifth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
            })
            .accept(Tuples.of(1, 2, 3, 4, 5));
    }

    @Test
    public void consumer6() {
        TupleUtils
            .consumer((first, second, third, fourth, fifth, sixth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
            })
            .accept(Tuples.of(1, 2, 3, 4, 5, 6));
    }

    @Test
    public void consumer7() {
        TupleUtils
            .consumer((first, second, third, fourth, fifth, sixth, seventh) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);
            })
            .accept(Tuples.of(1, 2, 3, 4, 5, 6, 7));
    }

    @Test
    public void consumer8() {
        TupleUtils
            .consumer((first, second, third, fourth, fifth, sixth, seventh, eighth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);
                assertThat(eighth).isEqualTo(8);
            })
            .accept(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void function2() {
        int result = TupleUtils
            .function((first, second) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);

                return -1;
            })
            .apply(Tuples.of(1, 2));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function3() {
        int result = TupleUtils
            .function((first, second, third) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function4() {
        int result = TupleUtils
            .function((first, second, third, fourth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3, 4));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function5() {
        int result = TupleUtils
            .function((first, second, third, fourth, fifth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3, 4, 5));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function6() {
        int result = TupleUtils
            .function((first, second, third, fourth, fifth, sixth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3, 4, 5, 6));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function7() {
        int result = TupleUtils
            .function((first, second, third, fourth, fifth, sixth, seventh) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3, 4, 5, 6, 7));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void function8() {
        int result = TupleUtils
            .function((first, second, third, fourth, fifth, sixth, seventh, eighth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);
                assertThat(eighth).isEqualTo(8);

                return -1;
            })
            .apply(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8));

        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void predicate2() {
        boolean result = TupleUtils
            .predicate((first, second) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);

                return true;
            })
            .test(Tuples.of(1, 2));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate3() {
        boolean result = TupleUtils
            .predicate((first, second, third) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);

                return true;
            })
            .test(Tuples.of(1, 2, 3));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate4() {
        boolean result = TupleUtils
            .predicate((first, second, third, fourth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);

                return true;
            })
            .test(Tuples.of(1, 2, 3, 4));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate5() {
        boolean result = TupleUtils
            .predicate((first, second, third, fourth, fifth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);

                return true;
            })
            .test(Tuples.of(1, 2, 3, 4, 5));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate6() {
        boolean result = TupleUtils
            .predicate((first, second, third, fourth, fifth, sixth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);

                return true;
            })
            .test(Tuples.of(1, 2, 3, 4, 5, 6));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate7() {
        boolean result = TupleUtils
            .predicate((first, second, third, fourth, fifth, sixth, seventh) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);

                return true;
            })
            .test(Tuples.of(1, 2, 3, 4, 5, 6, 7));

        assertThat(result).isTrue();
    }

    @Test
    public void predicate8() {
        boolean result = TupleUtils
            .predicate((first, second, third, fourth, fifth, sixth, seventh, eighth) -> {
                assertThat(first).isEqualTo(1);
                assertThat(second).isEqualTo(2);
                assertThat(third).isEqualTo(3);
                assertThat(fourth).isEqualTo(4);
                assertThat(fifth).isEqualTo(5);
                assertThat(sixth).isEqualTo(6);
                assertThat(seventh).isEqualTo(7);
                assertThat(eighth).isEqualTo(8);

                return true;
            })
            .test(Tuples.of(1, 2, 3, 4, 5, 6, 7, 8));

        assertThat(result).isTrue();
    }

}
