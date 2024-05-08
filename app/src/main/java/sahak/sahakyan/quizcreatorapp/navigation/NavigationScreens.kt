package sahak.sahakyan.quizcreatorapp.navigation

sealed class  NavigationScreens(
    val route: String
) {
    data object SignIn: NavigationScreens(
        route = "sign_in"
    )
    data object QuizCreator: NavigationScreens(
        route = "quiz_creator"
    )
    data object CreateQuiz: NavigationScreens(
        route = "quiz_create"
    )
    data object StartQuiz: NavigationScreens(
        route = "quiz_start"
    )
    data object QuizResult: NavigationScreens(
        route = "quiz_result"
    )
}
