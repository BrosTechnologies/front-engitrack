package pe.edu.upc.engitrack.features.projects.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pe.edu.upc.engitrack.features.projects.data.remote.ProjectApiService
import pe.edu.upc.engitrack.features.projects.data.repositories.ProjectRepositoryImpl
import pe.edu.upc.engitrack.features.projects.domain.repositories.ProjectRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectModule {

    @Binds
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository

    companion object {
        @Provides
        @Singleton
        fun provideProjectApiService(retrofit: Retrofit): ProjectApiService {
            return retrofit.create(ProjectApiService::class.java)
        }
    }
}