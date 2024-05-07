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
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions")
            .setValue(quiz.questions)
    }

    suspend fun getQuiz(quizId: String): Quiz? {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).child(quizId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val quiz = dataSnapshot.getValue(Quiz::class.java)

                            Log.d("Quiz--Repository", "getQuiz(): Quiz: $quiz")

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
        Log.d("Quiz--Repository", "getQuestion(): Question id: $questionId")
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).child(quizId).child("questions")
                    .child(questionId.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val question = dataSnapshot.getValue(Question::class.java)
                            if (question != null) {
                                continuation.resume(question)
                            } else {
                                continuation.resumeWithException(NullPointerException("Question cannot be found"))
                            }
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


    suspend fun getQuizzes(): List<Quiz>? {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val quizzesList = mutableListOf<Quiz>()
                            for (quizSnapshot in dataSnapshot.children) {
                                val quiz = quizSnapshot.getValue(Quiz::class.java)
                                quiz?.let { quizzesList.add(it) }
                            }
                            if (quizzesList.isEmpty()) {
                                continuation.resumeWithException(IllegalArgumentException("Quizzes list is empty"))
                            } else {
                                continuation.resume(quizzesList)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Quiz--Repository", "getQuizzes(): onCancelled")
                            continuation.resume(null)
                        }
                    })
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun updateQuestion(quizId: String, questionId: Int, question: Question) {
        val questions: ArrayList<Question> = getQuestionsList(quizId)!!
        questions[questionId] = question
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions")
            .setValue(questions).addOnSuccessListener {
            Log.i(
                "Quiz--Repository",
                " updateQuestion(): Question has been updated, Questions: $questions"
            )
        }.addOnCanceledListener {
            Log.e("Quiz--Repository", " updateQuestion(): Question Cannot be updated")
        }
    }

    suspend fun isQuestionExistWithId(quizId: String, questionId: String): Boolean {
        val questions: ArrayList<Question> = getQuestionsList(quizId)!!
        return questions.any {
            it.id == questionId.toInt()
        }
    }

    suspend fun getQuestionsListSize(quizId: String): Int {
        return getQuestionsList(quizId)!!.size
    }


    suspend fun setQuestions(quizId: String, questions: ArrayList<Question>) {
        var quiz: Quiz? = null


    }

    private suspend fun getQuestionsList(quizId: String): ArrayList<Question>? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val userUid = auth.currentUser?.uid
                if (userUid != null) {
                    quizzesRef.child(userUid).child(quizId).child("questions")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val questionList: ArrayList<Question> =
                                    dataSnapshot.children.mapNotNull { it.getValue(Question::class.java) }
                                        .toCollection(ArrayList())
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

/*
https://quizcreatorapp-419412-default-rtdb.firebaseio.com/
----question
--------9EnQEZw3uvPutm5Pv8tTm49I5sA2
------------c5c91740-259d-452a-afa3-1c736547f8ea
----------------description:"A"
----------------id:"c5c91740-259d-452a-afa3-1c736547f8ea"
----------------questions
--------------------0
------------------------answers
----------------------------0:"a"
----------------------------1:b
----------------------------2:"c"
----------------------------3:"d"
------------------------correctAnswer:1
------------------------id:0
------------------------image:""
------------------------question:"1"
--------------------1
------------------------answers
----------------------------0:"a"
----------------------------1:b
----------------------------2:"c"
----------------------------3:"d"
------------------------correctAnswer:1
------------------------id:0
------------------------image:""
------------------------question:"1"
--------------------....
--------------------20
----------------title:"A"
----users
--------9EnQEZw3uvPutm5Pv8tTm49I5sA2
------------email:"sahakyansahak404@gmail.com"
------------profilePictureUrl:"https://lh3.googleusercontent.com/a/ACg8ocIwsjhzq5VNYGMSsP7_aywNOjI3LaOFgL4HCs2etf1gOtEnD5U=s96-c"
* */