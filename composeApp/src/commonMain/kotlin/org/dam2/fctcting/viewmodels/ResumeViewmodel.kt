package org.dam2.fctcting.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.dam2.fctcting.model.dto.TimeCodeDTO
import org.dam2.fctcting.viewmodels.DataViewModel.changeMonth
import org.dam2.fctcting.viewmodels.DataViewModel.getPie
import org.dam2.fctcting.viewmodels.DataViewModel.today

class ResumeViewmodel {

    private val employeeActivities = MutableStateFlow(DataViewModel.employeeActivities.value)
    private val employee = DataViewModel.employee
    private val timeCodes: StateFlow<List<TimeCodeDTO>> = DataViewModel.timeCodes

    private var _dailyHours = MutableStateFlow(8)
    val dailyHours: StateFlow<Int> = _dailyHours

    private var _currentDay = MutableStateFlow(getDays())
    val currentDay: StateFlow<Int> = _currentDay


    fun getWeekDaysWithNeighbors(year: Int, month: Int, day: Int): List<LocalDate> {
        val selectedDate = LocalDate(year, month, day)
        val dayOfWeek = selectedDate.dayOfWeek.isoDayNumber // 1 (Lunes) a 7 (Domingo)

        val firstDayOfWeek = selectedDate.minus(dayOfWeek - 1, DateTimeUnit.DAY)

        return (0..6).map { firstDayOfWeek.plus(it, DateTimeUnit.DAY) }
    }

    fun onMonthChangePrevious(period: DatePeriod) {
        today.value = today.value.minus(period)
        changeMonth(today.value.monthNumber.toString(), today.value.year.toString())
        getPie()
    }

    fun onWeekChangeFordward(period: DatePeriod) {
        today.value = today.value.plus(period)
        changeMonth(today.value.monthNumber.toString(), today.value.year.toString())
        getPie()
    }

    
    private fun getDays():Int {
        var fc = ""
        var days = 0
        employeeActivities.value
            .filter { employee.idEmployee == it.idEmployee }
            .forEach {
                if (fc != it.date) {
                    days += 1
                    fc = it.date
                }
            }
        return days
    }

    fun getLegend(): MutableState<MutableMap<String, Long>> {
        val legend = mutableStateOf(mutableMapOf<String, Long>())

        timeCodes.value.forEach {
            legend.value[it.desc.take(10)] = it.color
        }
        return legend
    }

    fun getTimeActivity() : MutableState<MutableMap<Long, Float>> {
        val timeActivity = mutableStateOf(mutableMapOf<Long, Float>())

        employeeActivities.value
            .filter { employee.idEmployee == it.idEmployee }
            .forEach {
                val timeCode = timeCodes.value.find { time -> time.idTimeCode == it.idTimeCode }
                if (timeCode != null) {
                    if (timeActivity.value.containsKey(timeCode.color)) {
                        val hours = timeActivity.value[timeCode.color]?.plus(it.time)
                        timeActivity.value[timeCode.color] = hours ?: 0f
                    } else {
                        timeActivity.value[timeCode.color] = it.time
                    }
                }
        }
        return timeActivity
    }

    fun getDayActivity(): MutableState<MutableMap<String, MutableList<Color>>>  {
        val dayActivity = mutableStateOf(mutableMapOf<String, MutableList<Color>>())

        employeeActivities.value
            .filter { employee.idEmployee == it.idEmployee }
            .forEach {
                val timeCode = timeCodes.value.find { time -> time.idTimeCode == it.idTimeCode }
                timeCode?.let { tc ->
                    val currentList = dayActivity.value[it.date] ?: mutableListOf()
                    currentList.add(Color(tc.color))
                    dayActivity.value[it.date] = currentList
                }
            }
        return dayActivity
    }

}