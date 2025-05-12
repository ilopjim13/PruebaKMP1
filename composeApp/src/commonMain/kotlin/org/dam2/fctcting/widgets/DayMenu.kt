package org.dam2.fctcting.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.es.appmovil.viewmodel.CalendarViewModel
import kotlinx.datetime.LocalDate
import org.dam2.fctcting.model.EmployeeActivity
import org.dam2.fctcting.model.dto.ProjectTimeCodeDTO
import org.dam2.fctcting.viewmodels.DataViewModel.employee
import org.dam2.fctcting.viewmodels.DayMenuViewModel


/**
 * Funcion que muestra una ventana deslizante para reallenar los campos de un dia
 *
 *@param showDialog Muestra o no el dialogo
 * @param onChangeDialog Función que cambia el valor de showDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDialog(
    showDialog: Boolean,
    day: LocalDate,
    dayMenuViewModel: DayMenuViewModel,
    calendarViewModel: CalendarViewModel,
    onChangeDialog: (Boolean) -> Unit
) {
    dayMenuViewModel.generateWorkOrders()
    dayMenuViewModel.generateActivities()
    val sheetState = rememberModalBottomSheetState()
    val comentario by dayMenuViewModel.comment.collectAsState()
    val horas by dayMenuViewModel.hours.collectAsState()

    val timeCode by dayMenuViewModel.timeCode.collectAsState()
    val timeCodeSeleccionado by dayMenuViewModel.timeCodeSeleccionado.collectAsState()

    val workOrder by dayMenuViewModel.workOrder.collectAsState()
    val workOrdersTimeCodes by dayMenuViewModel.workOrderTimeCodeDTO.collectAsState()
    val workSeleccionado by dayMenuViewModel.workSelected.collectAsState()

    val activity by dayMenuViewModel.activity.collectAsState()
    val activitiesTimeCodes by dayMenuViewModel.activityTimeCode.collectAsState()
    val activitySeleccionado by dayMenuViewModel.activitySelected.collectAsState()


    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                onChangeDialog(false)
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight()
        ) {

            val dates = remember { mutableStateOf(listOf(day)) }

            DatePickerFieldToModal(Modifier.padding(horizontal = 16.dp), day) {
                dates.value = it
            }
            Spacer(Modifier.size(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                NumberInputField(value = horas, onValueChange = { dayMenuViewModel.onHours(it) })
                ProyectoYTimeCodeSelector(
                    workOrdersTimeCodes,
                    { dayMenuViewModel.onTimeCode(it) },
                    timeCodeSeleccionado,
                    { dayMenuViewModel.onTimeSelected(it) },
                    { dayMenuViewModel.onWorkSelected(it) })
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ProjectsSelected(
                    workOrdersTimeCodes,
                    timeCodeSeleccionado,
                    workSeleccionado,
                    "WorkOrder",
                    Modifier.weight(1f).padding(start = 16.dp, top = 8.dp),
                    { dayMenuViewModel.onWorkOrder(it) },
                    { dayMenuViewModel.onWorkSelected(it) }
                )
                ProjectsSelected(
                    activitiesTimeCodes,
                    timeCodeSeleccionado,
                    activitySeleccionado,
                    "Activity",
                    Modifier.weight(1f).padding(end = 16.dp, top = 8.dp),
                    { dayMenuViewModel.onActivity(it) },
                    { dayMenuViewModel.onActivitySelected(it) })
            }


            OutlinedTextField(
                value = comentario,
                onValueChange = { dayMenuViewModel.onComment(it) },
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(100.dp)
            )

            Save(timeCode, workOrder, activity, { onChangeDialog(false) }, {
                dates.value.forEach { date ->
                    calendarViewModel.addEmployeeActivity(
                        EmployeeActivity(
                            employee.idEmployee,
                            workOrder,
                            timeCode,
                            activity.toIntOrNull() ?: 59,
                            horas.toFloat(),
                            date.toString(),
                            comentario
                        )
                    )
                }
            })
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun Save(
    timeCode: Int,
    workOrder: String,
    activity: String,
    onChangeDialog: () -> Unit,
    onSaveEmploye: () -> Unit
) {
    Button(
        onClick = {
            if (timeCode != 0 && workOrder.isNotBlank() && activity.isNotBlank()) {
                onChangeDialog()
                onSaveEmploye()
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFF5B014),
            contentColor = Color.Black
        )
    ) {
        Text("Guardar")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectoYTimeCodeSelector(
    proyectTimecodesDTO: List<ProjectTimeCodeDTO>,
    onTimeCodeSelected: (Int) -> Unit,
    timeCodeSeleccionado: Int?,
    onTimeCodeChange: (Int) -> Unit,
    onProyectChange: (String?) -> Unit,
) {
    var expandirTimeCode by remember { mutableStateOf(false) }

    // Dropdown de TimeCodes
    ExposedDropdownMenuBox(
        expanded = expandirTimeCode,
        onExpandedChange = { expandirTimeCode = !expandirTimeCode },
        modifier = Modifier.padding(end = 16.dp)
    ) {
        OutlinedTextField(
            value = timeCodeSeleccionado?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Seleccione TimeCode") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirTimeCode) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expandirTimeCode,
            onDismissRequest = { expandirTimeCode = false }
        ) {
            proyectTimecodesDTO.sortedBy { it.idTimeCode }.map { it.idTimeCode }.distinct()
                .forEach { timeCode ->
                    DropdownMenuItem(
                        text = { Text(timeCode.toString()) },
                        onClick = {
                            onTimeCodeChange(timeCode)
                            onProyectChange(null)
                            onTimeCodeSelected(timeCode)
                            expandirTimeCode = false
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsSelected(
    proyectTimecodesDTO: List<ProjectTimeCodeDTO>,
    timeCodeSeleccionado: Int?,
    proyectoSeleccionado: String?,
    placeholder:String,
    modifier: Modifier,
    onChangeProyect: (String) -> Unit,
    onProjectSelected: (String) -> Unit
) {
    var expandirProyecto by remember { mutableStateOf(false) }

    // Una vez que se selecciona el timeCode, filtrar los proyectos asociados
    val proyectosDisponibles = proyectTimecodesDTO
        .firstOrNull { it.idTimeCode == timeCodeSeleccionado }
        ?.projects
        .orEmpty()

    // Dropdown de Proyectos (solo si ya se eligió un TimeCode)
    ExposedDropdownMenuBox(
        expanded = expandirProyecto,
        onExpandedChange = { expandirProyecto = !expandirProyecto },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = proyectoSeleccionado ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(placeholder) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirProyecto) },
            modifier = Modifier.menuAnchor(),
            enabled = timeCodeSeleccionado != null
        )

        ExposedDropdownMenu(
            expanded = expandirProyecto,
            onDismissRequest = { expandirProyecto = false }
        ) {
            proyectosDisponibles.forEach { proyecto ->
                DropdownMenuItem(
                    text = { Text(proyecto) },
                    onClick = {
                        onChangeProyect(proyecto)
                        onProjectSelected(proyecto)
                        expandirProyecto = false
                    }
                )
            }
        }
    }
}


@Composable
fun NumberInputField(
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        // TextField numérico
        OutlinedTextField(
            value = value.toString(),
            onValueChange = {
                val num = it.toIntOrNull()
                if (num != null) {
                    onValueChange(num)
                    if (num > 12) onValueChange(12)
                }
                if (num == null) onValueChange(0)
            },
            label = { Text("Horas") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(100.dp).padding(start = 16.dp)
        )
        Column {
            Button(
                onClick = { if (value < 12) onValueChange(value + 1) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier.size(40.dp, 25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Plus"
                )
            }
            Spacer(Modifier.size(5.dp))
            Button(
                onClick = { if (value > 0) onValueChange(value - 1) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier.size(40.dp, 25.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Minus",
                    modifier = Modifier.size(25.dp)
                )
            }

        }
    }
}
