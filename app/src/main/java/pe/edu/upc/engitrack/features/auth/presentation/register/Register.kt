package pe.edu.upc.engitrack.features.auth.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pe.edu.upc.engitrack.core.ui.theme.EasyShopTheme
import pe.edu.upc.engitrack.features.auth.presentation.di.PresentationModule

@Composable
fun Register(
    viewModel: RegisterViewModel,
    onRegister: () -> Unit,
    onLogin: () -> Unit
) {
    val fullName = viewModel.fullName.collectAsState()
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val repeatPassword = viewModel.repeatPassword.collectAsState()

    val isVisible = remember {
        mutableStateOf(false)
    }
    val isRepeatVisible = remember {
        mutableStateOf(false)
    }

    val user = viewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Project tracker",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = fullName.value,
                onValueChange = {
                    viewModel.updateFullName(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                placeholder = {
                    Text(text = "Full name")
                }
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = {
                    viewModel.updateEmail(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                placeholder = {
                    Text(text = "Email")
                }
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = {
                    viewModel.updatePassword(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                placeholder = {
                    Text("Password")
                },
                visualTransformation = if (isVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            isVisible.value = !isVisible.value
                        }
                    ) {
                        Icon(
                            if (isVisible.value) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = null
                        )
                    }
                }
            )

            OutlinedTextField(
                value = repeatPassword.value,
                onValueChange = {
                    viewModel.updateRepeatPassword(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                placeholder = {
                    Text("Repeat password")
                },
                visualTransformation = if (isRepeatVisible.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            isRepeatVisible.value = !isRepeatVisible.value
                        }
                    ) {
                        Icon(
                            if (isRepeatVisible.value) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = null
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.register()
                    onRegister()
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
            ) {
                Text(text = "Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val socialButtonColors = ButtonDefaults.buttonColors(containerColor = Color(0xFF36A420))

            Button(
                onClick = { /* TODO: Google login */ },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp),
                colors = socialButtonColors
            ) {
                Text(text = "Continue with google")
            }

            Button(
                onClick = { /* TODO: Microsoft login */ },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp),
                colors = socialButtonColors
            ) {
                Text(text = "Continue with Microsoft")
            }

            user.value?.let {
                Text("Success")
            }
        }

        ClickableText(
            text = buildAnnotatedString {
                append("Log in if you already have an account")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    pushStringAnnotation(tag = "Login", annotation = "Login")
                    append("")
                    pop()
                }
            },
            onClick = {
                onLogin()
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    val viewModel = PresentationModule.getRegisterViewModel()
    EasyShopTheme {
        Register (viewModel, {}, {})
    }
}
