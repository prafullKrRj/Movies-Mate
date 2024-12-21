package com.prafullkumar.moviesmate.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.prafullkumar.moviesmate.domain.AuthenticationRepo
import com.prafullkumar.moviesmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent

data class User(val username: String, val password: String)

class AuthenticationRepoImpl : AuthenticationRepo, KoinComponent {


    private val auth = FirebaseAuth.getInstance()


    override fun login(username: String, password: String): Flow<Resource<User>> {
        return flow {
            try {
                val user = User("$username@moviesmate.com", password)
                val result =
                    auth.signInWithEmailAndPassword("$username@moviesmate.com", password).await()
                if (result.user == null) {
                    emit(Resource.Error("An error occurred"))
                    return@flow
                }
                auth.updateCurrentUser(result.user!!)
                emit(Resource.Success(user))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

    override fun register(
        username: String,
        password: String,
        fullName: String
    ): Flow<Resource<User>> {
        return flow {
            try {
                val user = User(username, password)
                val result =
                    auth.createUserWithEmailAndPassword("$username@moviesmate.com", password)
                        .await()
                result.user?.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(fullName).build()
                )
                    ?.await()
                if (result.user == null) {
                    Log.d("Authentication", "register: ${result.user}")
                    emit(Resource.Error("An error occurred"))
                    return@flow
                }
                auth.updateCurrentUser(result.user!!)
                emit(Resource.Success(user))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

    override fun changeUsername(userName: String): Flow<Resource<User>> {
        return flow {
            try {
                val newUser = User(userName, "")
                val user = auth.currentUser ?: run {
                    emit(Resource.Error("User not logged in"))
                    return@flow
                }
                user.updateEmail(userName).await()
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }
}