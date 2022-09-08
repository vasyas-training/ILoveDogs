package su.pank.ilovedogs.models

data class Breed(val name: String, var subBreed: List<Breed>? = null, var logo: String? = null, var dogs: List<String>? = null)
