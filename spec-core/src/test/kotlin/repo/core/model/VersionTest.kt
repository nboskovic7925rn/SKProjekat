package repo.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class VersionTest {
    @Test
    fun staJeVece(){
        assertTrue {Version("1.10") > Version("1.9")  }
    }

    @Test
    fun daLiJeJednako(){
        assertEquals(0,Version("1.10.0 ").compareTo(Version("1.10")))
    }

    @Test
    fun daLiPuca(){
        assertFailsWith<IllegalArgumentException> { Version("abc") }
    }

}