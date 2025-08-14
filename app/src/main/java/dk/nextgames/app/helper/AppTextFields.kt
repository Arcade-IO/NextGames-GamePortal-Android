// helper/AppOutlinedTextField.kt
package dk.nextgames.app.helper

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun AppOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    // Vi default til én linje
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    // Hent alltid den “innebygde” content-fargen (fra AdaptiveSurface eller tema)
    val c: Color = LocalContentColor.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,

        // Sørg for at det virkelig er singleLine uten linjeskift
        singleLine = singleLine,
        maxLines   = if (singleLine) 1 else Int.MAX_VALUE,

        visualTransformation = visualTransformation,
        modifier = modifier,

        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor       = c,
            unfocusedTextColor     = c,
            cursorColor            = c,
            focusedLabelColor      = c,
            focusedBorderColor     = c,
            unfocusedBorderColor   = c.copy(alpha = .6f)
        )
    )
}
