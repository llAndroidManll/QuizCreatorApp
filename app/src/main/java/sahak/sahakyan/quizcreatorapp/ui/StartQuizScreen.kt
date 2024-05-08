package sahak.sahakyan.quizcreatorapp.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import sahak.sahakyan.quizcreatorapp.viewmodel.startquiz.StartQuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import sahak.sahakyan.quizcreatorapp.R
import sahak.sahakyan.quizcreatorapp.entity.Question


@Composable
fun StartQuizScreen(
    viewModel: StartQuizViewModel = viewModel<StartQuizViewModel>(),
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onFinishClick: () -> Unit = {}
) {
    val currentQuestion = remember {
        MutableLiveData<Question>(viewModel.currentQuestion.value)
    }
    var selectedOption by remember { mutableStateOf<String?>(null) }
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


            // Question Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White, RoundedCornerShape(20))
                    .background(Color.Transparent, RoundedCornerShape(20))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = currentQuestion.value!!.question,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }


            // Image
            if (currentQuestion.value!!.image!!.isNotEmpty()) {
                AsyncImage(
                    model = currentQuestion.value!!.image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color.White),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(1.dp, Color.White),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Image",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                        ),
                    )
                }
            }

            // Items
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ItemsInColumn(
                    selected = selectedOption == currentQuestion.value!!.answers[0],
                    text = currentQuestion.value!!.answers[0],
                    onClick = {
                        if (currentQuestion.value!!.answers[0] == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = currentQuestion.value!!.answers[0]
                        }
                    }
                )
                ItemsInColumn(
                    selected = selectedOption == currentQuestion.value!!.answers[1],
                    text = currentQuestion.value!!.answers[1],
                    onClick = {
                        if (currentQuestion.value!!.answers[1] == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = currentQuestion.value!!.answers[1]
                        }
                    }
                )
                ItemsInColumn(
                    selected = selectedOption == currentQuestion.value!!.answers[2],
                    text = currentQuestion.value!!.answers[2],
                    onClick = {
                        if (currentQuestion.value!!.answers[2] == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = currentQuestion.value!!.answers[2]
                        }
                    }
                )
                ItemsInColumn(
                    selected = selectedOption == currentQuestion.value!!.answers[3],
                    text = currentQuestion.value!!.answers[3],
                    onClick = {
                        if (currentQuestion.value!!.answers[3] == "") {
                            snackBarVisibleState.value = true
                        } else {
                            selectedOption = currentQuestion.value!!.answers[3]
                        }
                    }
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
                            viewModel.incrementQuestionCount()

                            onNextClick()
                        },
                        modifier = Modifier.height(50.dp)
                    )
                } else {
                    ButtonStyle(
                        text = "Finish",
                        onClick = {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .padding(start = 20.dp)
            )
            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color.White,
                    unselectedColor = Color.Gray,
                )
            )
        }
    }
}