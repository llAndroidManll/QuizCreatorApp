package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.exception.CustomException
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository
import sahak.sahakyan.quizcreatorapp.sign_in.SignInState

class StartQuizViewModel (
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {


    private val _state = MutableStateFlow(StartQuizState())
    val state = _state.asStateFlow()

    private val _correctAnswersCount = mutableIntStateOf(0)
    val correctAnswersCount: State<Int> = _correctAnswersCount

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz

    private val _questionCount = mutableIntStateOf(0)
    val questionCount: State<Int> = _questionCount

    private val _questionsSize = mutableIntStateOf(
        _currentQuiz.value.let {
            it?.questions?.size ?: 0
        }
    )
    val questionsSize: State<Int> = _questionsSize

    private val _currentQuestion = MutableStateFlow<Question?>(
        _currentQuiz.value.let {
            it?.questions?.get(_questionCount.intValue)?: Question()
        }
    )
    val currentQuestion: StateFlow<Question?> = _currentQuestion


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

    fun checkAnswer(answerIndex: Int): Boolean {
        val question = _currentQuiz.value?.questions?.get(questionCount.value)
        if(question!= null && question.correctAnswer == answerIndex) {
            incrementCorrectAnswersCount()
            return true
        }
        return false
    }

    private fun incrementCorrectAnswersCount() {
        _correctAnswersCount.intValue++
    }


    fun setCurrentQuestion(question: Question) {
        _currentQuestion.value = question
    }

    fun incrementQuestionCount() {
        _questionCount.intValue++
    }

    fun decrementQuestionCount() {
        _questionCount.intValue--
    }

    fun setQuiz(quiz: Quiz) {
        _currentQuiz.value = quiz
        _questionsSize.intValue = quiz.questions.size
    }

    fun setQuestionsSize(size: Int) {
        _questionsSize.intValue = size
    }

    fun setDefaultValues() {
        _correctAnswersCount.intValue = 0
        _currentQuiz.value = null
        _questionCount.intValue = 0
        _questionsSize.intValue = 0
    }
}