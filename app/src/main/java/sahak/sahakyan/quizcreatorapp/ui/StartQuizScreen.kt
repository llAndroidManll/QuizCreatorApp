package sahak.sahakyan.quizcreatorapp.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import sahak.sahakyan.quizcreatorapp.R
import sahak.sahakyan.quizcreatorapp.viewmodel.startquiz.StartQuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest


@Composable
fun StartQuizScreen(
    viewModel: StartQuizViewModel = viewModel(),
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onFinishClick: () -> Unit = {}
) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    Log.d("Quiz--StartQuizScreen", "currentQuestion: $currentQuestion")
    var questionValue by remember { mutableStateOf(currentQuestion!!.question) }
    var selectedImageCoin by remember { mutableStateOf(currentQuestion!!.image ?: "") }
    var answer1 by remember { mutableStateOf(currentQuestion!!.answers.getOrNull(0) ?: "") }
    var answer2 by remember { mutableStateOf(currentQuestion!!.answers.getOrNull(1) ?: "") }
    var answer3 by remember { mutableStateOf(currentQuestion!!.answers.getOrNull(2) ?: "") }
    var answer4 by remember { mutableStateOf(currentQuestion!!.answers.getOrNull(3) ?: "") }
    var selectedOption by remember { mutableStateOf<String>("") }
    val snackBarVisibleState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel.currentQuestion) {
        if(currentQuestion==null) {
            Log.d("Quiz--StartQuizScreen", "currentQuestion is null")
        } else {
            viewModel.currentQuestion.collectLatest { question ->
                questionValue = question!!.question
                selectedImageCoin = question.image ?: ""
                answer1 = question.answers.getOrNull(0) ?: ""
                answer2 = question.answers.getOrNull(1) ?: ""
                answer3 = question.answers.getOrNull(2) ?: ""
                answer4 = question.answers.getOrNull(3) ?: ""
                selectedOption = ""
            }
        }
    }

    Log.d("Quiz--StartQuizScreen", "questionValue: $questionValue")
    Log.d("Quiz--StartQuizScreen", "selectedImageCoin: $selectedImageCoin")
    Log.d("Quiz--StartQuizScreen", "answer1: $answer1")
    Log.d("Quiz--StartQuizScreen", "answer2: $answer2")
    Log.d("Quiz--StartQuizScreen", "answer3: $answer3")
    Log.d("Quiz--StartQuizScreen", "answer4: $answer4")

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gradient1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // Question Text
            Text(
                text = questionValue,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                )
            )

            // Image
            AsyncImage(
                model = selectedImageCoin,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            // Items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ItemsInColumn(
                    selected = selectedOption == answer1,
                    onClick = {
                        if (answer1 == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = answer1
                        }
                    },
                    text = answer1,
                )
                ItemsInColumn(
                    selected = selectedOption == answer2,
                    onClick = {
                        if (answer2 == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = answer2
                        }
                    },
                    text = answer2,
                )
                ItemsInColumn(
                    selected = selectedOption == answer3,
                    onClick = {
                        if (answer3 == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = answer3
                        }
                    },
                    text = answer3,
                )
                ItemsInColumn(
                    selected = selectedOption == answer4,
                    onClick = {
                        if (answer4 == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = answer4
                        }
                    },
                    text = answer4,
                )
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel.questionCount.value > 0) {
                    ButtonStyle(
                        text = "Previous Question",
                        onClick = {
                            viewModel.decrementQuestionCount()
                            onPreviousClick()
                        },
                        modifier = Modifier.height(50.dp)
                    )
                }
                if (viewModel.questionCount.value < viewModel.questionsSize.value - 1) {
                    ButtonStyle(
                        text = "Next Question",
                        onClick = {
                            viewModel.checkAnswer(
                                questionIndex = viewModel.currentQuiz.value!!.questions.indexOf(
                                    currentQuestion
                                ),
                                answerIndex = currentQuestion!!.answers.indexOf(selectedOption)
                            )
                            viewModel.incrementQuestionCount()

                            onNextClick()
                        },
                        modifier = Modifier.height(50.dp)
                    )
                } else {
                    ButtonStyle(
                        text = "Finish",
                        onClick = {
                            viewModel.checkAnswer(
                                questionIndex = viewModel.currentQuiz.value!!.questions.indexOf(
                                    currentQuestion
                                ),
                                answerIndex = currentQuestion!!.answers.indexOf(selectedOption)
                            )
                            onFinishClick()
                        },
                        modifier = Modifier.height(50.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun ItemsInColumn(
    selected: Boolean = false,
    text: String = "",
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier.padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(1.dp, Color.White),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = text,
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.Gray,
                )
            )
        }
    }
}