package pe.edu.upc.engitrack.shared.models

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String,
    val isFavorite: Boolean = false
)

