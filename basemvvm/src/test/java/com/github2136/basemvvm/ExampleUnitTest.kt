package com.github2136.basemvvm

import org.junit.Test
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val replay=true
        val file = File("aaaa/ssss/aaaa")
        var newFile = File("aaa/ssss/abcd")
        val index = newFile.name.lastIndexOf(".")
        val name = StringBuilder(newFile.name)
        if (index == -1) {
            name.append("%s")
        } else {
            name.insert(index, "%s")
        }
        String.format("")
        newFile = File(newFile.parent, name.toString().format("-1"))

        print(newFile .name)

    }
}