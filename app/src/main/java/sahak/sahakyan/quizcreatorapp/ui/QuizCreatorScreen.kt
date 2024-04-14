@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package sahak.sahakyan.quizcreatorapp.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sahak.sahakyan.quizcreatorapp.entity.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreatorScreen(
    quizId: String = "",
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onFinishClick: () -> Unit = {},
) {

    val question: Question = Question(
        id = quizId,
    )
    question.answers = ArrayList<String>()
    var answer1 = ""
    var answer2 = ""
    var answer3 = ""
    var answer4 = ""


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
                value = question.question,
                onValueChange = { question.question = it },
                placeHolder = "Enter your question here",
            )

            ImagePicker()

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ItemInColumn(
                    value = answer1,
                    onValueChange = { answer1 = it },
                )
                ItemInColumn(
                    value = answer2,
                    onValueChange = { answer2= it },
                )
                ItemInColumn(
                    value = answer3,
                    onValueChange = { answer3 = it },
                )
                ItemInColumn(
                    value = answer4,
                    onValueChange = { answer4 = it },
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonStyle(
                    text = "Previous Question",
                    onClick = onPreviousClick,
                    modifier = Modifier.height(50.dp)
                )
                ButtonStyle(
                    text = "Next Question",
                    onClick = onNextClick,
                    modifier = Modifier.height(50.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemInColumn(
    value: String = "",
    onValueChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(1.dp,Color.White),
    ) {
        Row(
            modifier = Modifier.padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {  },
                modifier = Modifier.width(250.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                ),
                placeholder = {
                    Text(
                        text = "Enter your answer here",
                        color = Color.White,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                        ),
                        modifier = Modifier
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                ),
                maxLines = 4,
            )

            SwitchWithCustomColors()
        }
    }
}

@Composable
fun SwitchWithCustomColors() {
    var checked by remember { mutableStateOf(true) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),

    )
}

@Composable
fun ButtonStyle(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        ),
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun QuizCreatorScreenPreview() {
    QuizCreatorScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextField(
    columnModifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    placeHolder: String = "",
    textStyle: TextStyle = TextStyle(
        color = Color.White,
        fontSize = 20.sp,
    ),
    textFieldModifier: Modifier = Modifier.fillMaxWidth(),
    spacerModifier: Modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(Color.White),
) {
    Column(
        modifier = columnModifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier,
            textStyle = textStyle,
            placeholder = {
                Text(
                    text = placeHolder,
                    color = Color.White,
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
            ),

        )
        Spacer(
            modifier = spacerModifier
        )
    }
}