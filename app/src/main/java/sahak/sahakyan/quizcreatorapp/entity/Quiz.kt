package sahak.sahakyan.quizcreatorapp.entity



data class Quiz (
    val id: String = "",
    val title: String = "",
    val questionsSize: Int = 0,
    val questions: ArrayList<Question> = emptyArrayList(),
)

data class Question(
    var id: Int = -1,
    var question: String = "",
    var image: String? = null,
    var answers: MutableList<String> = ArrayList(),
    var correctAnswer: Int = 0,
)

private fun <T> emptyArrayList():ArrayList<T> {
    return ArrayList<T>()
}