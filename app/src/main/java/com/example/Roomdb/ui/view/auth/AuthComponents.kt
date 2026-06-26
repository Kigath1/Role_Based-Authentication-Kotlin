package com.example.Roomdb.ui.view.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.Roomdb.ui.theme.*

@Composable
fun KaziTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 5,
    enabled: Boolean = true,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        enabled = enabled,
        isError = isError,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KaziGreen,
            focusedLabelColor = KaziGreen,
            cursorColor = KaziGreen,
            unfocusedBorderColor = BorderGray,
            errorBorderColor = ErrorRed
        )
    )
}

/**
 * Primary CTA button — green fill, consistent height and shape.
 */
@Composable
fun KaziPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = KaziGreen)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .height(22.dp)
                    .then(Modifier.fillMaxWidth(0.06f)),
                strokeWidth = 2.dp
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/**
 * Secondary/outlined button — used for back/skip actions.
 */
@Composable
fun KaziSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = KaziGreen),
        border = androidx.compose.foundation.BorderStroke(1.dp, KaziGreen)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/**
 * Section header used in multi-step onboarding flows.
 */
@Composable
fun KaziStepHeader(
    step: Int,
    total: Int,
    title: String,
    subtitle: String
) {
    Text(
        text = "Step $step of $total",
        style = MaterialTheme.typography.labelMedium,
        color = KaziGreen
    )
    androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        color = SurfaceDark,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
    androidx.compose.foundation.layout.Spacer(Modifier.height(4.dp))
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = TextGray
    )
}