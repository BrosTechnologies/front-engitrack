package pe.edu.upc.engitrack.features.auth.presentation.di

import pe.edu.upc.engitrack.features.auth.data.di.DataModule.getAuthRepository
import pe.edu.upc.engitrack.features.auth.presentation.login.LoginViewModel

object PresentationModule {

    fun getLoginViewModel(): LoginViewModel {
        return LoginViewModel(getAuthRepository())
    }
}