package pe.edu.upc.engitrack.features.home.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pe.edu.upc.engitrack.features.home.data.local.dao.ProductDao
import pe.edu.upc.engitrack.features.home.data.local.models.ProductEntity
import pe.edu.upc.engitrack.features.home.data.remote.services.ProductService
import pe.edu.upc.engitrack.features.home.domain.repositories.ProductRepository
import pe.edu.upc.engitrack.shared.models.Product
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val service: ProductService,
    private val dao: ProductDao
) : ProductRepository {
    override suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        val response = service.getAllProducts()

        if (response.isSuccessful) {
            response.body()?.let { productsWrapperDto ->
                val products = productsWrapperDto.products.map { productDto ->
                    Product(
                        id = productDto.id,
                        name = productDto.title,
                        price = productDto.price,
                        image = productDto.thumbnail
                    )
                }
                return@withContext products

            }
        }


        return@withContext emptyList()
    }

    override suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        val response = service.getProductById(id)

        if (response.isSuccessful) {
            response.body()?.let { productDto ->
                return@withContext Product(
                    id = productDto.id,
                    name = productDto.title,
                    price = productDto.price,
                    image = productDto.thumbnail,
                    isFavorite = dao.fetchById(productDto.id).isNotEmpty()
                )
            }
        }

        return@withContext null
    }

    override suspend fun insert(product: Product) = withContext(Dispatchers.IO) {
        dao.insert(
            ProductEntity(
                id = product.id,
                name = product.name,
                product.price,
                image = product.image
            )
        )
    }

    override suspend fun delete(product: Product) {
        dao.delete(
            ProductEntity(
                id = product.id,
                name = product.name,
                product.price,
                image = product.image
            )
        )
    }
}