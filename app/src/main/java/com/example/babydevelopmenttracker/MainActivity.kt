package com.example.babydevelopmenttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.babydevelopmenttracker.model.BabyDevelopmentRepository
import com.example.babydevelopmenttracker.ui.theme.BabyDevelopmentTrackerTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyDevelopmentTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BabyDevelopmentTrackerScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyDevelopmentTrackerScreen() {
    var selectedWeek by remember { mutableStateOf(20) }
    val weekInfo = BabyDevelopmentRepository.findWeek(selectedWeek)

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.week_selector_label, selectedWeek),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Slider(
                value = selectedWeek.toFloat(),
                onValueChange = { selectedWeek = it.roundToInt().coerceIn(4, 42) },
                valueRange = 4f..42f,
                steps = 42 - 4 - 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        weekInfo?.let { info ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.development_heading),
                        body = info.babyHighlights
                    )
                }
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.symptoms_heading),
                        body = info.parentChanges
                    )
                }
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.tips_heading),
                        body = info.tips
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedWeek > 4) {
                    val previousWeek = selectedWeek - 1
                    val previousInfo = BabyDevelopmentRepository.findWeek(previousWeek)
                    if (previousInfo != null) {
                        item {
                            Text(
                                text = "Previously (Week $previousWeek)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        item {
                            HighlightCard(
                                title = stringResource(id = R.string.development_heading),
                                body = previousInfo.babyHighlights
                            )
                        }
                    }
                }

                if (selectedWeek < 42) {
                    val nextWeek = selectedWeek + 1
                    val nextInfo = BabyDevelopmentRepository.findWeek(nextWeek)
                    if (nextInfo != null) {
                        item {
                            Text(
                                text = "Up Next (Week $nextWeek)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                            )
                        }
                        item {
                            HighlightCard(
                                title = stringResource(id = R.string.development_heading),
                                body = nextInfo.babyHighlights
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightCard(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
