package sahak.sahakyan.quizcreatorapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        androidx.compose.material3.OutlinedTextField(
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