package pe.edu.upc.engitrack.features.home.presentation.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.home.domain.repositories.ProductRepository
import pe.edu.upc.engitrack.shared.models.Product
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(private val repository: ProductRepository) : ViewModel() {
    private var _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun getProductById(id: Int) {
        viewModelScope.launch {
            _product.value = repository.getProductById(id)
        }
    }

    fun toggleFavorite() {

        _product.value?.let { product ->
            viewModelScope.launch {
                if (product.isFavorite) {
                    repository.delete(product)
                } else {
                    repository.insert(product)
                }
                _product.value = product.copy(
                    isFavorite = !product.isFavorite
                )
            }
        }

    }
}