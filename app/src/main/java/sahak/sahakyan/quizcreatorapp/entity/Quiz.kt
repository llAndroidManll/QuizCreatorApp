package sahak.sahakyan.quizcreatorapp.entity


data class Quizzes(
    val userEmail: String,
    val quizzes: List<Quiz>
)

data class Quiz (
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question>,
)
data class Question(
    val id: String,
    var question: String = "",
    var image: String? = null,
    var answers: List<String> = emptyList(),
    var correctAnswer: Int = 0,
)