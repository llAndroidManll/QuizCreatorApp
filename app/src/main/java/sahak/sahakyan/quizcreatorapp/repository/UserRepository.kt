package sahak.sahakyan.quizcreatorapp.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import sahak.sahakyan.quizcreatorapp.entity.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = database.getReference("users")

    suspend fun getCurrentUser(): User{
        val user = auth.currentUser
        return User(
            email = user?.email.toString(),
            profilePictureUrl = user?.photoUrl.toString()
        )
    }

    private suspend fun isExistingUser() : Boolean {
        Log.e("QuizCreatorApp", "isExistingUser()")
        val userRef = usersRef.child(auth.currentUser?.uid.toString())
        return try {
            val snapshot = userRef.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            Log.e("QuizCreatorApp", "Error checking if user exists: $e")
            false
        }
    }

    suspend fun createUser() {
        val bool = isExistingUser()
        Log.i("QuizCreatorApp", "createUser() -- $bool")
        if (!isExistingUser()) {
            Log.i("QuizCreatorApp", "TRUE")
            usersRef.child(auth.currentUser?.uid.toString()).setValue(getCurrentUser())
        }
    }
}