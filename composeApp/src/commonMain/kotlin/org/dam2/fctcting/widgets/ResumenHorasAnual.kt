package org.dam2.fctcting.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dam2.fctcting.viewmodels.ResumeViewmodel

@Composable
fun ResumenHorasAnual(resumeViewmodel: ResumeViewmodel) {
    // Creamos la variable de modificador base de los box
    val boxModifier = Modifier.height(20.dp).clip(shape = RoundedCornerShape(5.dp))

    val timeActivity = resumeViewmodel.getTimeActivity()

    Row {
        Text("Resumen anual", fontWeight = FontWeight.SemiBold)
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "ArrowForward")
    }

    Spacer(Modifier.size(20.dp))

    // Generamos la estructura
    ElevatedCard(
        colors = CardColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black),
        modifier = Modifier.size(180.dp, 63.dp),
        elevation = CardDefaults.elevatedCardElevation(5.dp)
    ) {
        Spacer(Modifier.size(10.dp))
        Row(
            Modifier.padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val totalHours = timeActivity.value.values.sum()

            timeActivity.value.forEach { (color, hour) ->
                if (hour != 0f) {
                    Box(
                        boxModifier.background(color = Color(color)).weight(hour / totalHours)
                    )
                    Spacer(Modifier.width(5.dp))
                }
            }

        }
        Spacer(Modifier.size(5.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            timeActivity.value.forEach { (timeCode, hour) ->
                if (hour != 0f) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .background(Color(timeCode), shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${hour.toInt()}", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
        }
        Spacer(Modifier.size(10.dp))
    }
}