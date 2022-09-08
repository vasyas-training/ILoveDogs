package su.pank.ilovedogs

import org.json.JSONArray
import org.junit.Test

import org.junit.Assert.*
import su.pank.ilovedogs.api.getBreeds
import su.pank.ilovedogs.models.Breed

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTest {
    @Test
    fun testBreed(){
        val breed = Breed("Vasya")
        assertEquals(breed.name, "Vasya")
    }

    @Test
    fun testSubBreed(){
        val breed = Breed("Vasya")
        breed.subBreeds = listOf(Breed("Vova"), Breed("Victor"))

        assertEquals(JSONArray(breed.subBreedsToArray().toTypedArray()).toString(), """["Vova","Victor"]""")
    }
}