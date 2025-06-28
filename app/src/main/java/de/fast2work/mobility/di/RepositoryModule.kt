package de.fast2work.mobility.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.fast2work.mobility.di.helper.AuthenticationRepoHelper
import de.fast2work.mobility.ui.authentication.AuthenticationRepository
import de.fast2work.mobility.utility.util.ResourceProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun provideAuthenticationRepo(authenticationRepository: AuthenticationRepository): AuthenticationRepoHelper {
        return authenticationRepository
    }

    @Provides
    @Singleton
    fun provideResource(app: Application): ResourceProvider {
        return ResourceProvider(app.applicationContext)
    }

}