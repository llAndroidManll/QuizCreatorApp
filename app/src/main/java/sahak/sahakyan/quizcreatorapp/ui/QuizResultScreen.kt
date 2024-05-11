package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import sahak.sahakyan.quizcreatorapp.viewmodel.StartQuizViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import sahak.sahakyan.quizcreatorapp.R

@Composable
fun QuizResultScreen(
    viewModel: StartQuizViewModel = viewModel(),
    onGoHomeClick: () -> Unit
) {

    val textStyle = TextStyle(
        color = Color.White,
        fontSize = 18.sp,
        fontStyle = FontStyle.Italic,
        textDecoration = TextDecoration.Underline,
        textAlign = TextAlign.Center,
    )

    val textStyle2 = textStyle.copy(
        fontSize = 20.sp,
        textDecoration = TextDecoration.None,
    )

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
            Text(text = "Quiz Result:",
                style = textStyle2
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Correct Answers: ${viewModel.correctAnswersCount.value}",
                style = textStyle
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Incorrect Answers: ${(viewModel.questionCount.value + 1 - viewModel.correctAnswersCount.value)}",
                style = textStyle
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Total Questions: ${viewModel.questionCount.value + 1}",
                style = textStyle
            )
            Spacer(modifier = Modifier.height(20.dp))
            ButtonStyle(
                text = "Go Home",
                onClick = {
                    onGoHomeClick()
                }
            )

        }
    }
}