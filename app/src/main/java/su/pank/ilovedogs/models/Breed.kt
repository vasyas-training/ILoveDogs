package su.pank.ilovedogs.models

data class Breed(val name: String, val subBreed: Breed?, val dogs: List<String>)
