package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import sahak.sahakyan.quizcreatorapp.R
import sahak.sahakyan.quizcreatorapp.viewmodel.HomeViewModel
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavHostController = rememberNavController(),
    onAddClick: () -> Unit = {},
    onRunButton: (String) -> Unit = {},
    onEditButton: () -> Unit = {},
    onShareButton: () -> Unit = {},
) {
    val quizzes by viewModel.quizzes

    var buttonVisibility by remember { mutableStateOf(quizzes.associate { it.title to false }) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gradient2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier,
                    text = "Your Quizzes `",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    this.items(quizzes) { quiz ->
                        ItemBox(
                            title = quiz.title,
                            onClick = {
                                buttonVisibility = buttonVisibility.mapValues { (_, _) -> false } // Hide all buttons
                                buttonVisibility = buttonVisibility.toMutableMap().apply { set(quiz.title, true) } // Show buttons for the clicked quiz
                            },
                            isButtonVisible = buttonVisibility[quiz.title] ?: false, // Get visibility state for the current quiz,
                            onRunButton = {
                                onRunButton(quiz.id)
                            },
                            onEditButton = onEditButton,
                            onShareButton = onShareButton

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
    onClick: () -> Unit = {},
    isButtonVisible: Boolean = false,
    onRunButton: () -> Unit = {},
    onEditButton: () -> Unit = {},
    onShareButton: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color.White, RoundedCornerShape(20))
            .background(Color.White, RoundedCornerShape(20)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(20.dp)
        )

        if (isButtonVisible) {
            Row(
                modifier = Modifier.width(120.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(onClick = onRunButton, imageVector = Icons.Default.PlayArrow)
                Spacer(modifier = Modifier.width(10.dp))
                CustomIconButton(onClick = onEditButton, imageVector = Icons.Default.Edit)
                Spacer(modifier = Modifier.width(10.dp))
                CustomIconButton(onClick = onShareButton, imageVector = Icons.Default.Share)
            }
        } else {
            Spacer(modifier = Modifier.width(120.dp)) // Spacer to maintain layout consistency
        }
    }
}

@Composable
fun CustomIconButton(
    onClick: () -> Unit = {},
    imageVector: ImageVector
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(24.dp)
            .height(24.dp)
            .padding(0.dp),
        shape = RoundedCornerShape(0),
        border = BorderStroke(0.dp, Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        colors = buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
        )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/*
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent,
        ),
*/