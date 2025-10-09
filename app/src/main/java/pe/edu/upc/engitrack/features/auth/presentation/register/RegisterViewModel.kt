package pe.edu.upc.engitrack.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.engitrack.features.auth.domain.models.User
import pe.edu.upc.engitrack.features.auth.domain.repositories.AuthRepository

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword: StateFlow<String> = _repeatPassword

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun updateFullName(value: String) {
        _fullName.value = value
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updatePassword(value: String) {
        _password.value = value
    }

    fun updateRepeatPassword(value: String) {
        _repeatPassword.value = value
    }

    fun register() {
        viewModelScope.launch {
            // TODO: Implement register logic in repository
            // For now, just simulating a login
            _user.value = repository.login(email.value, password.value)
        }
    }
}

