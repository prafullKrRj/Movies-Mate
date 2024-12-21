package com.prafullkumar.moviesmate.ui.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.prafullkumar.moviesmate.AppRoutes
import com.prafullkumar.moviesmate.R
import com.prafullkumar.moviesmate.utils.Resource

@Composable
fun AuthScreen(viewModel: AuthViewModel, navController: NavHostController) {
    var isLogin by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val loginState by viewModel.loginState.collectAsState()
    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            navController.popBackStack()
            navController.navigate(AppRoutes.Application)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Movies Mate Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App Name
                Text(
                    text = "Movies Mate",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your Personal Movie Companion",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Toggle between Login and Register
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    TabRow(
                        selectedTabIndex = if (isLogin) 0 else 1,
                        modifier = Modifier
                            .width(300.dp)
                            .clip(RoundedCornerShape(50.dp)),

                        ) {
                        Tab(
                            selected = isLogin,
                            onClick = { isLogin = true },
                            text = { Text("Login") })
                        Tab(selected = !isLogin,
                            onClick = { isLogin = false },
                            text = { Text("Register") })
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (isLogin) {
                    LoginContent(viewModel, loginState)
                } else {
                    RegisterContent(viewModel, loginState)
                }
            }
            if (loginState is Resource.Loading) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LoginContent(viewModel: AuthViewModel, loginState: Resource<Boolean>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            })

        Spacer(modifier = Modifier.height(24.dp))
        if (isError || loginState is Resource.Error) {
            if (loginState is Resource.Error) {
                errorMessage = loginState.message ?: "An error occurred"
            }
            ErrorText(errorMessage)
        }
        Button(
            enabled = loginState !is Resource.Loading,
            onClick = {
                if (username.isEmpty()) {
                    isError = true
                    errorMessage = "Username cannot be empty"
                } else if (password.isEmpty()) {
                    isError = true
                    errorMessage = "Password cannot be empty"
                } else if (password.length < 6) {
                    isError = true
                    errorMessage = "Password must be at least 6 characters"
                } else {
                    viewModel.loginUser(username, password)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { /* Forgot password logic */ }) {
            Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun RegisterContent(viewModel: AuthViewModel, loginState: Resource<Boolean>) {
    var name by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp)
        )


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                        contentDescription = "Show password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.8f),
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                }) {
                    Icon(
                        painter = painterResource(id = if (confirmPasswordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24),
                        contentDescription = "Show password"
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.height(24.dp))

        if (isError || loginState is Resource.Error) {
            if (loginState is Resource.Error) {
                errorMessage = loginState.message ?: "An error occurred"
            }
            ErrorText(errorMessage)
        }
        Button(
            enabled = loginState !is Resource.Loading,
            onClick = {
                if (password != confirmPassword && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    isError = true
                    errorMessage = "Passwords do not match"
                } else if (username.isEmpty()) {
                    isError = true
                    errorMessage = "Username cannot be empty"
                } else if (name.isEmpty()) {
                    isError = false
                    errorMessage = "Name cannot be empty"
                } else {
                    isError = false
                    viewModel.registerUser(username, password, name)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register", fontSize = 16.sp)
        }
    }
}

@Composable
fun ErrorText(errorMessage: String) {
    Text(
        text = errorMessage,
        color = MaterialTheme.colorScheme.error,
        fontSize = 14.sp,
        modifier = Modifier.padding(8.dp)
    )
}