package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository

class HomeViewModel(
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeViewState())
    val state = _state.asStateFlow()

    private val _quizzes = mutableStateOf<List<Quiz>>(emptyList())
    val quizzes: State<List<Quiz>> = _quizzes

    suspend fun getQuizzes(): List<Quiz>? {
        return try {
            _state.update {
                it.copy(isLoading = true)
            }
            val lists = quizRepository.getQuizzes()
            _state.update{
                it.copy(isLoading = false)
            }
            lists
        } catch (e: RuntimeException) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
            Log.d("Quiz--HomeViewModel", "HomeViewModel: QuizList is empty")
            return emptyList()
        }
    }

    fun setQuizzes(quizzes: List<Quiz>) {
        _quizzes.value = quizzes
    }
}