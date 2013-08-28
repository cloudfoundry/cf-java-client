package org.cloudfoundry.gradle.text

class FlexibleTableOutput {
    private def rows = []
    private def columns = [:]

    public void addRow(def values) {
        rows << values

        values.each { name, value ->
            if (columns[name]) {
                columns[name] << value.length()
            } else {
                columns[name] = [name.length(), value.length()]
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder()

        def columnSizes = [:]

        columns.each { name, sizes ->
            def max = sizes.max()
            columnSizes[name] = max
            sb.append(name.padRight(max)).append('  ')
        }
        sb.append('\n')

        columnSizes.each { name, size ->
            sb.append("".padRight(size, '-')).append('  ')
        }
        sb.append('\n')

        rows.each { row ->
            row.each { name, value ->
                sb.append(value.padRight(columnSizes[name])).append('  ')
            }
            sb.append('\n')
        }

        sb.toString()
    }
}
