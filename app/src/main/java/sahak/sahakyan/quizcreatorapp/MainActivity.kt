package sahak.sahakyan.quizcreatorapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.entity.Quiz
import sahak.sahakyan.quizcreatorapp.entity.Quizzes
import sahak.sahakyan.quizcreatorapp.navigation.BottomBar
import sahak.sahakyan.quizcreatorapp.navigation.NavigationScreens
import sahak.sahakyan.quizcreatorapp.sign_in.GoogleAuthUiClient
import sahak.sahakyan.quizcreatorapp.sign_in.SignInScreen
import sahak.sahakyan.quizcreatorapp.ui.CreateQuizScreen
import sahak.sahakyan.quizcreatorapp.ui.HomeScreen
import sahak.sahakyan.quizcreatorapp.ui.ProfileScreen
import sahak.sahakyan.quizcreatorapp.ui.QuizCreatorScreen
import sahak.sahakyan.quizcreatorapp.ui.theme.QuizCreatorAppTheme
import sahak.sahakyan.quizcreatorapp.viewmodel.QuizViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.SignInViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    private lateinit var userViewModel: UserViewModel
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        googleAuthUiClient = GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )


        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)



        setContent {
            QuizCreatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = NavigationScreens.SignIn.route) {
                        composable(NavigationScreens.SignIn.route) {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate(BottomBar.Home.route)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if(result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if(state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()


                                    userViewModel.createUser()

                                    navController.navigate(BottomBar.Home.route)
                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
                        composable(BottomBar.Home.route) {

                            fun getQuizData(): Quizzes {
                                var userEmail: String = ""
                                var quizzesList: List<Quiz> = emptyList()
                                lifecycleScope.launch {
                                    userEmail = userViewModel.getCurrentUser().email ?: ""
                                    quizzesList = quizViewModel.getQuizzes() ?: emptyList()
                                }
                                return Quizzes(userEmail = userEmail, quizzes = quizzesList)
                            }
                            val quizData = getQuizData()
                            HomeScreen(
                                navController = navController,
                                quizData = quizData,
                                onAddClick = {
                                    navController.navigate(NavigationScreens.CreateQuiz.route)
                                }
                            )
                        }
                        composable(BottomBar.Profile.route) {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate(NavigationScreens.SignIn.route)
                                    }
                                }
                            )
                        }
                        composable(NavigationScreens.QuizCreator.route + "/{quizId}") { backStackEntry ->
                            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""


                            Log.d("Quiz--MainActivity", "QuestionsListSize is: ${quizViewModel.questionsSize.value}")
                            Log.d("Quiz--MainActivity", "QuestionCount is: ${quizViewModel.questionCount.value}")
                            if (quizViewModel.questionsSize.value == quizViewModel.questionCount.value) {
                                Log.d("Quiz--MainActivity","setting OnPreviousState to false")
                                quizViewModel.setOnPreviousStateChange(false)
                            }

                            QuizCreatorScreen(
                                viewModel = quizViewModel,
                                onNextClick = { question ->
                                    Log.d("Quiz--MainActivity", "quizViewModel.onPreviousStateChange.value is: ${quizViewModel.onPreviousStateChange.value}")
                                    if(quizViewModel.onPreviousStateChange.value) {
                                        Log.d("Quiz--MainActivity", "quizViewModel.onPreviousStateChange.value is: true")
                                        lifecycleScope.launch {
                                            quizViewModel.updateQuestion(
                                                question = question,
                                                quizId = quizId,
                                                questionId = quizViewModel.questionCount.value
                                            )
                                            Log.d("Quiz--MainActivity", "Question before nullableQuestion is : ${quizViewModel.currentQuestion.value}")
                                            val nullableQuestion: Question = quizViewModel.getQuestion(quizId, quizViewModel.questionCount.value)!!
                                            Log.d("Quiz--MainActivity", "Question after nullableQuestion is : ${quizViewModel.currentQuestion.value}")
                                            quizViewModel.setCurrentQuestion(nullableQuestion)
                                        }
                                    } else {
                                        Log.d("Quiz--MainActivity", "quizViewModel.onPreviousStateChange.value is: false")
                                        question.id = quizViewModel.questionCount.value
                                        lifecycleScope.launch {
                                            quizViewModel.addQuestion(
                                                question = question,
                                                quizId = quizId
                                            )
                                        }
                                        quizViewModel.setCurrentQuestion(Question())
                                    }
                                    Log.i("Quiz--MainActivity", "Navigating to destination ID: NavigationScreens.QuizCreator.route")
                                    navController.navigate(NavigationScreens.QuizCreator.route + "/$quizId")
                                },
                                onPreviousClick = {
                                    quizViewModel.setOnPreviousStateChange(true)
                                    lifecycleScope.launch {
                                        quizViewModel.getQuestion(
                                            quizId = quizId,
                                            questionId = quizViewModel.questionCount.value
                                        )
                                    }
                                    navController.navigate(NavigationScreens.QuizCreator.route + "/$quizId")
                                },
                            )
                        }
                        composable(NavigationScreens.CreateQuiz.route) {
                            CreateQuizScreen(
                                onNextStepClick = { title, description ->
                                    val quizId = quizViewModel.generateId()
                                    val quiz = Quiz(id = quizId, title = title, description = description, ArrayList<Question>())
                                    lifecycleScope.launch {
                                        quizViewModel.saveQuiz(quiz = quiz)
                                    }
                                    navController.navigate(NavigationScreens.QuizCreator.route + "/$quizId")
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}