package sahak.sahakyan.quizcreatorapp.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class QuizRepository {

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val quizzesRef = database.getReference("quizzes")

    suspend fun saveQuiz(quiz: Quiz) {

        quizzesRef.child(auth.currentUser?.uid.toString()).child(quiz.id).setValue(quiz)
    }

    fun generateId() = UUID.randomUUID().toString()

    suspend fun addQuestion(question: Question, quizId: String) {
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions").setValue(question)
    }

    suspend fun getQuiz(quizId: String): Quiz? {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).child(quizId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val quiz = dataSnapshot.getValue(Quiz::class.java)
                            continuation.resume(quiz)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            continuation.resume(null)
                        }
                    })
            } else {
                continuation.resume(null)
            }
        }
    }

    /*
    suspend fun getQuizzes(): List<Quiz> {
        return quizzesRef.child(auth.currentUser?.uid.toString())
    }*/

}