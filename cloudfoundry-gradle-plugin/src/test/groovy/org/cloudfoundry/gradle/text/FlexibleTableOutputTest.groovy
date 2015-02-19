package org.cloudfoundry.gradle.text

import org.junit.Test

class FlexibleTableOutputTest {
    @Test
    void testAdd() {
        FlexibleTableOutput output = new FlexibleTableOutput()

        output.addRow(first: 'a', second: '1234567890', longcolumname: 'zyxwvut')
        output.addRow(first: 'abcdefg', second: '123', longcolumname: 'zyxwvutsrqp')
        output.addRow(first: 'abcdefghijklmnop', second: '12345', longcolumname: 'zyx')

        assert output.toString() == """\
first             second      longcolumnname  
----------------  ----------  --------------
a                 1234567890  zyxwvut
abcdefg           123         zyxwvutsrqp
abcdefghijklmnop  12345       zyx
"""
    }
}
