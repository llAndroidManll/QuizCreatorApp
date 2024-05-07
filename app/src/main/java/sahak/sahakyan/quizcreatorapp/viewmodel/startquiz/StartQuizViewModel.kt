package sahak.sahakyan.quizcreatorapp.viewmodel.startquiz

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository

class StartQuizViewModel (
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {

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


    suspend fun getQuiz(quizId: String): Quiz? {
        val quiz = quizRepository.getQuiz(quizId)
        Log.d("Quiz--StartQuizViewModel", "getQuiz() Quiz: $quiz")
        return quiz
    }

    fun checkAnswer(questionIndex: Int, answerIndex: Int) {
        val question = _currentQuiz.value?.questions?.get(questionIndex)
        if(question!= null && question.correctAnswer == answerIndex) {
            incrementCorrectAnswersCount()
        }
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

    fun setDefaultValues() {
        _correctAnswersCount.intValue = 0
        _currentQuiz.value = null
        _questionCount.intValue = 0
        _questionsSize.intValue = 0
    }
}