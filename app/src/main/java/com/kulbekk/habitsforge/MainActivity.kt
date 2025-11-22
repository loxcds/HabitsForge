package com.kulbekk.habitsforge
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kulbekk.habitsforge.ui.theme.HabitsForgeTheme

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun GreetingPreview() {
    AppNavigation()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitsForgeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "registry"
    ) {
        composable("registry") {
            Main(navController = navController)
        }
        composable("enter") {
            EnterActivity(navController = navController)
        }
        composable("createAccount") {
            MenuActivity(navController = navController)
        }
    }
}

@Composable
fun Main(navController: NavController? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            RegistryScreen(navController = navController)
            EnterButton(navController = navController)
        }
    }
}

@Composable
fun RegistryScreen(navController: NavController? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        Text("HABITSFORGE")
        Text("Добро пожаловать в HabitsForge!\n Давайте создадим ваш аккаунт")

        var userName by remember { mutableStateOf("") }
        TextField(
            value = userName,
            onValueChange = { newName ->
                userName = newName
            },
            placeholder = {
                Text("Введите ваше имя")
            }
        )

        var mail by remember { mutableStateOf("") }
        TextField(
            value = mail,
            onValueChange = { newMail ->
                mail = newMail
            },
            placeholder = {
                Text("Почта")
            }
        )

        var password by remember { mutableStateOf("") }
        TextField(
            value = password,
            onValueChange = { newPassword ->
                password = newPassword
            },
            placeholder = {
                Text("Пароль")
            },
            visualTransformation = AsteriskPasswordVisualTransformation()
        )

        Text("Уже есть аккаунт", Modifier.clickable {
            navController?.navigate("enter")
        })
    }
}

@Composable
fun EnterButton(navController: NavController? = null){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        OutlinedButton(
            onClick = { navController?.navigate("createAccount") },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier

                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
                .width(300.dp)
                .height(60.dp)
        ) {
            Text("Создать аккаунт")
        }
    }
}

@Composable
fun EnterActivity(navController: NavController? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Экран входа")
                Text("Войдите в свой аккаунт")

                var mail by remember { mutableStateOf("") }
                TextField(
                    value = mail,
                    onValueChange = { newMail -> mail = newMail },
                    placeholder = { Text("Почта") }
                )

                var password by remember { mutableStateOf("") }
                TextField(
                    value = password,
                    onValueChange = { newPassword -> password = newPassword },
                    placeholder = { Text("Пароль") },
                    visualTransformation = AsteriskPasswordVisualTransformation()
                )

                Button(
                    onClick = {
                        println("Вход в аккаунт")
                        // Здесь логика входа
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Войти")
                }

                Text("Нет аккаунта?", Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        navController?.navigate("registry")
                    })
            }
        }
    }
}
@Composable
fun MenuActivity(navController: NavController? = null){
    Text("MainMenu")
}

class AsteriskPasswordVisualTransformation(
    private val mask: Char = '*'
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val masked = AnnotatedString(mask.toString().repeat(text.length))
        val offset = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset
        }
        return TransformedText(masked, offset)
    }
}