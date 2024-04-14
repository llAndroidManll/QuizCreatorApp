package sahak.sahakyan.quizcreatorapp.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import java.util.UUID

class QuizRepository {

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val quizzesRef = database.getReference("quizzes")

    suspend fun saveQuiz(quiz: Quiz) {

        quizzesRef.child(auth.currentUser?.uid.toString()).child(quiz.id).setValue(quiz)
    }

    fun generateId() = UUID.randomUUID().toString()
    /*
    suspend fun addQuestion(question: Question, quizId: String) {
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions").setValue(question)
    }*/



    /*
    suspend fun getQuizzes(): List<Quiz> {
        return quizzesRef.child(auth.currentUser?.uid.toString())
    }*/

}