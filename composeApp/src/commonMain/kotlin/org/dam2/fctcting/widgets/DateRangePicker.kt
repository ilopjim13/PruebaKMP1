package org.dam2.fctcting.widgets

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

@Composable
fun DatePickerFieldToModal(modifier: Modifier = Modifier, day: LocalDate, onGetListDates:(SnapshotStateList<LocalDate>)->Unit) {
    var selectedStartDate by remember { mutableStateOf<Long?>(null) }
    var selectedEndDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }
    val dateTime = day.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()


    val displayText = when {
        selectedStartDate != null && selectedEndDate != null -> {
            "${convertMillisToDate(selectedStartDate!!)} - ${convertMillisToDate(selectedEndDate!!)}"
        }

        selectedStartDate != null -> {
            convertMillisToDate(selectedStartDate!!)
        }

        else -> convertMillisToDate(dateTime)
    }

    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        readOnly = true,
        placeholder = { Text("Selecciona fecha") },
        trailingIcon = {
            Icon(Icons.Default.DateRange, contentDescription = "Selccionar rango de fecha")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateRangeSelected = { startDate, endDate ->
                selectedStartDate = startDate
                selectedEndDate = endDate

                if (startDate != null && endDate != null) {
                    val start = Instant.fromEpochMilliseconds(startDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val end = Instant.fromEpochMilliseconds(endDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val listOfDates = generateDatesBetween(start, end)
                    onGetListDates(listOfDates)
                } else if (startDate != null){
                    val start = Instant.fromEpochMilliseconds(startDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val end = Instant.fromEpochMilliseconds(startDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val listOfDates = generateDatesBetween(start, end)
                    onGetListDates(listOfDates)
                }
            },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateRangeSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateRangePickerState = rememberDateRangePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val instant = Instant.fromEpochMilliseconds(millis)
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "${localDate.dayOfMonth}/${localDate.monthNumber}/${localDate.year}"
}

fun generateDatesBetween(start: LocalDate, end: LocalDate): SnapshotStateList<LocalDate> {
    val dates = mutableStateListOf<LocalDate>()
    var current = start
    while (current <= end) {
        dates.add(current)
        current = current.plus(1, DateTimeUnit.DAY)
    }
    return dates
}