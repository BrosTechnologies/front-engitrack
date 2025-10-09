package pe.edu.upc.engitrack.features.home.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val image: String
)
