@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package sahak.sahakyan.quizcreatorapp.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import sahak.sahakyan.quizcreatorapp.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import sahak.sahakyan.quizcreatorapp.entity.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreatorScreen(
    quizId: String = "",
    onPreviousClick: () -> Unit = {},
    onNextClick: (Question) -> Unit = {},
    onFinishClick: () -> Unit = {},
) {

    var questionCount: Int by remember { mutableIntStateOf(1) }

    val question: Question = remember {
        Question(
            id = questionCount,
        )
    }

    var questionValue by remember {
        mutableStateOf<String>(question.question)
    }
    var selectedImageCoin by remember {
        mutableStateOf<String>("")
    }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageCoin = uri.toString() }
    )


    var answer1 by remember {
        mutableStateOf<String>("")
    }
    var answer2 by remember {
        mutableStateOf<String>("")
    }
    var answer3 by remember {
        mutableStateOf<String>("")
    }
    var answer4 by remember {
        mutableStateOf<String>("")
    }
    var selectedOption by remember { mutableStateOf("1") }

    val context = LocalContext.current
    val snackBarVisibleState = remember { mutableStateOf(false) }


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
                    Log.i("ImagePicker","selected img url -- $selectedImageCoin")
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
                        } else{
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
                ButtonStyle(
                    text = "Previous Question",
                    onClick = {
                        questionCount--
                        onPreviousClick()
                    },
                    modifier = Modifier.height(50.dp)
                )
                ButtonStyle(
                    text = "Next Question",
                    onClick = {
                        question.answers = mutableListOf(answer1,answer2,answer3,answer4)
                        question.image = selectedImageCoin
                        questionCount++
                        question.question = questionValue
                        question.correctAnswer = question.answers.indexOf(selectedOption)
                        onNextClick(question)
                    },
                    modifier = Modifier.height(50.dp)
                )
            }
        }
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
                    selectedColor = Color.White, // Color when the RadioButton is selected
                    unselectedColor = Color.Gray, // Color when the RadioButton is not selected
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun QuizCreatorScreenPreview() {
    QuizCreatorScreen()
}

