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
import sahak.sahakyan.quizcreatorapp.exception.CustomException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class QuizRepository {

    private val database = Firebase.database
    private val auth = FirebaseAuth.getInstance()
    private val quizzesRef = database.getReference("quizzes")


    suspend fun deleteQuiz(quizId: String) {
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).removeValue().addOnFailureListener {
            throw CustomException(it.message.toString())
        }
    }

    suspend fun saveQuiz(quiz: Quiz) {
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quiz.id).setValue(quiz)
    }
    fun generateId() = UUID.randomUUID().toString()

    suspend fun setQuizFinished(quizId: String) {
        val quiz = getQuiz(quizId)
        quiz.isFinished = true
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("finished")
            .setValue(quiz.isFinished)
    }

    suspend fun isQuizFinished(quizId: String): Boolean {
        return suspendCoroutine { continuation ->
            quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("finished").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        continuation.resume(dataSnapshot.value as Boolean)
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        continuation.resume(false)
                    }
                }
            )
        }
    }

    // Done
    suspend fun addQuestion(question: Question, quizId: String) {
        Log.d("Quiz--Repository", "addQuestion(): Question: $question")
        val quiz = getQuiz(quizId)
        quiz.questions.add(question)
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions")
            .setValue(quiz.questions).addOnFailureListener {
                throw CustomException(it.message.toString())
            }
    }

    // Done
    suspend fun updateQuestion(quizId: String, questionId: Int, question: Question) {
        quizzesRef.child(auth.currentUser?.uid.toString()).child(quizId).child("questions").child(questionId.toString())
        .addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.ref.setValue(question)
                    Log.d("Quiz--Repository", "updateQuestion(): Question with Id: $questionId")
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    throw CustomException(databaseError.message)
                }
            }
        )
    }

    // Done
    suspend fun getQuiz(quizId: String): Quiz {
        return suspendCoroutine { continuation ->
            val userUid = auth.currentUser?.uid
            if (userUid != null) {
                quizzesRef.child(userUid).child(quizId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val quiz = dataSnapshot.getValue(Quiz::class.java)
                            if(quiz != null) {
                                continuation.resume(quiz)
                                Log.d("Quiz--Repository", "getQuiz(): Quiz: $quiz")
                            } else {
                                continuation.resumeWithException(CustomException("Quiz has been found but it is null"))
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            continuation.resumeWithException(CustomException("Quiz cannot be found because of $databaseError"))
                        }
                    })
            } else {
                continuation.resumeWithException(CustomException("Quiz cannot be found"))
            }
        }
    }

    // Done
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

    // Done
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
                            continuation.resumeWithException(CustomException(databaseError.message))
                        }
                    })
            } else {
                continuation.resumeWithException(CustomException("Quizzes cannot be found"))
            }
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

/*.addListenerForSingleValueEvent(
    object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot.ref.setValue(question)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("Quiz--Repository", "updateQuestion(): onCancelled")
        }
    }
)*/