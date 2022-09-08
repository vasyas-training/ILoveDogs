package su.pank.ilovedogs

import org.junit.Test

import org.junit.Assert.*
import su.pank.ilovedogs.api.getBreeds

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {
    @Test
    suspend fun getBreedsTest(){
        val breeds = getBreeds()
        for (el in breeds){
            println(el)
        }
        assertEquals(breeds.size, 96)
    }
}