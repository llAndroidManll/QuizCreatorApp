package sahak.sahakyan.quizcreatorapp.viewmodel

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
        return quizRepository.getQuizzes()
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        _quizzes.value = quizzes
    }
}