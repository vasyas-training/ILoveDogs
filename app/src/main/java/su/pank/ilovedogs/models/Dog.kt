package su.pank.ilovedogs.models

// Собака - класс для отображение изображения опр. породы собаки
data class Dog(val breed: String, val imageUrls: MutableList<String>, val subBreed: String? = null)
