package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import sahak.sahakyan.quizcreatorapp.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = HomeViewModel(),
    navController: NavHostController = rememberNavController(),
    onAddClick: ()->Unit = {}
) {

    val quizzes by viewModel.quizzes
    val showDialog = remember { mutableStateOf(false) }

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
                    this.items(quizzes.size) {
                        ItemBox(
                            title = quizzes[it].title,
                            onClick = {
                                showDialog.value = true
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

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Choose Option") },

            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = "OK")
                }
            }
        )
    }
}

@Composable
fun ItemBox(
    title: String,
    onClick: () -> Unit = {},
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

            }
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

    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

/*


    var buttonState by remember {
        mutableStateOf(false)
    }
if (buttonState) {
            Row(
                modifier = Modifier
                    .width(85.dp)
                    .height(24.dp)
                    .border(1.dp, Color.Red,)
                ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = onRunButton,
                    modifier = Modifier.size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.start),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
                IconButton(
                    onClick = onRunButton,
                    modifier = Modifier.size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
                IconButton(
                    onClick = onRunButton,
                    modifier = Modifier.size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.share),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }


*/
