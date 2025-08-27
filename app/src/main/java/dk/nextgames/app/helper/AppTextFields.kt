// helper/AppOutlinedTextField.kt
package dk.nextgames.app.helper

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
    // Hent altid den “indbyggede” content-farve (fra Surface/tema)
    val c: Color = LocalContentColor.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,

        // Sørg for at det virkelig er singleLine uden linjeskift
        singleLine = singleLine,
        maxLines   = if (singleLine) 1 else Int.MAX_VALUE,

        visualTransformation = visualTransformation,
        modifier = modifier,

        // Tving selve tekstens farve – så er den korrekt også UDEN fokus
        textStyle = LocalTextStyle.current.copy(color = c),

        colors = OutlinedTextFieldDefaults.colors(
            // Tekst
            focusedTextColor   = c,
            unfocusedTextColor = c,
            disabledTextColor  = c.copy(alpha = 0.38f),
            errorTextColor     = c,

            // Cursor
            cursorColor      = c,
            errorCursorColor = c,

            // Label (vigtigt: også i UNFOCUSED, ellers ser du “hvid” label)
            focusedLabelColor   = c,
            unfocusedLabelColor = c.copy(alpha = 0.80f),

            // Placeholder-farver (hvis du senere bruger placeholder-slot)
            focusedPlaceholderColor   = c.copy(alpha = 0.60f),
            unfocusedPlaceholderColor = c.copy(alpha = 0.60f),

            // Border
            focusedBorderColor   = c,
            unfocusedBorderColor = c.copy(alpha = .6f),
            disabledBorderColor  = c.copy(alpha = .38f),
            errorBorderColor     = Color.Red
        )
    )
}
