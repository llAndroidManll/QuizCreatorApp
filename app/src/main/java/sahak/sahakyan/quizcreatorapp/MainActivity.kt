package sahak.sahakyan.quizcreatorapp

import android.os.Bundle
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch
import sahak.sahakyan.quizcreatorapp.repository.UserRepository
import sahak.sahakyan.quizcreatorapp.navigation.BottomBar
import sahak.sahakyan.quizcreatorapp.navigation.NavigationScreens
import sahak.sahakyan.quizcreatorapp.ui.HomeScreen
import sahak.sahakyan.quizcreatorapp.ui.ProfileScreen
import sahak.sahakyan.quizcreatorapp.sign_in.GoogleAuthUiClient
import sahak.sahakyan.quizcreatorapp.sign_in.SignInScreen
import sahak.sahakyan.quizcreatorapp.ui.QuizCreatorScreen
import sahak.sahakyan.quizcreatorapp.viewmodel.SignInViewModel
import sahak.sahakyan.quizcreatorapp.ui.theme.QuizCreatorAppTheme

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val userRepo: UserRepository = UserRepository()




    override fun onCreate(savedInstanceState: Bundle?) {

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


                                    userRepo.createUser()

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
                            HomeScreen(
                                navController = navController,
                                onAddClick = {
                                    navController.navigate(NavigationScreens.QuizCreator.route)
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
                        composable(NavigationScreens.QuizCreator.route) {
                            QuizCreatorScreen()
                        }
                    }
                }
            }
        }
    }
}