package org.dam2.fctcting.viewmodels


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.dam2.fctcting.model.Activity
import org.dam2.fctcting.model.ProjectTimeCode
import org.dam2.fctcting.model.dto.ProjectTimeCodeDTO
import org.dam2.fctcting.viewmodels.DataViewModel.employee
import org.dam2.fctcting.viewmodels.DataViewModel.employeeWO

class DayMenuViewModel {
    // Cargamos los projectsTimeCode de la base de datos
    private val projectTimeCodes: StateFlow<List<ProjectTimeCode>> = DataViewModel.projectTimeCodes
    private val activities: StateFlow<List<Activity>> = DataViewModel.activities

    // Variables para que se guarden las horas y el comentario
    private var _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment

    private var _hours = MutableStateFlow(8)
    val hours:StateFlow<Int> = _hours

    // Variables para la selección de TimeCodes
    private var _timeCode = MutableStateFlow(0)
    val timeCode:StateFlow<Int> = _timeCode

    private var _timeCodeSelected:MutableStateFlow<Int?> = MutableStateFlow(null)
    val timeCodeSeleccionado:StateFlow<Int?> = _timeCodeSelected

    // Variables para la selección de WorkOrders
    private var _workOrder = MutableStateFlow("")
    val workOrder:StateFlow<String> = _workOrder

    private var _workSelected:MutableStateFlow<String?> = MutableStateFlow(null)
    val workSelected:StateFlow<String?> = _workSelected

    val workOrderTimeCodeDTO = MutableStateFlow(mutableListOf<ProjectTimeCodeDTO>())

    // Variables para la selección de los Activities
    private var _activity = MutableStateFlow("")
    val activity:StateFlow<String> = _activity

    private var _activitySelected:MutableStateFlow<String?> = MutableStateFlow(null)
    val activitySelected:StateFlow<String?> = _activitySelected

    val activityTimeCode = MutableStateFlow(mutableListOf<ProjectTimeCodeDTO>())


    fun onComment(newComment:String) {
        _comment.value = newComment
    }

    fun onHours(newHour:Int) {
        _hours.value = newHour
    }

    fun onTimeCode(newTimeCode:Int) {
        _timeCode.value = newTimeCode
    }

    fun onTimeSelected(newTimeCode:Int?) {
        _timeCodeSelected.value = newTimeCode
    }

    fun onWorkOrder(newWorkOrder:String) {
        _workOrder.value = newWorkOrder
    }

    fun onWorkSelected(newProject:String?) {
        _workSelected.value = newProject
    }

    fun onActivity(newActivity:String) {
        _activity.value = newActivity
    }

    fun onActivitySelected(newActivity:String) {
        _activitySelected.value = newActivity
    }

    fun generateWorkOrders() {
        val workOrdersPorTimeCode = mutableListOf<ProjectTimeCodeDTO>()
        val timeCodeProcesados = mutableSetOf<Int>()

        projectTimeCodes.value.forEach { code ->
            if (code.idTimeCode !in timeCodeProcesados) {
                timeCodeProcesados.add(code.idTimeCode)

                // Filtramos los proyectos que tienen este timeCode
                val proyectosAsociados = projectTimeCodes.value
                    .filter { it.idTimeCode == code.idTimeCode }
                    .map { it.idProject }

                // Obtenemos los workOrders de esos proyectos
                val workOrdersAsociados = DataViewModel.workOrders.value
                    .filter { it.idProject in proyectosAsociados }
                    .map { it.idWorkOrder }

                // Filtramos por los workOrders en los que participa el empleado
                val workOrdersEmpleado = employeeWO.value
                    .filter { it.idWorkOrder in workOrdersAsociados && it.idEmployee == employee.idEmployee }
                    .map { it.idWorkOrder }

                val dto = ProjectTimeCodeDTO(code.idTimeCode, workOrdersEmpleado.toMutableList())
                workOrdersPorTimeCode.add(dto)
            }
        }

        workOrderTimeCodeDTO.value = workOrdersPorTimeCode
    }

    fun generateActivities() {
        val activitiesPorTimeCode = mutableListOf<ProjectTimeCodeDTO>()
        val timeCodeProcesados = mutableSetOf<Int>()

        activities.value.forEach { activity ->
            if (activity.idTimeCode !in timeCodeProcesados) {

                // Filtramos los activities que tienen este timeCode
                val activitiesTimeCode = activities.value
                    .filter { it.idTimeCode == activity.idTimeCode }
                    .map { it.idActivity.toString() }

                val dto = ProjectTimeCodeDTO(activity.idTimeCode, activitiesTimeCode.toMutableList())
                activitiesPorTimeCode.add(dto)
            }
        }
        activityTimeCode.value = activitiesPorTimeCode

//        projectTimeCodes.value.forEach { code ->
//            if (code.idTimeCode !in timeCodeProcesados) {
//                timeCodeProcesados.add(code.idTimeCode)
//
//                // Filtramos los proyectos que tienen este timeCode
//                val proyectosAsociados = projectTimeCodes.value
//                    .filter { it.idTimeCode == code.idTimeCode }
//                    .map { it.idProject }
//
//                // Obtenemos los workOrders de esos proyectos
//                val workOrdersAsociados = DataViewModel.workOrders.value
//                    .filter { it.idProject in proyectosAsociados }
//                    .map { it.idWorkOrder }
//
//                // Filtramos por los workOrders en los que participa el empleado
//                val workOrdersEmpleado = employeeWO.value
//                    .filter { it.idWorkOrder in workOrdersAsociados && it.idEmployee == employee.idEmployee }
//                    .map { it.idWorkOrder }
//
//                val dto = ProjectTimeCodeDTO(code.idTimeCode, workOrdersEmpleado.toMutableList())
//                activitiesPorTimeCode.add(dto)
//            }
//        }
//
//        activityTimeCode.value = activitiesPorTimeCode
    }

}