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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.text.format
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList

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
    MyCalendarScreen()
}
// Data class для представления дня
data class CalendarDay(
    val dayOfMonth: String,
    val dayOfWeek: String,
    val isSelected: Boolean = false
)

/**
 * Composable-функция для отображения отдельного дня календаря с обработчиком клика.
 * @param onClick Функция, вызываемая при нажатии на этот день.
 */
@Composable
fun DayItem(day: CalendarDay, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick) // Добавляем кликабельность ко всему столбцу дня
    ) {
        // День недели (над кругом)
        Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Число в круге
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (day.isSelected) Color.Blue else Color.LightGray)
        ) {
            Text(
                text = day.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (day.isSelected) Color.White else Color.Black
            )
        }
    }
}

/**
 * Composable-функция для отображения строки недели.
 */
@Composable
fun WeekRow(days: List<CalendarDay>, onDayClick: (CalendarDay) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        days.forEach { day ->
            DayItem(day = day, onClick = { onDayClick(day) }) // Передаем обработчик клика
        }
    }
}

// Пример использования (раскомментируйте и добавьте в ваш код):


@Composable
fun MyCalendarScreen() {
    // 1. Инициализируем список дней и сохраняем его в состоянии с помощью remember
    val initialDays = listOf(
        CalendarDay("31", "Вс"),
        CalendarDay("1", "Пн"),
        CalendarDay("2", "Вт"),
        CalendarDay("3", "Ср"),
        CalendarDay("4", "Чт"),
        CalendarDay("5", "Пт"),
        CalendarDay("6", "Сб")
    ).toMutableStateList() // Преобразуем в изменяемый список состояния

    val weekDays = remember { initialDays }

    // 2. Определяем обработчик клика, который обновляет состояние
    val handleDayClick: (CalendarDay) -> Unit = { clickedDay ->
        // Находим индекс кликнутого дня
        val index = weekDays.indexOf(clickedDay)
        if (index != -1) {
            // Сначала сбрасываем выбор со всех остальных дней (опционально, если нужен только один выбор)
            weekDays.forEachIndexed { i, day ->
                if (i != index && day.isSelected) {
                    weekDays[i] = day.copy(isSelected = false)
                }
            }
            // Обновляем состояние выбранного дня, создавая его копию с isSelected = true
            weekDays[index] = clickedDay.copy(isSelected = !clickedDay.isSelected)
        }
    }

    Column {
        // 3. Передаем изменяемый список и обработчик клика в WeekRow
        WeekRow(days = weekDays, onDayClick = handleDayClick)
    }
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