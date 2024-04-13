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
    val question: String,
    val image: String? = null,
    val answers: List<String>,
    val correctAnswer: Int,
)