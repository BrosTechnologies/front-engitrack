package pe.edu.upc.engitrack.features.home.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pe.edu.upc.engitrack.core.networking.ApiConstants
import pe.edu.upc.engitrack.features.auth.data.remote.AuthService
import pe.edu.upc.engitrack.features.auth.data.repositories.AuthRepositoryImpl
import pe.edu.upc.engitrack.features.auth.domain.repositories.AuthRepository
import pe.edu.upc.engitrack.features.home.data.remote.services.ProductService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    @Named("url")
    fun provideApiBaseUrl(): String {
        return ApiConstants.BASE_URL
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: pe.edu.upc.engitrack.core.networking.AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val detailedLoggingInterceptor = pe.edu.upc.engitrack.core.networking.DetailedLoggingInterceptor()
        
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Agregar interceptor de autenticación
            .addInterceptor(loggingInterceptor)
            .addInterceptor(detailedLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(@Named("url") url: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository {
        return authRepositoryImpl
    }
}