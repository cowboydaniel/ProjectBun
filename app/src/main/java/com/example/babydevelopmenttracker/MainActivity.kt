package com.example.babydevelopmenttracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.babydevelopmenttracker.data.FamilyRole
import com.example.babydevelopmenttracker.data.PartnerInviteCode
import com.example.babydevelopmenttracker.data.UserPreferences
import com.example.babydevelopmenttracker.data.UserPreferencesRepository
import com.example.babydevelopmenttracker.model.BabyDevelopmentRepository
import com.example.babydevelopmenttracker.model.FetalGrowthPoint
import com.example.babydevelopmenttracker.model.FetalGrowthTrends
import com.example.babydevelopmenttracker.model.calculateWeekFromDueDate
import com.example.babydevelopmenttracker.model.findWeek
import com.example.babydevelopmenttracker.network.CreateFamilyRequest
import com.example.babydevelopmenttracker.network.FamilySyncService
import com.example.babydevelopmenttracker.network.RegisterFamilyMemberRequest
import com.example.babydevelopmenttracker.reminders.WeeklyReminderScheduler
import com.example.babydevelopmenttracker.ui.theme.BabyDevelopmentTrackerTheme
import com.example.babydevelopmenttracker.ui.theme.ThemePreference
import com.example.babydevelopmenttracker.ui.theme.themePreviewColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyDevelopmentTrackerApp()
        }
    }
}

// Most anatomy scans that reveal a baby’s sex happen around week 18.
private const val GENDER_REVEAL_WEEK = 18
private enum class DrawerDestination { Home, Settings }

private const val FAMILY_SYNC_TAG = "FamilySync"

private sealed class PartnerInviteOutcome {
    data class Success(val epochDay: Long) : PartnerInviteOutcome()
    data class Error(val reason: PartnerInviteError) : PartnerInviteOutcome()
}

private enum class PartnerInviteError {
    InvalidCode,
    RegistrationFailed,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyDevelopmentTrackerApp() {
    val context = LocalContext.current
    val userPreferencesRepository = remember(context) { UserPreferencesRepository(context) }
    val reminderScheduler = remember(context) { WeeklyReminderScheduler(context) }
    val familySyncService = remember { FamilySyncService.create() }
    val userPreferences by userPreferencesRepository.userPreferences.collectAsState(
        initial = UserPreferences()
    )

    BabyDevelopmentTrackerTheme(themePreference = userPreferences.themePreference) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BabyDevelopmentTrackerScreen(
                userPreferences = userPreferences,
                userPreferencesRepository = userPreferencesRepository,
                reminderScheduler = reminderScheduler,
                familySyncService = familySyncService
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyDevelopmentTrackerScreen(
    userPreferences: UserPreferences,
    userPreferencesRepository: UserPreferencesRepository,
    reminderScheduler: WeeklyReminderScheduler,
    familySyncService: FamilySyncService
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val zoneId = remember { ZoneId.systemDefault() }
    val today = remember { LocalDate.now(zoneId) }
    var selectedWeek by remember { mutableStateOf(20) }
    val dueDateEpochDay = userPreferences.dueDateEpochDay
    val remindersEnabled = userPreferences.remindersEnabled
    val familyRole = userPreferences.familyRole
    val onboardingCompleted = userPreferences.onboardingCompleted
    val themePreference = userPreferences.themePreference
    val dueDateFromPartnerInvite = userPreferences.dueDateFromPartnerInvite
    val partnerLinkApproved = userPreferences.partnerLinkApproved
    val dueDate = dueDateEpochDay?.let(LocalDate::ofEpochDay)
    var showDatePicker by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val inviteCode = remember(dueDateEpochDay) {
        dueDateEpochDay?.let(PartnerInviteCode::generate)
    }

    var partnerCodeError by remember { mutableStateOf<String?>(null) }
    var partnerCodeSuccess by remember { mutableStateOf<String?>(null) }
    var partnerLinkError by remember { mutableStateOf<String?>(null) }
    var isPartnerLinking by remember { mutableStateOf(false) }
    var isPartnerCodeSubmitting by remember { mutableStateOf(false) }
    val clearPartnerCodeStatus: () -> Unit = remember {
        {
            partnerCodeError = null
            partnerCodeSuccess = null
        }
    }

    LaunchedEffect(inviteCode) {
        partnerLinkError = null
    }

    suspend fun registerPartnerInvite(code: String): PartnerInviteOutcome {
        val epochDay = PartnerInviteCode.parse(code)
            ?: return PartnerInviteOutcome.Error(PartnerInviteError.InvalidCode)
        val familyId = PartnerInviteCode.familyIdForInviteCode(code)
            ?: return PartnerInviteOutcome.Error(PartnerInviteError.RegistrationFailed)

        return try {
            userPreferencesRepository.updateFamilyLink(familyId, null)
            val response = familySyncService.registerFamilyMember(
                familyId,
                RegisterFamilyMemberRequest(inviteCode = code)
            )
            userPreferencesRepository.updateDueDate(epochDay, fromPartnerInvite = true)
            if (remindersEnabled) {
                reminderScheduler.scheduleWeeklyReminder(epochDay)
            }
            userPreferencesRepository.updateDeviceAuthToken(response.authToken)
            userPreferencesRepository.updatePartnerLinkApproved(true)
            PartnerInviteOutcome.Success(epochDay)
        } catch (error: Exception) {
            Log.e(FAMILY_SYNC_TAG, "Failed to register family member", error)
            userPreferencesRepository.updateFamilyLink(null, null)
            userPreferencesRepository.updateDeviceAuthToken(null)
            userPreferencesRepository.updatePartnerLinkApproved(false)
            PartnerInviteOutcome.Error(PartnerInviteError.RegistrationFailed)
        }
    }

    val handlePartnerLinkToggle: (Boolean) -> Unit = { approved ->
        if (isPartnerLinking) return@handlePartnerLinkToggle
        partnerLinkError = null
        if (approved) {
            if (inviteCode == null) {
                partnerLinkError =
                    context.getString(R.string.settings_partner_connection_missing_code)
                return@handlePartnerLinkToggle
            }
            val familyId = PartnerInviteCode.familyIdForInviteCode(inviteCode)
            if (familyId == null) {
                partnerLinkError = context.getString(R.string.settings_partner_connection_error)
                return@handlePartnerLinkToggle
            }
            val secret = userPreferences.familyLinkSecret ?: UUID.randomUUID().toString()
            isPartnerLinking = true
            scope.launch {
                try {
                    userPreferencesRepository.updateFamilyLink(familyId, secret)
                    val response = familySyncService.createFamily(
                        familyId,
                        CreateFamilyRequest(secret = secret)
                    )
                    userPreferencesRepository.updateDeviceAuthToken(response.authToken)
                    userPreferencesRepository.updatePartnerLinkApproved(true)
                } catch (error: Exception) {
                    Log.e(FAMILY_SYNC_TAG, "Failed to create family link", error)
                    partnerLinkError =
                        context.getString(R.string.settings_partner_connection_error)
                    userPreferencesRepository.updateFamilyLink(null, null)
                    userPreferencesRepository.updateDeviceAuthToken(null)
                    userPreferencesRepository.updatePartnerLinkApproved(false)
                } finally {
                    isPartnerLinking = false
                }
            }
        } else {
            isPartnerLinking = true
            scope.launch {
                try {
                    userPreferencesRepository.updatePartnerLinkApproved(false)
                    userPreferencesRepository.updateFamilyLink(null, null)
                    userPreferencesRepository.updateDeviceAuthToken(null)
                } finally {
                    isPartnerLinking = false
                }
            }
        }
    }

    val handlePartnerCodeSubmit: (String) -> Unit = { code ->
        if (isPartnerCodeSubmitting) return@handlePartnerCodeSubmit
        clearPartnerCodeStatus()
        scope.launch {
            isPartnerCodeSubmitting = true
            when (val result = registerPartnerInvite(code)) {
                is PartnerInviteOutcome.Success -> {
                    val targetDate = LocalDate.ofEpochDay(result.epochDay)
                    partnerCodeSuccess = context.getString(
                        R.string.settings_partner_code_success,
                        targetDate.format(dateFormatter)
                    )
                }
                is PartnerInviteOutcome.Error -> {
                    partnerCodeError = when (result.reason) {
                        PartnerInviteError.InvalidCode ->
                            context.getString(R.string.settings_partner_code_error)
                        PartnerInviteError.RegistrationFailed ->
                            context.getString(R.string.settings_partner_code_registration_error)
                    }
                }
            }
            isPartnerCodeSubmitting = false
        }
    }

    val calculatedWeek = dueDate?.let { calculateWeekFromDueDate(it, today) }

    LaunchedEffect(calculatedWeek) {
        calculatedWeek?.let { selectedWeek = it }
    }

    LaunchedEffect(remindersEnabled, dueDateEpochDay) {
        if (remindersEnabled) {
            reminderScheduler.scheduleWeeklyReminder(dueDateEpochDay)
        } else {
            reminderScheduler.cancelWeeklyReminder()
        }
    }

    LaunchedEffect(familyRole) {
        if (familyRole != FamilyRole.PARTNER_SUPPORTER) {
            partnerCodeError = null
            partnerCodeSuccess = null
        }
        partnerLinkError = null
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentDestination by remember { mutableStateOf(DrawerDestination.Home) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showPermissionRationale = false
            scope.launch {
                userPreferencesRepository.updateReminderEnabled(true)
                reminderScheduler.scheduleWeeklyReminder(userPreferences.dueDateEpochDay)
            }
            Unit
        } else {
            showPermissionRationale = true
        }
        Unit
    }

    val handleReminderToggle: (Boolean) -> Unit = { isChecked ->
        if (isChecked) {
            val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED

            if (needsPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showPermissionRationale = false
                scope.launch {
                    userPreferencesRepository.updateReminderEnabled(true)
                    reminderScheduler.scheduleWeeklyReminder(dueDateEpochDay)
                }
                Unit
            }
        } else {
            showPermissionRationale = false
            scope.launch {
                userPreferencesRepository.updateReminderEnabled(false)
                reminderScheduler.cancelWeeklyReminder()
            }
            Unit
        }
        Unit
    }

    if (!onboardingCompleted) {
        OnboardingFlow(
            familyRole = familyRole,
            dueDate = dueDate,
            zoneId = zoneId,
            today = today,
            remindersEnabled = remindersEnabled,
            onSelectFamilyRole = { role ->
                showPermissionRationale = false
                scope.launch { userPreferencesRepository.updateFamilyRole(role) }
                Unit
            },
            onConfirmDueDate = { date, fromPartnerInvite ->
                scope.launch {
                    val epochDay = date.toEpochDay()
                    userPreferencesRepository.updateDueDate(epochDay, fromPartnerInvite)
                    if (remindersEnabled) {
                        reminderScheduler.scheduleWeeklyReminder(epochDay)
                    }
                }
                Unit
            },
            onPartnerInviteSubmit = { code -> registerPartnerInvite(code) },
            onReminderToggle = handleReminderToggle,
            onFinish = {
                scope.launch { userPreferencesRepository.updateOnboardingCompleted(true) }
                Unit
            },
            showPermissionRationale = showPermissionRationale
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.navigation_home)) },
                        selected = currentDestination == DrawerDestination.Home,
                        onClick = {
                            currentDestination = DrawerDestination.Home
                            scope.launch { drawerState.close() }
                            Unit
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.navigation_settings)) },
                        selected = currentDestination == DrawerDestination.Settings,
                        onClick = {
                            currentDestination = DrawerDestination.Settings
                            scope.launch { drawerState.close() }
                            Unit
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                    Unit
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = stringResource(id = R.string.drawer_menu_content_description)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { innerPadding ->
                when (currentDestination) {
                    DrawerDestination.Home -> HomeContent(
                        modifier = Modifier.padding(innerPadding),
                        selectedWeek = selectedWeek,
                        onWeekChange = { selectedWeek = it },
                        dueDate = dueDate,
                        dateFormatter = dateFormatter,
                        calculatedWeek = calculatedWeek,
                        familyRole = familyRole,
                        remindersEnabled = remindersEnabled
                    )
                    DrawerDestination.Settings -> SettingsContent(
                        modifier = Modifier.padding(innerPadding),
                        dueDate = dueDate,
                        dateFormatter = dateFormatter,
                        calculatedWeek = calculatedWeek,
                        remindersEnabled = remindersEnabled,
                        onReminderToggle = handleReminderToggle,
                        showPermissionRationale = showPermissionRationale,
                        onSelectDueDate = { showDatePicker = true },
                        familyRole = familyRole,
                        onFamilyRoleSelected = { role ->
                            scope.launch { userPreferencesRepository.updateFamilyRole(role) }
                            Unit
                        },
                        themePreference = themePreference,
                        onThemePreferenceSelected = { preference ->
                            scope.launch {
                                userPreferencesRepository.updateThemePreference(preference)
                            }
                            Unit
                        },
                        inviteCode = inviteCode,
                        partnerLinkApproved = partnerLinkApproved,
                        onPartnerLinkApprovedChange = handlePartnerLinkToggle,
                        isPartnerLinking = isPartnerLinking,
                        partnerLinkErrorMessage = partnerLinkError,
                        onPartnerCodeSubmit = handlePartnerCodeSubmit,
                        isPartnerCodeSubmitting = isPartnerCodeSubmitting,
                        onPartnerCodeInputChanged = clearPartnerCodeStatus,
                        partnerCodeError = partnerCodeError,
                        partnerCodeSuccessMessage = partnerCodeSuccess,
                        dueDateFromPartnerInvite = dueDateFromPartnerInvite
                    )
                }
            }
        }
        if (currentDestination == DrawerDestination.Settings && showDatePicker) {
            val defaultDueDate = remember { today.plusWeeks(20) }
            val initialDate = dueDate ?: defaultDueDate
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = initialDate
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMillis = datePickerState.selectedDateMillis
                            if (selectedMillis != null) {
                                val selectedDate = Instant.ofEpochMilli(selectedMillis)
                                    .atZone(zoneId)
                                    .toLocalDate()
                                scope.launch {
                                    val epochDay = selectedDate.toEpochDay()
                                    userPreferencesRepository.updateDueDate(epochDay)
                                    if (remindersEnabled) {
                                        reminderScheduler.scheduleWeeklyReminder(epochDay)
                                    }
                                }
                                Unit
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text(text = stringResource(id = R.string.date_picker_confirm))
                    }
                    Unit
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(text = stringResource(id = R.string.date_picker_cancel))
                    }
                    Unit
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    selectedWeek: Int,
    onWeekChange: (Int) -> Unit,
    dueDate: LocalDate?,
    dateFormatter: DateTimeFormatter,
    calculatedWeek: Int?,
    familyRole: FamilyRole?,
    remindersEnabled: Boolean,
) {
    val weekInfo = remember(selectedWeek) { BabyDevelopmentRepository.findWeek(selectedWeek) }
    val doctorChecklist = remember(selectedWeek) {
        BabyDevelopmentRepository.doctorChecklistForWeek(selectedWeek)
    }
    val partnerSupport = remember(selectedWeek) {
        BabyDevelopmentRepository.partnerSupportForWeek(selectedWeek)
    }
    val isPartnerRole = remember(familyRole) {
        familyRole == FamilyRole.PARTNER_SUPPORTER
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                familyRole?.let { role ->
                    val roleLabel = stringResource(id = role.labelRes)
                    Text(
                        text = stringResource(id = R.string.home_greeting, roleLabel),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                Text(
                    text = dueDate?.let { date ->
                        stringResource(
                            id = R.string.selected_due_date_label,
                            date.format(dateFormatter)
                        )
                    } ?: stringResource(id = R.string.due_date_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.home_settings_hint),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
                if (isPartnerRole) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CompanionModeBanner(
                        dueDate = dueDate,
                        dateFormatter = dateFormatter,
                        remindersEnabled = remindersEnabled,
                        calculatedWeek = calculatedWeek
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                calculatedWeek?.let { week ->
                    Text(
                        text = stringResource(id = R.string.estimated_week_label, week),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.week_selector_label, selectedWeek),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Slider(
                    value = selectedWeek.toFloat(),
                    onValueChange = { onWeekChange(it.roundToInt().coerceIn(4, 42)) },
                    valueRange = 4f..42f,
                    steps = 42 - 4 - 1,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.today_dashboard_title),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.today_dashboard_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        weekInfo?.let { info ->
            if (isPartnerRole) {
                item {
                    SupporterInsightCard(parentHighlights = info.parentChanges)
                }
                item {
                    val firstHighlight = info.babyHighlights.firstOrNull()
                        ?: stringResource(id = R.string.partner_baby_overview_default)
                    val babyOverviewHighlights = listOf(
                        firstHighlight,
                        stringResource(id = R.string.partner_baby_overview_prompt)
                    )
                    HighlightCard(
                        title = stringResource(id = R.string.partner_baby_overview_heading),
                        highlights = babyOverviewHighlights
                    )
                }
            } else {
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.development_heading),
                        highlights = info.babyHighlights
                    )
                }
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.symptoms_heading),
                        highlights = info.parentChanges
                    )
                }
            }
            if (!isPartnerRole) {
                item {
                    HighlightCard(
                        title = stringResource(id = R.string.tips_heading),
                        highlights = info.tips
                    )
                }
                item {
                    ChecklistCard(
                        title = stringResource(id = R.string.doctor_checklist_heading),
                        subtitle = stringResource(id = R.string.doctor_checklist_description),
                        emptyStateText = stringResource(id = R.string.doctor_checklist_empty),
                        items = doctorChecklist
                    )
                }
            }
            if (isPartnerRole) {
                item {
                    PartnerSupportCard(
                        title = stringResource(id = R.string.partner_support_heading),
                        subtitle = stringResource(id = R.string.partner_support_description),
                        emptyStateText = stringResource(id = R.string.partner_support_empty),
                        items = partnerSupport
                    )
                }
            }
            item {
                GrowthTrendCard(selectedWeek = selectedWeek)
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
                            highlights = previousInfo.babyHighlights
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
                            highlights = nextInfo.babyHighlights
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    dueDate: LocalDate?,
    dateFormatter: DateTimeFormatter,
    calculatedWeek: Int?,
    remindersEnabled: Boolean,
    onReminderToggle: (Boolean) -> Unit,
    showPermissionRationale: Boolean,
    onSelectDueDate: () -> Unit,
    familyRole: FamilyRole?,
    onFamilyRoleSelected: (FamilyRole) -> Unit,
    themePreference: ThemePreference,
    onThemePreferenceSelected: (ThemePreference) -> Unit,
    inviteCode: String?,
    partnerLinkApproved: Boolean,
    onPartnerLinkApprovedChange: (Boolean) -> Unit,
    isPartnerLinking: Boolean,
    partnerLinkErrorMessage: String?,
    onPartnerCodeSubmit: (String) -> Unit,
    isPartnerCodeSubmitting: Boolean,
    onPartnerCodeInputChanged: () -> Unit,
    partnerCodeError: String?,
    partnerCodeSuccessMessage: String?,
    dueDateFromPartnerInvite: Boolean,
) {
    val clipboardManager = LocalClipboardManager.current
    val isPartnerRole = familyRole == FamilyRole.PARTNER_SUPPORTER
    var partnerCodeInput by rememberSaveable { mutableStateOf("") }
    var inviteCopied by rememberSaveable { mutableStateOf(false) }
    val hasDueDate = dueDate != null

    LaunchedEffect(partnerCodeSuccessMessage) {
        if (partnerCodeSuccessMessage != null) {
            partnerCodeInput = ""
        }
    }

    LaunchedEffect(inviteCode) {
        inviteCopied = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.settings_family_role_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(id = R.string.settings_family_role_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            FamilyRoleSelection(
                selectedRole = familyRole,
                onSelectRole = onFamilyRoleSelected,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (!isPartnerRole) {
            Column {
                Text(
                    text = stringResource(id = R.string.settings_partner_invite_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.settings_partner_invite_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                if (!hasDueDate || inviteCode == null) {
                    Text(
                        text = stringResource(id = R.string.settings_partner_invite_requirements_due_date),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.settings_partner_invite_code_label,
                                    inviteCode
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            IconButton(onClick = {
                                clipboardManager.setText(AnnotatedString(inviteCode))
                                inviteCopied = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = stringResource(id = R.string.settings_partner_invite_copy)
                                )
                            }
                        }
                    }
                    Text(
                        text = stringResource(id = R.string.settings_partner_invite_helper_text),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    if (inviteCopied) {
                        Text(
                            text = stringResource(id = R.string.settings_partner_invite_copy_confirmation),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(end = 72.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_partner_connection_toggle_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.settings_partner_connection_toggle_description),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Switch(
                        checked = partnerLinkApproved,
                        onCheckedChange = onPartnerLinkApprovedChange,
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !isPartnerLinking,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                    partnerLinkErrorMessage?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } else {
            Column {
                Text(
                    text = stringResource(id = R.string.settings_partner_code_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(id = R.string.settings_partner_code_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = partnerCodeInput,
                    onValueChange = { value ->
                        partnerCodeInput = value.uppercase()
                        inviteCopied = false
                        onPartnerCodeInputChanged()
                    },
                    placeholder = {
                        Text(text = stringResource(id = R.string.settings_partner_code_placeholder))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
                Button(
                    onClick = {
                        onPartnerCodeSubmit(partnerCodeInput)
                    },
                    enabled = partnerCodeInput.isNotBlank() && !isPartnerCodeSubmitting,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.settings_partner_code_apply))
                }
                partnerCodeError?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                partnerCodeSuccessMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Column {
            Text(
                text = stringResource(id = R.string.settings_due_date_title),
                style = MaterialTheme.typography.titleLarge
            )
            if (isPartnerRole) {
                Text(
                    text = stringResource(id = R.string.settings_due_date_description_partner),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = dueDate?.let { date ->
                        stringResource(
                            id = R.string.selected_due_date_label,
                            date.format(dateFormatter)
                        )
                    } ?: stringResource(id = R.string.settings_partner_code_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 12.dp)
                )
                if (dueDateFromPartnerInvite) {
                    Text(
                        text = stringResource(id = R.string.settings_due_date_invite_managed),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                Text(
                    text = stringResource(id = R.string.settings_due_date_description),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(
                    onClick = {
                        inviteCopied = false
                        onSelectDueDate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = stringResource(id = R.string.select_due_date))
                }
                Text(
                    text = dueDate?.let { date ->
                        stringResource(
                            id = R.string.selected_due_date_label,
                            date.format(dateFormatter)
                        )
                    } ?: stringResource(id = R.string.due_date_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            calculatedWeek?.let { week ->
                Text(
                    text = stringResource(id = R.string.estimated_week_label, week),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Column {
            Text(
                text = stringResource(id = R.string.settings_theme_title),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = stringResource(id = R.string.settings_theme_description),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            ThemeSelection(
                selectedTheme = themePreference,
                onThemeSelected = onThemePreferenceSelected,
                calculatedWeek = calculatedWeek,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Column {
            Text(
                text = stringResource(id = R.string.settings_notifications_title),
                style = MaterialTheme.typography.titleLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(end = 72.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.reminder_toggle_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(id = R.string.reminder_toggle_description),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = onReminderToggle,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            if (showPermissionRationale) {
                Text(
                    text = stringResource(id = R.string.notifications_permission_rationale),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
private fun ThemeSelection(
    selectedTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
    calculatedWeek: Int?,
    modifier: Modifier = Modifier
) {
    val genderThemesUnlocked = calculatedWeek != null && calculatedWeek >= GENDER_REVEAL_WEEK
    val availableThemes = remember(genderThemesUnlocked, selectedTheme) {
        val baseThemes = if (genderThemesUnlocked) {
            ThemePreference.entries.toList()
        } else {
            listOf(ThemePreference.Neutral)
        }
        if (baseThemes.contains(selectedTheme)) baseThemes else baseThemes + selectedTheme
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        availableThemes.forEach { theme ->
            ThemeOptionCard(
                themePreference = theme,
                isSelected = theme == selectedTheme,
                enabled = genderThemesUnlocked || theme == ThemePreference.Neutral,
                onSelect = { onThemeSelected(theme) }
            )
        }

        if (!genderThemesUnlocked) {
            val message = if (calculatedWeek == null) {
                stringResource(id = R.string.settings_theme_locked_message_due_date)
            } else {
                stringResource(id = R.string.settings_theme_locked_message_week, GENDER_REVEAL_WEEK)
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    themePreference: ThemePreference,
    isSelected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val previewColors = remember(themePreference) { themePreviewColors(themePreference) }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.6f)
            .clickable(enabled = enabled, onClick = onSelect),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        border = BorderStroke(width = if (isSelected) 1.5.dp else 1.dp, color = borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                previewColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                    )
                }
            }
            Text(
                text = stringResource(id = themePreference.titleRes),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = stringResource(id = themePreference.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
private fun FamilyRoleSelection(
    selectedRole: FamilyRole?,
    onSelectRole: (FamilyRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FamilyRole.entries.forEach { role ->
            FamilyRoleOptionCard(
                role = role,
                isSelected = role == selectedRole,
                onSelect = { onSelectRole(role) }
            )
        }
    }
}

@Composable
private fun FamilyRoleOptionCard(
    role: FamilyRole,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        border = BorderStroke(width = if (isSelected) 1.5.dp else 1.dp, color = borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = role.labelRes),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = stringResource(id = role.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingFlow(
    familyRole: FamilyRole?,
    dueDate: LocalDate?,
    zoneId: ZoneId,
    today: LocalDate,
    remindersEnabled: Boolean,
    onSelectFamilyRole: (FamilyRole) -> Unit,
    onConfirmDueDate: (LocalDate, Boolean) -> Unit,
    onPartnerInviteSubmit: suspend (String) -> PartnerInviteOutcome,
    onReminderToggle: (Boolean) -> Unit,
    onFinish: () -> Unit,
    showPermissionRationale: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val totalSteps = 3
    var step by remember { mutableStateOf(0) }
    val defaultDueDate = remember(today) { today.plusWeeks(20) }
    val initialDueDateMillis = remember(dueDate, defaultDueDate, zoneId) {
        (dueDate ?: defaultDueDate).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDueDateMillis)
    LaunchedEffect(initialDueDateMillis) {
        datePickerState.selectedDateMillis = initialDueDateMillis
    }
    var selectedRole by remember(familyRole) { mutableStateOf(familyRole) }
    var partnerCode by rememberSaveable { mutableStateOf("") }
    var partnerCodeError by remember { mutableStateOf<String?>(null) }
    var isPartnerCodeSubmitting by remember { mutableStateOf(false) }
    val onboardingScope = rememberCoroutineScope()

    val selectedDate = datePickerState.selectedDateMillis?.let { millis ->
        Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()
    }

    val isPartnerRoleSelected = selectedRole == FamilyRole.PARTNER_SUPPORTER

    LaunchedEffect(isPartnerRoleSelected) {
        if (!isPartnerRoleSelected) {
            partnerCode = ""
            partnerCodeError = null
        }
        isPartnerCodeSubmitting = false
    }

    val canProceed = when (step) {
        0 -> selectedRole != null
        1 -> if (isPartnerRoleSelected) {
            partnerCode.isNotBlank() && !isPartnerCodeSubmitting
        } else {
            selectedDate != null
        }
        else -> true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.onboarding_step_progress, step + 1, totalSteps),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            when (step) {
                0 -> {
                    Text(
                        text = stringResource(id = R.string.onboarding_welcome_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = stringResource(id = R.string.onboarding_welcome_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.onboarding_role_prompt),
                        style = MaterialTheme.typography.titleMedium
                    )
                    FamilyRoleSelection(
                        selectedRole = selectedRole,
                        onSelectRole = { role ->
                            selectedRole = role
                            onSelectFamilyRole(role)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                1 -> {
                    if (isPartnerRoleSelected) {
                        Text(
                            text = stringResource(id = R.string.onboarding_partner_code_title),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = stringResource(id = R.string.onboarding_partner_code_description),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = partnerCode,
                            onValueChange = { value ->
                                partnerCode = value.uppercase()
                                partnerCodeError = null
                            },
                            placeholder = {
                                Text(text = stringResource(id = R.string.onboarding_partner_code_placeholder))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                            modifier = Modifier.fillMaxWidth()
                        )
                        partnerCodeError?.let { error ->
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(id = R.string.onboarding_due_date_title),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = stringResource(id = R.string.onboarding_due_date_description),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DatePicker(state = datePickerState)
                    }
                }
                2 -> {
                    Text(
                        text = stringResource(id = R.string.onboarding_notifications_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = stringResource(id = R.string.onboarding_notifications_description),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(end = 72.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.reminder_toggle_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(id = R.string.reminder_toggle_description),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = onReminderToggle,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.onboarding_notifications_hint),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    if (showPermissionRationale) {
                        Text(
                            text = stringResource(id = R.string.notifications_permission_rationale),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 0) {
                TextButton(onClick = { step -= 1 }) {
                    Text(text = stringResource(id = R.string.onboarding_back))
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
            Button(
                onClick = {
                    when (step) {
                        0 -> if (selectedRole != null) {
                            step += 1
                        }
                        1 -> if (isPartnerRoleSelected) {
                            if (!isPartnerCodeSubmitting) {
                                onboardingScope.launch {
                                    isPartnerCodeSubmitting = true
                                    when (val result = onPartnerInviteSubmit(partnerCode)) {
                                        is PartnerInviteOutcome.Success -> {
                                            partnerCodeError = null
                                            step += 1
                                        }
                                        is PartnerInviteOutcome.Error -> {
                                            partnerCodeError = when (result.reason) {
                                                PartnerInviteError.InvalidCode ->
                                                    context.getString(R.string.onboarding_partner_code_error)
                                                PartnerInviteError.RegistrationFailed ->
                                                    context.getString(R.string.settings_partner_code_registration_error)
                                            }
                                        }
                                    }
                                    isPartnerCodeSubmitting = false
                                }
                            }
                        } else {
                            selectedDate?.let {
                                onConfirmDueDate(it, false)
                                step += 1
                            }
                        }
                        2 -> onFinish()
                    }
                },
                enabled = canProceed
            ) {
                Text(
                    text = if (step == totalSteps - 1) {
                        stringResource(id = R.string.onboarding_finish)
                    } else {
                        stringResource(id = R.string.onboarding_next)
                    }
                )
            }
        }
    }
}

@Composable
fun SupporterInsightCard(parentHighlights: List<String>) {
    val highlights = if (parentHighlights.isNotEmpty()) {
        parentHighlights
    } else {
        BabyDevelopmentRepository.DEFAULT_PARENT_CHANGES
    }

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
                text = stringResource(id = R.string.partner_parent_focus_heading),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.partner_parent_focus_summary),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            highlights.forEachIndexed { index, highlight ->
                Text(
                    text = "• $highlight",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = if (index == 0) Modifier else Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun HighlightCard(title: String, highlights: List<String>) {
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
            highlights.forEachIndexed { index, highlight ->
                Text(
                    text = "• $highlight",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = if (index == 0) Modifier else Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ChecklistCard(
    title: String,
    subtitle: String,
    emptyStateText: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    val checklistState = remember(items) {
        mutableStateMapOf<String, Boolean>().apply {
            items.forEach { put(it, false) }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (items.isEmpty()) {
                Text(
                    text = emptyStateText,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                items.forEach { item ->
                    val isChecked = checklistState[item] ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { checklistState[item] = !isChecked },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked -> checklistState[item] = checked }
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerSupportCard(
    title: String,
    subtitle: String,
    emptyStateText: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (items.isEmpty()) {
                Text(
                    text = emptyStateText,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = if (index == 0) 0.dp else 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompanionModeBanner(
    dueDate: LocalDate?,
    dateFormatter: DateTimeFormatter,
    remindersEnabled: Boolean,
    calculatedWeek: Int?,
    modifier: Modifier = Modifier
) {
    val dueDateText = dueDate?.format(dateFormatter)
    val statusText = when {
        dueDateText == null -> stringResource(id = R.string.companion_banner_message_no_due_date)
        remindersEnabled -> stringResource(
            id = R.string.companion_banner_message_due_date_active,
            dueDateText
        )
        else -> stringResource(
            id = R.string.companion_banner_message_due_date_inactive,
            dueDateText
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(id = R.string.companion_banner_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            if (calculatedWeek != null) {
                Text(
                    text = stringResource(id = R.string.companion_banner_week_context, calculatedWeek),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun GrowthTrendCard(selectedWeek: Int) {
    val growthPoints = remember { FetalGrowthTrends.weeklyGrowth }
    val highlightPoint = remember(selectedWeek) {
        FetalGrowthTrends.findClosestWeek(selectedWeek)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.growth_trends_heading),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.growth_trends_overview),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            GrowthTrendChart(points = growthPoints, highlightPoint = highlightPoint)
            highlightPoint?.let { point ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.week_selector_label, point.week),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.growth_trends_length, point.lengthCm),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(id = R.string.growth_trends_weight, point.weightG),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun GrowthTrendChart(
    points: List<FetalGrowthPoint>,
    highlightPoint: FetalGrowthPoint?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val paddingHorizontal = with(density) { 16.dp.toPx() }
    val paddingVertical = with(density) { 20.dp.toPx() }
    val axisStrokeWidth = with(density) { 1.dp.toPx() }
    val lineStrokeWidth = with(density) { 3.dp.toPx() }
    val highlightRadius = with(density) { 6.dp.toPx() }
    val highlightOutlineRadius = with(density) { 12.dp.toPx() }
    val highlightOutlineStroke = with(density) { 2.dp.toPx() }
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
    val lineColor = MaterialTheme.colorScheme.primary
    val highlightColor = MaterialTheme.colorScheme.secondary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        if (points.isEmpty()) return@Canvas

        val usableWidth = size.width - paddingHorizontal * 2f
        val usableHeight = size.height - paddingVertical * 2f
        if (usableWidth <= 0f || usableHeight <= 0f) return@Canvas

        val minLength = points.minOf { it.lengthCm }
        val maxLength = points.maxOf { it.lengthCm }
        val lengthRange = (maxLength - minLength).takeIf { it != 0f } ?: 1f

        val xStep = if (points.size > 1) usableWidth / (points.size - 1) else 0f

        fun offsetFor(index: Int, length: Float): Offset {
            val x = paddingHorizontal + (xStep * index)
            val normalized = (length - minLength) / lengthRange
            val y = paddingVertical + usableHeight - (normalized * usableHeight)
            return Offset(x, y)
        }

        drawLine(
            color = axisColor,
            start = Offset(paddingHorizontal, paddingVertical + usableHeight),
            end = Offset(paddingHorizontal + usableWidth, paddingVertical + usableHeight),
            strokeWidth = axisStrokeWidth
        )

        drawLine(
            color = axisColor,
            start = Offset(paddingHorizontal, paddingVertical),
            end = Offset(paddingHorizontal, paddingVertical + usableHeight),
            strokeWidth = axisStrokeWidth
        )

        val trendPath = Path()
        points.forEachIndexed { index, point ->
            val pointOffset = offsetFor(index, point.lengthCm)
            if (index == 0) {
                trendPath.moveTo(pointOffset.x, pointOffset.y)
            } else {
                trendPath.lineTo(pointOffset.x, pointOffset.y)
            }
        }

        drawPath(
            path = trendPath,
            color = lineColor,
            style = Stroke(width = lineStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        highlightPoint?.let { point ->
            val highlightIndex = points.indexOfFirst { it.week == point.week }
            if (highlightIndex >= 0) {
                val highlightOffset = offsetFor(highlightIndex, point.lengthCm)
                drawCircle(
                    color = highlightColor,
                    radius = highlightOutlineRadius,
                    center = highlightOffset,
                    style = Stroke(width = highlightOutlineStroke)
                )
                drawCircle(
                    color = highlightColor,
                    radius = highlightRadius,
                    center = highlightOffset
                )
            }
        }
    }
}
