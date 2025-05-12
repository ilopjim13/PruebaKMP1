package org.dam2.fctcting.viewmodels

import androidx.compose.ui.graphics.Color

import ir.ehsannarmani.compose_charts.models.Pie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.dam2.fctcting.database.Database
import org.dam2.fctcting.model.Activity
import org.dam2.fctcting.model.Employee
import org.dam2.fctcting.model.EmployeeActivity
import org.dam2.fctcting.model.EmployeeWO
import org.dam2.fctcting.model.Project
import org.dam2.fctcting.model.ProjectTimeCode
import org.dam2.fctcting.model.TimeCode
import org.dam2.fctcting.model.WorkOrder
import org.dam2.fctcting.model.dto.TimeCodeDTO
import org.dam2.fctcting.utils.DTOConverter.toDTO

object DataViewModel {

    var today = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    var currentToday = MutableStateFlow(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)

    var employee = Employee(-1, "", "", "", "", null, -1)


    // Variables comunes a varias pantallas
    private var _currentHours = MutableStateFlow(0)
    val currentHours: StateFlow<Int> = _currentHours

    private var _currentMonth = MutableStateFlow("0")
    private var _currentYear = MutableStateFlow("0")

    private var _pieList = MutableStateFlow(mutableListOf<Pie>())
    val pieList: StateFlow<MutableList<Pie>> = _pieList


    // Listas con todos los datos de las tablas
    private val _timeCodes = MutableStateFlow<List<TimeCodeDTO>>(emptyList())
    val timeCodes: StateFlow<List<TimeCodeDTO>> = _timeCodes

    private val _employeeActivities =
        MutableStateFlow<MutableList<EmployeeActivity>>(mutableListOf())
    val employeeActivities: StateFlow<MutableList<EmployeeActivity>> = _employeeActivities

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    //val projects: StateFlow<List<Project>> = _projects

    private val _projectTimeCodes = MutableStateFlow<List<ProjectTimeCode>>(emptyList())
    val projectTimeCodes: StateFlow<List<ProjectTimeCode>> = _projectTimeCodes

    private val _workOrders = MutableStateFlow<List<WorkOrder>>(emptyList())
    val workOrders: StateFlow<List<WorkOrder>> = _workOrders

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities

    private val _employeeWO = MutableStateFlow<List<EmployeeWO>>(emptyList())
    val employeeWO: StateFlow<List<EmployeeWO>> = _employeeWO

    // Carga de los datos de la base de datos
    init {
        cargarTimeCodes()
        cargarEmployeeActivities()
        cargarProjects()
        cargarProjectsTimeCode()
        cargarWorkOrders()
        cargarActivities()
        cargarEmployeeWO()
    }

    private fun cargarTimeCodes() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<TimeCode>("TimeCode")
            _timeCodes.value = datos.map { it.toDTO() }

        }
    }
    private fun cargarEmployeeActivities() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<EmployeeActivity>("EmployeeActivity")
            _employeeActivities.value = datos.toMutableList()
        }
    }

    private fun cargarProjects() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<Project>("Project")
            _projects.value = datos
        }
    }

    private fun cargarProjectsTimeCode() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<ProjectTimeCode>("ProjectTimeCode")
            _projectTimeCodes.value = datos
        }
    }
    private fun cargarWorkOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<WorkOrder>("WorkOrder")
            _workOrders.value = datos
        }
    }

    private fun cargarActivities() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<Activity>("Activity")
            _activities.value = datos

        }
    }

    private fun cargarEmployeeWO() {
        CoroutineScope(Dispatchers.IO).launch {
            val datos = Database.getData<EmployeeWO>("EmployeeWO")
            _employeeWO.value = datos
        }
    }


    // Funciones comunes a varias pantallas
    fun getHours() {
        _currentHours.value = 0
        employeeActivities.value
            .filter { employee.idEmployee == it.idEmployee }
            .forEach {
                _currentHours.value += it.time.toInt()
            }
    }

    fun getMonth() {
        _currentMonth.value = today.value.monthNumber.toString()
        _currentYear.value = today.value.year.toString()
    }

    fun resetToday() {
        today.value = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        changeMonth(today.value.monthNumber.toString(), today.value.year.toString())
    }

    fun changeMonth(month: String, year: String) {
        _currentMonth.value = month
        _currentYear.value = year
    }

    fun getPie() {
        val pies = mutableListOf<Pie>()
        val dateFilter =
            if (today.value.monthNumber.toString().length == 1) "0${_currentMonth.value}"
            else _currentMonth.value
        employeeActivities.value
            .filter {
                employee.idEmployee == it.idEmployee
                        && it.date.split("-")[1] == dateFilter
                        && it.date.split("-")[0] == _currentYear.value
            }
            .forEach { activity ->
                createPie(pies, activity)
            }
        _pieList.value = pies
    }

    private fun createPie(pies:MutableList<Pie>, activity:EmployeeActivity) {
        val timeCode = timeCodes.value.find { time -> time.idTimeCode == activity.idTimeCode }
        if (timeCode != null) {
            val pie = pies.find { p -> p.label == timeCode.idTimeCode.toString() }

            if (pie != null) {
                val timePie = pie.data + activity.time
                pies.remove(pie)
                pies.add(
                    Pie(
                        label = timeCode.idTimeCode.toString(),
                        data = timePie,
                        color = Color(timeCode.color)
                    )
                )
            } else {
                pies.add(
                    Pie(
                        label = timeCode.idTimeCode.toString(),
                        data = activity.time.toDouble(),
                        color = Color(timeCode.color)
                    )
                )
            }
        }
    }
}