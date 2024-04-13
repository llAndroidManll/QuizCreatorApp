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
}
