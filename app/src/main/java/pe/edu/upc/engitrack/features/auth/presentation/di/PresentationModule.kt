package pe.edu.upc.engitrack.features.auth.presentation.di

import pe.edu.upc.engitrack.features.auth.data.di.DataModule.getAuthRepository
import pe.edu.upc.engitrack.features.auth.presentation.login.LoginViewModel
import pe.edu.upc.engitrack.features.auth.presentation.register.RegisterViewModel
import pe.edu.upc.engitrack.features.profile.presentation.editprofile.EditProfileViewModel
import pe.edu.upc.engitrack.features.profile.presentation.profile.ProfileViewModel

object PresentationModule {

    fun getLoginViewModel(): LoginViewModel {
        return LoginViewModel(getAuthRepository())
    }

    fun getRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel(getAuthRepository())
    }

    fun getProfileViewModel(): ProfileViewModel {
        return ProfileViewModel()
    }

    fun getEditProfileViewModel(): EditProfileViewModel {
        return EditProfileViewModel()
    }
}