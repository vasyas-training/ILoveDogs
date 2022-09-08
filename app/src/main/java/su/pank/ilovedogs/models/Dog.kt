package su.pank.ilovedogs.models

data class Dog(val breed: String, val imageUrls: MutableList<String>, val subBreed: String? = null)
