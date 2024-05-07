package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository

class HomeViewModel(
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _quizzes = mutableStateOf<List<Quiz>>(emptyList())
    val quizzes: State<List<Quiz>> = _quizzes

    suspend fun getQuizzes(): List<Quiz>? {
        return try {
            quizRepository.getQuizzes()
        } catch (e: IllegalArgumentException) {
            Log.d("Quiz--HomeViewModel", "HomeViewModel: QuizList is empty")
            return emptyList()
        }
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        _quizzes.value = quizzes
    }
}