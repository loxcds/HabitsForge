package com.kulbekk.habitsforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kulbekk.habitsforge.ui.theme.HabitsForgeTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.datetime.*
import java.util.Calendar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GreetingPreview() {
    RegistryScreen()
    //MenuScreen()
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
    val context = LocalContext.current

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
            MenuScreen(onNavigateToActivity = {
                context.startActivity(Intent(context, MenuActivity::class.java))
            })
        }
    }
}

@Composable
fun Main(navController: NavController? = null) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            RegistryScreen(navController = navController)
        }
    }
}

@Composable
fun RegistryScreen(navController: NavController? = null) {
    var userName by remember { mutableStateOf("") }
    var mail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isInformerVisible by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize())
    {
        Image(
            modifier = Modifier.size(300.dp),
            painter = painterResource(id = R.drawable.logo2),
            contentDescription = null,

            )
        Text("Добро пожаловать в HabitsForge!\n Давайте создадим ваш аккаунт")

        TextField(
            value = userName,
            onValueChange = { newName ->
                userName = newName
            },
            placeholder = {
                Text("Введите ваше имя")
            }
        )

        TextField(
            value = mail,
            onValueChange = { newMail ->
                mail = newMail
            },
            placeholder = {
                Text("Почта")
            }
        )

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

        if (isInformerVisible) {
            Box(
                Modifier
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Yellow.copy(alpha = 0.3f))
            ) {
                LaunchedEffect(isInformerVisible) {
                    delay(2 * 1000)
                    isInformerVisible = false
                }
                Text(
                    text = "Заполните все поля",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp
                )
            }

        }
    }

    val context = LocalContext.current
    EnterButton(
        onClick = {
            if (password.isEmpty() || mail.isEmpty() || userName.isEmpty()) {
                isInformerVisible = true
            } else {
                navController?.navigate("createAccount")
            }
        }
    )
}
@Composable
fun EnterButton(onClick: () -> Unit){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        OutlinedButton(
            onClick = onClick,
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
fun MenuScreen(onNavigateToActivity: () -> Unit = {}) {
    var isDaySelected by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val habits = remember { 
        mutableStateListOf<Habit>().apply {
            addAll(HabitRepository.loadHabits(context))
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        MainCalendarScreen(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onSelectionChanged = { isDaySelected = it },
            habits = habits
        )
        
        if (!isDaySelected) {
            pet(isVisible = true)
        }
        
        BottomButtonsScreen(modifier = Modifier.wrapContentHeight())
    }
}

data class MainCalendarDay(
    val date: LocalDate,
    val dayOfMonth: String,
    val dayOfWeek: String,
    val isSelected: Boolean = false
)

@Composable
fun MainDayItem(day: MainCalendarDay, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = day.dayOfWeek,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (day.isSelected) Color.Blue else Color.LightGray)
        ) {
            Text(
                text = day.dayOfMonth,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (day.isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun MainWeekRow(days: List<MainCalendarDay>, onDayClick: (MainCalendarDay) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            MainDayItem(day = day, onClick = { onDayClick(day) })
        }
    }
}

@Composable
fun MainCalendarScreen(modifier: Modifier, onSelectionChanged: (Boolean) -> Unit, habits: MutableList<Habit>) {
    val today = remember {
        val calendar = Calendar.getInstance()
        LocalDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    var currentStartOfWeek by remember {
        val daysToSubtract = (today.dayOfWeek.isoDayNumber - 1)
        mutableStateOf(today.minus(daysToSubtract, DateTimeUnit.DAY))
    }

    val dayNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    val weekDays = remember(currentStartOfWeek) {
        mutableStateListOf<MainCalendarDay>().apply {
            addAll(
                List(7) { i ->
                    val date = currentStartOfWeek.plus(i, DateTimeUnit.DAY)
                    MainCalendarDay(
                        date = date,
                        dayOfMonth = date.dayOfMonth.toString(),
                        dayOfWeek = dayNames[i],
                        isSelected = false
                    )
                }
            )
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val handleDayClick: (MainCalendarDay) -> Unit = { clickedDay ->
        val index = weekDays.indexOf(clickedDay)
        if (index != -1) {
            val isCurrentlySelected = weekDays[index].isSelected
            weekDays.forEachIndexed { i, day ->
                if (day.isSelected) {
                    weekDays[i] = day.copy(isSelected = false)
                }
            }
            val newSelected = !isCurrentlySelected
            weekDays[index] = clickedDay.copy(isSelected = newSelected)
            onSelectionChanged(newSelected)
        }
    }

    val selectedDay = weekDays.find { it.isSelected }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            CalendarMonthTitle(month = currentStartOfWeek.month, year = currentStartOfWeek.year)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentStartOfWeek = currentStartOfWeek.minus(7, DateTimeUnit.DAY) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Предыдущая неделя")
                }

                MainWeekRow(
                    days = weekDays,
                    onDayClick = handleDayClick,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { currentStartOfWeek = currentStartOfWeek.plus(7, DateTimeUnit.DAY) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Следующая неделя")
                }
            }
        }

        if (selectedDay != null) {
            val habitsForDay = habits.filter { it.selectedDates.contains(selectedDay.date) }
            
            if (habitsForDay.isNotEmpty()) {
                items(habitsForDay, key = { it.id }) { habit ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        HabitItem(
                            habit = habit,
                            onDelete = {
                                habits.remove(habit)
                                HabitRepository.saveHabits(context, habits)
                            }
                        )
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Нет привычек на этот день", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { showDialog = true }) {
                    Text("Добавить новую привычку")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDialog) {
        CreateHabitDialog(
            initialSelectedDate = selectedDay?.date,
            onDismiss = { showDialog = false },
            onSave = { habit ->
                habits.add(habit)
                HabitRepository.saveHabits(context, habits)
                showDialog = false
                weekDays.forEachIndexed { i, day -> weekDays[i] = day.copy(isSelected = false) }
                onSelectionChanged(false)
            }
        )
    }
}

@Composable
fun CircularIconButton(iconResId: Int, onClick: () -> Unit) {
    val size = 50.dp
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.size(size),
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun BottomButtonsScreen(modifier: Modifier) {
    Box(modifier = modifier) {
        val iconsList = listOf(R.drawable.profile, R.drawable.drugi, R.drawable.nastroiki)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            iconsList.forEachIndexed { index, iconResId ->
                CircularIconButton(iconResId = iconResId) {
                    println("Нажата кнопка $index")
                }
            }
        }
    }
}

@Composable
fun pet(isVisible: Boolean = true){
    val imageResources = listOf(
        R.drawable.i1rostok,
        R.drawable.i2rostok,
        R.drawable.i3rostok,
        R.drawable.i4,
        R.drawable.i5,
        R.drawable.i6
    )

    var currentIndex by remember { mutableStateOf(0) }
    
    if (isVisible) {
        val currentImageResId = imageResources[currentIndex]
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = currentImageResId),
                contentDescription = "Циклическое изображение",
                modifier = Modifier
                    .size(300.dp)
                    .clickable {
                        currentIndex = (currentIndex + 1) % imageResources.size
                    },
                contentScale = ContentScale.Fit
            )
        }
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
