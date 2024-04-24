package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.tasks.await
import sahak.sahakyan.quizcreatorapp.entity.User
import sahak.sahakyan.quizcreatorapp.repository.UserRepository

class UserViewModel(
    private val userRepository: UserRepository = UserRepository()
): ViewModel() {
    suspend fun getCurrentUser(): User {
        return userRepository.getCurrentUser()
    }

    suspend fun createUser() {
        userRepository.createUser()
    }

}