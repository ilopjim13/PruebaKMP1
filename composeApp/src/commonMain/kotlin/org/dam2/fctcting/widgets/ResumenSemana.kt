package org.dam2.fctcting.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import org.dam2.fctcting.viewmodels.DataViewModel.currentToday
import org.dam2.fctcting.viewmodels.DataViewModel.resetToday
import org.dam2.fctcting.viewmodels.DataViewModel.today
import org.dam2.fctcting.viewmodels.ResumeViewmodel

@Composable
fun ResumenSemana(resumeViewmodel: ResumeViewmodel) {

    val listState = rememberLazyListState()
    val dayModifier = Modifier.padding(end = 5.dp, start = 5.dp).background(Color.White)
    .width(65.dp).height(60.dp)

    val daysActivity = resumeViewmodel.getDayActivity()
    val fechaActual by today.collectAsState()
    var monthChangeFlag = true

    val semana = resumeViewmodel.getWeekDaysWithNeighbors(
        fechaActual.year,
        fechaActual.monthNumber,
        fechaActual.dayOfMonth
    )

    val todayIndex = semana.indexOf(fechaActual)
    val visibleItems = 4
    val targetIndex = (todayIndex - visibleItems / 2).coerceIn(0, semana.size - visibleItems)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (monthChangeFlag) {
                monthChangeFlag = false
                resumeViewmodel.onMonthChangePrevious(DatePeriod(days = 7))
            }
        }) {
            Text("<", fontSize = 20.sp)
        }
        Text(
            "${monthNameInSpanish(fechaActual.month.name)} ${fechaActual.year}",
            fontSize = 20.sp,
            modifier = Modifier.clickable { resetToday() })
        IconButton(onClick = {
            if (monthChangeFlag) {
                monthChangeFlag = false
                resumeViewmodel.onWeekChangeFordward(DatePeriod(days = 7))
            }
        }) {
            Text(">", fontSize = 20.sp)
        }
    }
    ElevatedCard(
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth().height(70.dp),
        elevation = CardDefaults.elevatedCardElevation(5.dp)

    ){
        LaunchedEffect(Unit) {
            listState.animateScrollToItem(targetIndex)
        }

        LazyRow(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, state = listState) {
            items(semana.size) {
                val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                Days(semana[it], dayModifier, diasSemana[it], daysActivity.value)
            }
        }
    }
}

@Composable
fun Days(day:LocalDate, dayModifier: Modifier, diaSemana:String, daysActivity:MutableMap<String, MutableList<Color>>) {
    val todayModifier = if (day == currentToday.value) {
        dayModifier.border(width = 2.dp, color = Color(0xFF000000))
    }else{
        dayModifier
    }

    var colors = mutableListOf<Color>()

    daysActivity.forEach { (fc, color) ->
        if(day.toString() == fc) colors = color
    }

    val backgroundModifier = if (colors.size >= 2) {
        todayModifier.background(Brush.linearGradient(colors))
    } else if (colors.isNotEmpty()) {
        todayModifier.background(colors[0]) // Usamos un solo color si solo hay uno
    } else {
        todayModifier
    }

    Column(
        modifier = backgroundModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day.dayOfMonth.toString())
        Text(diaSemana)
    }
}



