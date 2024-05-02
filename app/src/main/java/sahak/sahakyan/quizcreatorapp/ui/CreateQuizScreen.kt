package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import sahak.sahakyan.quizcreatorapp.R


@Composable
fun CreateQuizScreen(
    onNextStepClick: (String, Int) -> Unit
) {

    val title = remember {
        mutableStateOf("")
    }
    val questionSize = remember {
        mutableStateOf("")
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gradient2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create Quiz",
                modifier = Modifier,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                columnModifier = Modifier.padding(top = 50.dp),
                value = title.value,
                onValueChange = { title.value = it },
                placeHolder = "Enter your quiz name",
            )

            OutlinedTextField(
                columnModifier = Modifier.padding(top = 50.dp),
                value = questionSize.value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        questionSize.value = it
                    }
                },
                placeHolder = "Questions number",
            )

            ButtonStyle(
                text = "Next Step",
                onClick = { onNextStepClick(title.value, questionSize.value.toInt()) },
                modifier = Modifier.padding(50.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateQuizScreenPreview() {
//    CreateQuizScreen()
}