package sahak.sahakyan.quizcreatorapp.entity


data class Quizzes(
    val userEmail: String,
    val quizzes: List<Quiz>
)

data class Quiz (
    val id: String,
    val title: String,
    val description: String,
    val questions: ArrayList<Question>,
)
data class Question(
    val id: Int,
    var question: String = "",
    var image: String? = null,
    var answers: MutableList<String> = ArrayList(),
    var correctAnswer: Int = 0,
)