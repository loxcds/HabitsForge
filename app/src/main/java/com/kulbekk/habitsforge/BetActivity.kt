import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------------------------------------------------
// 1. DATA MODELS
// ---------------------------------------------------------

data class User(
    val id: String,
    val name: String,
    val profilePicUrl: String? = null
)

enum class BetStatus {
    PENDING_ACCEPTANCE,
    ACTIVE,
    COMPLETED,
    DISPUTED
}

data class HabitBet(
    val id: String,
    val title: String,          // e.g., "Gym 3x a week"
    val description: String,    // e.g., "Must send photo by Sunday 8pm"
    val stake: String,          // e.g., "$20" or "Dinner"
    val creator: User,
    val opponent: User,
    val status: BetStatus = BetStatus.PENDING_ACCEPTANCE,
    val proofImages: List<Uri> = emptyList() // List of photo URIs uploaded
)

// ---------------------------------------------------------
// 2. VIEW MODEL (Mock Logic).
// ---------------------------------------------------------

class BetViewModel {
    // In a real app, this would come from a Room Database or Firebase
    var currentBet by mutableStateOf(
        HabitBet(
            id = "101",
            title = "Gym: No Excuses",
            description = "We must upload a selfie at the gym squat rack. Loser buys lunch.",
            stake = "Lunch at Chip's",
            creator = User("u1", "Alex"),
            opponent = User("u2", "Sam"), // This is the current user in this scenario
            status = BetStatus.PENDING_ACCEPTANCE
        )
    )
        private set

    // Simulate Accepting the Bet
    fun acceptBet() {
        currentBet = currentBet.copy(status = BetStatus.ACTIVE)
    }

    // Simulate Uploading a Photo
    fun submitProof(uri: Uri?) {
        if (uri != null) {
            // Append the new image to the list
            val updatedList = currentBet.proofImages + uri
            currentBet = currentBet.copy(proofImages = updatedList)
        }
    }
}

// ---------------------------------------------------------
// 3. UI COMPOSABLES
// ---------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitBetScreen() {
    val viewModel = remember { BetViewModel() }
    val bet = viewModel.currentBet
    val scope = rememberCoroutineScope()

    // Snackbar for feedback
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bet Details") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            BetStatusCard(status = bet.status)

            // The Contract Details
            Card(
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = bet.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "The Stake: ${bet.stake}", color = MaterialTheme.colorScheme.error)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(text = bet.description, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Participants Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserChip(bet.creator, isCreator = true)
                Icon(Icons.Default.Handshake, contentDescription = "Vs")
                UserChip(bet.opponent, isCreator = false)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons based on Status
            when (bet.status) {
                BetStatus.PENDING_ACCEPTANCE -> {
                    Button(
                        onClick = {
                            viewModel.acceptBet()
                            scope.launch { snackbarHostState.showSnackbar("You accepted the bet! It's on.") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Accept Bet")
                    }
                }
                BetStatus.ACTIVE -> {
                    Text("Proof Uploaded: ${bet.proofImages.size}", style = MaterialTheme.typography.labelLarge)

                    Button(
                        onClick = {
                            // In a real app, this launches the Camera or Gallery launcher
                            // We are mocking a URI here
                            viewModel.submitProof(Uri.parse("file://dummy/photo.jpg"))
                            scope.launch { snackbarHostState.showSnackbar("Photo proof uploaded!") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload Photo Proof")
                    }
                }
                else -> {
                    Text("This bet is closed.", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

// ---------------------------------------------------------
// HELPER COMPONENTS
// ---------------------------------------------------------

@Composable
fun BetStatusCard(status: BetStatus) {
    val (color, text) = when (status) {
        BetStatus.PENDING_ACCEPTANCE -> Color(0xFFFFB74D) to "Pending Acceptance"
        BetStatus.ACTIVE -> Color(0xFF81C784) to "Active - Game On!"
        BetStatus.COMPLETED -> Color.Gray to "Completed"
        BetStatus.DISPUTED -> Color.Red to "Disputed"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun UserChip(user: User, isCreator: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Avatar
            Box(modifier = Modifier.size(24.dp).background(Color.LightGray, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = user.name, fontWeight = FontWeight.Bold)
                Text(text = if(isCreator) "Challenger" else "You", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitBet() {
    HabitBetScreen()
}