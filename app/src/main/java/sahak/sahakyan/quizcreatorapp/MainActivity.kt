package sahak.sahakyan.quizcreatorapp

import android.annotation.SuppressLint
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
import sahak.sahakyan.quizcreatorapp.navigation.BottomBar
import sahak.sahakyan.quizcreatorapp.navigation.NavigationScreens
import sahak.sahakyan.quizcreatorapp.sign_in.GoogleAuthUiClient
import sahak.sahakyan.quizcreatorapp.sign_in.SignInScreen
import sahak.sahakyan.quizcreatorapp.ui.CreateQuizScreen
import sahak.sahakyan.quizcreatorapp.ui.HomeScreen
import sahak.sahakyan.quizcreatorapp.ui.ProfileScreen
import sahak.sahakyan.quizcreatorapp.ui.QuizCreatorScreen
import sahak.sahakyan.quizcreatorapp.ui.QuizResultScreen
import sahak.sahakyan.quizcreatorapp.ui.StartQuizScreen
import sahak.sahakyan.quizcreatorapp.ui.theme.QuizCreatorAppTheme
import sahak.sahakyan.quizcreatorapp.viewmodel.HomeViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.QuizViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.SignInViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.UserViewModel
import sahak.sahakyan.quizcreatorapp.viewmodel.StartQuizViewModel

class MainActivity : ComponentActivity() {

    private lateinit var googleAuthUiClient: GoogleAuthUiClient
    private lateinit var userViewModel: UserViewModel
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var quizViewModel: QuizViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var startQuizViewModel: StartQuizViewModel

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {

        googleAuthUiClient = GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )


        signInViewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        startQuizViewModel = ViewModelProvider(this)[StartQuizViewModel::class.java]

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
                        composable(BottomBar.Home.route) {
                            LaunchedEffect(key1 = true) {
                                lifecycleScope.launch {
                                    homeViewModel.setQuizzes(
                                        homeViewModel.getQuizzes()!!
                                    )
                                }
                            }
                            HomeScreen(
                                viewModel = homeViewModel,
                                navController = navController,
                                onAddClick = {
                                    navController.navigate(NavigationScreens.CreateQuiz.route)
                                },
                                onRunButton = {id ->
                                    lifecycleScope.launch {
                                        val bool = quizViewModel.isQuizFinished(id)
                                        if (bool) {
                                            startQuizViewModel.getQuiz(id)?.let { it1 ->
                                                startQuizViewModel.setQuiz(it1)
//                                                startQuizViewModel.setQuestionsSize(it1.questionsSize)
                                            }
//                                            Log.d("Quiz--Composable--Home","Starting Quiz with quiz: ${startQuizViewModel.currentQuiz.value}")
                                            navController.navigate(NavigationScreens.StartQuiz.route + "/$id")
                                        } else {
//                                            Log.d("Quiz--Composable--Home","Navigating to Home Page")
                                            navController.navigate(BottomBar.Home.route)
                                        }


                                    }
                                },
                                onEditButton = {id ->
                                    lifecycleScope.launch {
                                        if (quizViewModel.isQuizFinished(id)) {
                                            quizViewModel.setQuizFinishedState(true)
                                            quizViewModel.getQuestion(id,quizViewModel.questionCount.value)?.let { quizViewModel.setCurrentQuestion(it) }
                                            quizViewModel.setOnPreviousStateChange(true)
                                            quizViewModel.setQuestionsSize(quizViewModel.getQuiz(id)!!.questionsSize)
                                            Log.d("Quiz--Composable--QuizCreator","QuizViewModel Questions Size: ${quizViewModel.questionsSize.value}")
                                        }
                                        navController.navigate(NavigationScreens.QuizCreator.route + "/$id")
                                    }
                                }
                            )
                        }
                        composable(NavigationScreens.QuizCreator.route + "/{quizId}") { backStackEntry ->
                            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                            if (!quizViewModel.isQuizFinished.value && quizViewModel.questionsSize.value == quizViewModel.questionCount.value) {
                                Log.i("Quiz--Composable--QuizCreator","setting OnPreviousStateChange to false")
                                quizViewModel.setOnPreviousStateChange(false)
                            }
                            QuizCreatorScreen(
                                viewModel = quizViewModel,
                                onNextClick = { question ->
                                    if(quizViewModel.onPreviousStateChange.value) {
                                        lifecycleScope.launch {
                                            Log.i("Quiz--Composable--QuizCreator","Updating question with id: ${quizViewModel.questionCount.value}")
                                            quizViewModel.updateQuestion(
                                                question = question,
                                                quizId = quizId,
                                                questionId = quizViewModel.questionCount.value
                                            )
                                            quizViewModel.incrementQuestionCount()
                                            Log.i("Quiz--Composable--QuizCreator","QuizViewModel Question Count: ${quizViewModel.questionCount.value}")
                                            val nullableQuestion: Question = quizViewModel.getQuestion(quizId, quizViewModel.questionCount.value)!!
                                            quizViewModel.setCurrentQuestion(nullableQuestion)
                                        }
                                    } else {
                                        Log.d("Quiz--Composable--QuizCreator", "QuizViewModel adding new question")
                                        question.id = quizViewModel.questionCount.value
                                        lifecycleScope.launch {
                                            quizViewModel.addQuestion(
                                                question = question,
                                                quizId = quizId
                                            )
                                        }
                                        quizViewModel.incrementQuestionCount()
                                        quizViewModel.setCurrentQuestion(Question())
                                    }
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
                                onFinishClick = { question ->
                                    lifecycleScope.launch {
                                        if(quizViewModel.isQuizFinished.value) {
                                            quizViewModel.updateQuestion(
                                                question = question,
                                                quizId = quizId,
                                                questionId = quizViewModel.questionCount.value
                                            )
                                        } else {
                                            question.id = quizViewModel.questionCount.value
                                            quizViewModel.addQuestion(
                                                question = question,
                                                quizId = quizId
                                            )
                                            quizViewModel.setQuizFinished(quizId)
                                        }
                                    }
                                    quizViewModel.setDefaultValues()
                                    navController.navigate(BottomBar.Home.route)
                                }
                            )
                        }
                        composable(NavigationScreens.CreateQuiz.route) {
                            CreateQuizScreen(
                                onNextStepClick = { title, number ->
                                    val quizId = quizViewModel.generateId()
                                    val quiz = Quiz(id = quizId, title = title, questionsSize = number, ArrayList<Question>(number))
                                    lifecycleScope.launch {
                                        quizViewModel.saveQuiz(quiz = quiz)
                                        quizViewModel.setQuestionsSize(number)
                                    }
                                    navController.navigate(NavigationScreens.QuizCreator.route + "/$quizId")
                                },
                            )
                        }
                        composable(NavigationScreens.StartQuiz.route + "/{quizId}") { backStackEntry ->
                            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""

                            Log.d("Quiz--Composable--StartQuiz", "StartViewModel quiz: ${startQuizViewModel.currentQuiz.value}")

                            val quiz = startQuizViewModel.currentQuiz.value

                            startQuizViewModel.setCurrentQuestion(quiz!!.questions[startQuizViewModel.questionCount.value])

                            Log.i("Quiz--Composable--StartQuiz", "quiz field: $quiz")
                            StartQuizScreen(
                                viewModel = startQuizViewModel,
                                onNextClick = {
                                    navController.navigate(NavigationScreens.StartQuiz.route + "/$quizId")
                                },
                                onCheckClick = {

                                },
                                onFinishClick = {
                                    navController.navigate(NavigationScreens.QuizResult.route)
                                }
                            )
                        }
                        composable(NavigationScreens.QuizResult.route) {
                            QuizResultScreen(
                                viewModel = startQuizViewModel,
                                onGoHomeClick = {
                                    navController.navigate(BottomBar.Home.route)
                                    startQuizViewModel.setDefaultValues()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}