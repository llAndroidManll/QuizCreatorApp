package sahak.sahakyan.quizcreatorapp.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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
        val quiz = getQuiz(quizId)
        quiz!!.questions.add(question)

        Log.i("Quiz", "QuizRepository addQuestion() quiz -- $quiz ")

        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions").setValue(quiz.questions)
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

    suspend fun getQuestion(quizId: String, questionId: Int): Question? {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).child(quizId).child("questions").child(questionId.toString())
                   .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val question = dataSnapshot.getValue(Question::class.java)
                            Log.i("Quiz", "QuizRepository getQuestion(): Question is found -- $question")
                            continuation.resume(question)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.i("Quiz", "QuizRepository getQuestion(): Question Cannot be find")
                            continuation.resume(null)
                        }
                   })
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun getQuizzes(): List<Quiz>? {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val quizzes = mutableListOf<Quiz>()
                        continuation.resume(quizzes)
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

    suspend fun updateQuestion(quizId: String,questionId: Int, question: Question) {
        val questions: ArrayList<Question> = getQuestionsList(quizId)!!
        questions[questionId] = question
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions").setValue(questions)
    }

    suspend fun isQuestionExistWithId(quizId: String,questionId: String) : Boolean {
        val questions: ArrayList<Question> = getQuestionsList(quizId)!!
        return questions.any {
            it.id == questionId.toInt()
        }
    }
    suspend fun getQuestionsListSize(quizId: String) : Int {
        return getQuestionsList(quizId)!!.size
    }

    private suspend fun getQuestionsList(quizId: String): ArrayList<Question>? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val userUid = auth.currentUser?.uid
                if (userUid != null) {
                    quizzesRef.child(userUid).child(quizId).child("questions").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val questionList: ArrayList<Question> = dataSnapshot.children.mapNotNull { it.getValue(Question::class.java) }.toCollection(ArrayList())
                            continuation.resume(questionList)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            continuation.resumeWithException(databaseError.toException())
                        }
                    })
                } else {
                    continuation.resume(null)
                }
            }
        }
    }

}