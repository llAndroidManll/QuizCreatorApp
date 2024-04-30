package sahak.sahakyan.quizcreatorapp.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.repository.QuizRepository

class QuizViewModel(
    private val quizRepository: QuizRepository = QuizRepository()
) : ViewModel() {

    private val _questionCount = mutableIntStateOf(0)
    val questionCount: State<Int> = _questionCount

    private val _questionsSize = mutableIntStateOf(0)
    val questionsSize: State<Int> = _questionsSize

    private val _currentQuestion = mutableStateOf<Question>(Question())
    val currentQuestion: State<Question> = _currentQuestion

    private val _onPreviousStateChange = mutableStateOf(false)
    val onPreviousStateChange: State<Boolean> = _onPreviousStateChange

    suspend fun saveQuiz(quiz: Quiz) {
        quizRepository.saveQuiz(quiz)
    }

    suspend fun addQuestion(quizId: String, question: Question) {
        quizRepository.addQuestion(question, quizId)
        _questionsSize.intValue +=1
    }

    suspend fun getQuiz(quizId: String): Quiz? {
        return quizRepository.getQuiz(quizId)
    }

    suspend fun getQuestion(quizId: String, questionId: Int): Question? {
        return try {
            val question = quizRepository.getQuestion(quizId, questionId)
            if (question != null) {
                _currentQuestion.value = question
            }
            question
        } catch (e: NullPointerException) {
            Log.d("Quiz--QuizViewModel", "QuizViewModel: nullableQuestion is null")
            Question(id = questionCount.value)
        }
    }



    suspend fun updateQuestion(quizId: String,questionId: Int, question: Question) {
        quizRepository.updateQuestion(quizId, questionId, question)
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
        _questionCount.intValue = 0
        _questionsSize.intValue = 0
        _currentQuestion.value = Question()
        _onPreviousStateChange.value = false
    }
}
