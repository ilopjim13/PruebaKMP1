package com.es.appmovil.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import ir.ehsannarmani.compose_charts.models.Bars
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.dam2.fctcting.database.Database
import org.dam2.fctcting.model.EmployeeActivity
import org.dam2.fctcting.model.dto.TimeCodeDTO
import org.dam2.fctcting.viewmodels.DataViewModel
import org.dam2.fctcting.viewmodels.DataViewModel.changeMonth
import org.dam2.fctcting.viewmodels.DataViewModel.employeeActivities
import org.dam2.fctcting.viewmodels.DataViewModel.getPie
import org.dam2.fctcting.viewmodels.DataViewModel.today

/**
 * Clase viewmodel para el calendario, donde guardaremos los datos del calendario y sus posibles funciones
 */
class CalendarViewModel {

    private val _bars = MutableStateFlow<List<Bars>>(emptyList())
    val bars: StateFlow<List<Bars>> = _bars

    val timeCodes: StateFlow<List<TimeCodeDTO>> = DataViewModel.timeCodes

    private var _employeeActivity = MutableStateFlow(employeeActivities.value)
    val employeeActivity: StateFlow<List<EmployeeActivity>> = _employeeActivity

    private var _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private var _reset = MutableStateFlow(false)


    fun changeDialog(bool: Boolean) {
        _showDialog.value = bool
    }

    /**
     * Función para cambiar el mes que se muestra en el calendario
     * @param month Numero de meses que se van a cambiar hacia atras
     */
    fun onMonthChangePrevious(month: DatePeriod) {
        today.value = today.value.minus(month)
        changeMonth(today.value.monthNumber.toString(), today.value.year.toString())
        getPie()
        _reset.value = true
    }

    /**
     * Función para cambiar el mes que se muestra en el calendario
     * @param month Numero de meses que se van a cambiar hacia delante
     */
    fun onMonthChangeFordward(month: DatePeriod) {
        today.value = today.value.plus(month)
        changeMonth(today.value.monthNumber.toString(), today.value.year.toString())
        getPie()
        _reset.value = true
    }

    private fun resetBars() {
        _bars.value = listOf(Bars(
            label = "",
            values = listOf(
                Bars.Data(
                    label = "",
                    value = 0.0,
                    color = SolidColor(Color.Black)
                )
            )
        ))
    }

    fun addEmployeeActivity(employeeActivity: EmployeeActivity){
        val filtro = employeeActivities.value.find { it.date == employeeActivity.date && it.idTimeCode == employeeActivity.idTimeCode }
        if (filtro == null) {
            if (employeeActivity.time != 0f) {
                employeeActivities.value.add(employeeActivity)
                CoroutineScope(Dispatchers.IO).launch {
                    Database.addEmployeeActivity(employeeActivity)
                }
            }
        }
        else {
            employeeActivities.value.remove(filtro)
            if (employeeActivity.time != 0f) {
                employeeActivities.value.add(employeeActivity)
                CoroutineScope(Dispatchers.IO).launch {
                    Database.updateEmployeeActivity(employeeActivity)
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    Database.deleteEmployeeActivity(employeeActivity)
                }
            }
        }
        getPie()
    }

    fun generarBarrasPorDia(fechaSeleccionada: LocalDate) {
        val timeCodeMap = timeCodes.value.associateBy { it.idTimeCode }

        val actividadesDelDia = employeeActivity.value.filter {
            LocalDate.parse(it.date) == fechaSeleccionada
        }

        if (actividadesDelDia.isEmpty() || _reset.value) {
            resetBars()
            _reset.value = false
            return
        }

        val dataPorTimeCode = actividadesDelDia.groupBy { it.idTimeCode }
            .mapNotNull { (idTimeCode, listaActividades) ->
                val timeCode = timeCodeMap[idTimeCode] ?: return@mapNotNull null
                Bars.Data(
                    label = timeCode.desc,
                    value = listaActividades.sumOf { it.time.toDouble() },
                    color = SolidColor(Color(timeCode.color))
                )
            }

        _bars.value = listOf(
            Bars(
                label = fechaSeleccionada.dayOfMonth.toString(),
                values = dataPorTimeCode
            )
        )
    }
}