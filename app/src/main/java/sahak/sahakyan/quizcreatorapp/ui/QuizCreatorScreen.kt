@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package sahak.sahakyan.quizcreatorapp.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import sahak.sahakyan.quizcreatorapp.R
import sahak.sahakyan.quizcreatorapp.entity.Question
import sahak.sahakyan.quizcreatorapp.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun QuizCreatorScreen(
    viewModel: QuizViewModel,
    onPreviousClick: () -> Unit,
    onNextClick: (Question) -> Unit = {},
    onFinishClick: (Question) -> Unit = {},
) {
    val currentQuestion by viewModel.currentQuestion


    var questionValue by remember { mutableStateOf(currentQuestion.question) }
    var selectedImageCoin by remember { mutableStateOf(currentQuestion.image ?: "") }
    var answer1 by remember { mutableStateOf(currentQuestion.answers.getOrNull(0) ?: "") }
    var answer2 by remember { mutableStateOf(currentQuestion.answers.getOrNull(1) ?: "") }
    var answer3 by remember { mutableStateOf(currentQuestion.answers.getOrNull(2) ?: "") }
    var answer4 by remember { mutableStateOf(currentQuestion.answers.getOrNull(3) ?: "") }
    var selectedOption by remember { mutableStateOf<String?>(currentQuestion.answers.getOrNull(currentQuestion.correctAnswer))}


    LaunchedEffect(currentQuestion) {
        questionValue = currentQuestion.question
        selectedImageCoin = currentQuestion.image ?: ""
        answer1 = currentQuestion.answers.getOrNull(0) ?: ""
        answer2 = currentQuestion.answers.getOrNull(1) ?: ""
        answer3 = currentQuestion.answers.getOrNull(2) ?: ""
        answer4 = currentQuestion.answers.getOrNull(3) ?: ""
        selectedOption = currentQuestion.answers.getOrNull(currentQuestion.correctAnswer)
    }



    val questionCount by viewModel.questionCount

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageCoin = uri.toString() }
    )

    val context = LocalContext.current
    val snackBarVisibleState = remember { mutableStateOf(false) }

    val showDialog = remember { mutableStateOf(false) }
    val showOptionDialog = remember { mutableStateOf(false) }
    val showAnswersDuplicationDialog = remember { mutableStateOf(false) }

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

            Text(
                text = "${viewModel.questionCount.value + 1}/ ${viewModel.questionsSize.value}",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                )
            )

            OutlinedTextField(
                value = questionValue,
                onValueChange = { questionValue = it },
                placeHolder = "Enter your question here",
            )
            // Image Picker
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, bottom = 10.dp)
                    .height(200.dp)
                    .background(Color.Transparent),
            ) {
                if (selectedImageCoin.isEmpty() || selectedImageCoin == "null") {
                    Button(
                        onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        shape = RoundedCornerShape(0),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent
                        ),

                        ) {
                        Text(text = "Pick Image")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    AsyncImage(
                        model = selectedImageCoin,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ItemInColumn(
                    selected = selectedOption == answer1,
                    onClick = {
                        if (answer1 == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = answer1
                        }
                    },
                    answer = answer1,
                    onAnswerChange = {
                        answer1 = it
                    }
                )
                ItemInColumn(
                    selected = selectedOption == answer2,
                    onClick = {
                        selectedOption = answer2
                    },
                    answer = answer2,
                    onAnswerChange = {
                        answer2 = it
                    }
                )
                ItemInColumn(
                    selected = selectedOption == answer3,
                    onClick = {
                        selectedOption = answer3
                    },
                    answer = answer3,
                    onAnswerChange = {
                        answer3 = it
                    }
                )
                ItemInColumn(
                    selected = selectedOption == answer4,
                    onClick = {
                        selectedOption = answer4
                    },
                    answer = answer4,
                    onAnswerChange = {
                        answer4 = it
                    }
                )
                if (snackBarVisibleState.value) {
                    Snackbar(
                        action = {
                            Button(onClick = { snackBarVisibleState.value = false }) {
                                Text(text = "Dismiss")
                            }
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "You need to enter Text")
                    }
                }
            }

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (questionCount > 0) {
                    ButtonStyle(
                        text = "Previous Question",
                        onClick = {
                            viewModel.decrementQuestionCount()
                            onPreviousClick()
                        },
                        modifier = Modifier.height(50.dp)
                    )
                }
                if (questionCount < viewModel.questionsSize.value-1) {
                    ButtonStyle(
                        text = "Next Question",
                        onClick = {
                            val answers = mutableListOf(answer1, answer2, answer3, answer4)
                            showDialog.value = listOf(
                                questionValue.isEmpty(),
                                answer1.isEmpty(),
                                answer2.isEmpty(),
                                answer3.isEmpty(),
                                answer4.isEmpty(),
                            ).any { it }
                            if (selectedOption == null) {
                                showOptionDialog.value = true
                            }
                            showAnswersDuplicationDialog.value = checkForDuplicates(answers = answers)

                            if(!showDialog.value && !showOptionDialog.value && !showAnswersDuplicationDialog.value) {
                                viewModel.currentQuestion.value
                                    .answers = answers
                                viewModel.currentQuestion.value
                                    .image = selectedImageCoin
                                viewModel.currentQuestion.value
                                    .question = questionValue
                                viewModel.currentQuestion.value
                                    .correctAnswer = viewModel.currentQuestion.value
                                    .answers.indexOf(selectedOption)
                                onNextClick(
                                    viewModel.currentQuestion.value
                                )
                            }
                        },
                        modifier = Modifier.height(50.dp)
                    )
                } else {
                    ButtonStyle(
                        text = "Finish",
                        onClick = {
                            val answers = mutableListOf(answer1, answer2, answer3, answer4)
                            showDialog.value = listOf(
                                questionValue.isEmpty(),
                                answer1.isEmpty(),
                                answer2.isEmpty(),
                                answer3.isEmpty(),
                                answer4.isEmpty(),
                            ).any { it }
                            if (selectedOption == null) {
                                showOptionDialog.value = true
                            }
                            showAnswersDuplicationDialog.value = checkForDuplicates(answers = answers)

                            if(!showDialog.value && !showOptionDialog.value && !showAnswersDuplicationDialog.value) {
                                viewModel.currentQuestion.value
                                    .answers = answers
                                viewModel.currentQuestion.value
                                    .image = selectedImageCoin
                                viewModel.currentQuestion.value
                                    .question = questionValue
                                viewModel.currentQuestion.value
                                    .correctAnswer = viewModel.currentQuestion.value
                                    .answers.indexOf(selectedOption)
                                onFinishClick(
                                    viewModel.currentQuestion.value
                                )
                            }
                        },
                        modifier = Modifier.height(50.dp)
                    )
                }

            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Empty Fields") },
            text = { Text(text = "Please fill in all fields before saving.") },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "OK")
                }
            }
        )
    }
    if (showOptionDialog.value) {
        AlertDialog(
            onDismissRequest = { showOptionDialog.value = false },
            title = { Text(text = "Option is not chosen") },
            text = { Text(text = "Please choose true answer before saving.") },
            confirmButton = {
                Button(onClick = { showOptionDialog.value = false }) {
                    Text(text = "OK")
                }
            }
        )
    }
    if (showAnswersDuplicationDialog.value) {
        AlertDialog(
            onDismissRequest = { showAnswersDuplicationDialog.value = false },
            title = { Text(text = "Multiple True Answers") },
            text = { Text(text = "Multiple True Answers is not allowed") },
            confirmButton = {
                Button(onClick = { showAnswersDuplicationDialog.value = false }) {
                    Text(text = "OK")
                }
            }
        )
    }
}

@Composable
fun ItemInColumn(
    selected: Boolean = false,
    onClick: () -> Unit = {},
    answer: String,
    onAnswerChange: (String) -> Unit = {},
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
            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChange,
                placeHolder = "Enter your answer here",
                columnModifier = Modifier.width(300.dp)
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

fun setAnswers(list: List<String>, index: Int): String {
    return if (list.isEmpty()) {
        ""
    } else {
        list[index]
    }
}

fun checkForDuplicates(answers: List<String>): Boolean {
    val distinctAnswers = answers.distinct()
    return answers.size != distinctAnswers.size
}


@Composable
fun CustomLinearProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    progressColor: Color = Color.White,
    backgroundColor: Color = Color.Transparent,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(progressColor)
                .fillMaxHeight()
                .fillMaxWidth(progress)
        )
    }
}