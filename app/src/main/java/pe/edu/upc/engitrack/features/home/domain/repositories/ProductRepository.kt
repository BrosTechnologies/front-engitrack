package pe.edu.upc.engitrack.features.home.domain.repositories

import pe.edu.upc.engitrack.shared.models.Product

interface ProductRepository {

    suspend fun getAllProducts(): List<Product>

    suspend fun getProductById(id: Int): Product?

    suspend fun insert(product: Product)

    suspend fun delete(product: Product)
}