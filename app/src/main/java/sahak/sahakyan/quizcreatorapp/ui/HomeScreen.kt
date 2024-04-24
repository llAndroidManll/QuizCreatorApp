package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import sahak.sahakyan.quizcreatorapp.R
import sahak.sahakyan.quizcreatorapp.entity.Quizzes
import sahak.sahakyan.quizcreatorapp.navigation.NavigationScreens

@Composable
fun HomeScreen(
    navController: NavHostController,
    quizData: Quizzes = Quizzes("", emptyList()),
    onAddClick: ()->Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.gradient2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        // Content on top of the background image
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // If User have created a custom Quiz, will be displayed that
            // If not then will be displayed only one box inside + symbol

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .border(1.dp, Color.Red),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier,
                    text = "Your Quizzes `",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier,
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    ),
                ) {
                    items(quizData.quizzes.size) { index ->
                        ItemBox(
                            title = quizData.quizzes[index].title,
                            onClick = {

                                navController.navigate(NavigationScreens.QuizCreator.route + "/${quizData.quizzes[index].id}")

                                // TODO -- OPEN THAT QUIZ
                            }
                        )
                    }

                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .size(30.dp)
                ) {
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                BottomBar(navController = navController)
            }
        }

    }
}

@Composable
fun ItemBox(
    title: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick,
        modifier = Modifier
    ) {
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

