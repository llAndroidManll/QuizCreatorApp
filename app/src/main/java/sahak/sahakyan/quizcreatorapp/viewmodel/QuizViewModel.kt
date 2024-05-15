package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.exception.CustomException
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository

class QuizViewModel(
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(CreateQuizState())
    val state = _state.asStateFlow()

    private val _questionCount = mutableIntStateOf(0)
    val questionCount: State<Int> = _questionCount

    private val _questionsSize = mutableIntStateOf(0)
    val questionsSize: State<Int> = _questionsSize


    private val _currentQuestion = mutableStateOf<Question>(Question())
    val currentQuestion: State<Question> = _currentQuestion

    private val _onPreviousStateChange = mutableStateOf(false)
    val onPreviousStateChange: State<Boolean> = _onPreviousStateChange

    private val _isQuizFinished = mutableStateOf(false)
    val isQuizFinished: State<Boolean> = _isQuizFinished

    suspend fun saveQuiz(quiz: Quiz) {
        quizRepository.saveQuiz(quiz)
    }

    // Done
    suspend fun addQuestion(quizId: String, question: Question) {
        try {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            quizRepository.addQuestion(question, quizId)
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        } catch (e: CustomException) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // Done
    suspend fun getQuiz(quizId: String): Quiz {
        try {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val quiz = quizRepository.getQuiz(quizId)
            Log.d("Quiz--StartQuizViewModel", "getQuiz() Quiz: $quiz")
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
            return quiz
        } catch (e: CustomException) {
            Log.e("Quiz--StartQuizViewModel", "getQuiz() Error: ${e.message}", e)
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
            return Quiz(
                id = "-1"
            )
        }
    }

    // Done
    suspend fun getQuestion(quizId: String, questionId: Int): Question? {
        return try {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val question = quizRepository.getQuestion(quizId, questionId)
            if (question != null) {
                _currentQuestion.value = question
            }
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
            question
        } catch (e: NullPointerException) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
            Log.d("Quiz--QuizViewModel", "QuizViewModel: nullableQuestion is null")
            Question(id = questionCount.value)
        }
    }

    suspend fun setQuizFinished(quizId: String) {
        quizRepository.setQuizFinished(quizId)
    }

    suspend fun deleteQuiz(quizId: String) {
        try {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            quizRepository.deleteQuiz(quizId)
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        } catch (e: CustomException) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    suspend fun isQuizFinished(quizId: String): Boolean {
        return quizRepository.isQuizFinished(quizId)
    }

    fun setQuizFinishedState(boolean: Boolean) {
        _isQuizFinished.value = boolean
    }


    suspend fun updateQuestion(quizId: String,questionId: Int, question: Question) {
        try {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            quizRepository.updateQuestion(quizId, questionId, question)
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        } catch (e: CustomException) {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    suspend fun isQuestionExistWithId(quizId: String,questionId: String) : Boolean {
        return quizRepository.isQuestionExistWithId(quizId, questionId)
    }
    suspend fun getQuestionsListSize(quizId: String) : Int {
        return quizRepository.getQuestionsListSize(quizId)
    }
    fun generateId() = quizRepository.generateId()

    fun incrementQuestionCount() {
        _questionCount.intValue++
    }

    fun decrementQuestionCount() {
        _questionCount.intValue--
    }

    fun setCurrentQuestion(question: Question) {
        _currentQuestion.value = question
    }

    fun setOnPreviousStateChange(onPreviousStateChange: Boolean) {
        _onPreviousStateChange.value = onPreviousStateChange
    }

    fun setQuestionsSize(questionsSize: Int) {
        _questionsSize.intValue = questionsSize
    }

    fun setDefaultValues() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        _questionCount.intValue = 0
        _questionsSize.intValue = 0
        _currentQuestion.value = Question()
        _onPreviousStateChange.value = false
        _isQuizFinished.value = false
        _state.update {
            it.copy(
                isLoading = false,
                error = null
            )
        }
    }
}
