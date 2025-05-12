package org.dam2.fctcting.widgets

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line
import org.dam2.fctcting.viewmodels.AnualViewModel

@Composable
fun TeoricoAnual(anualViewModel: AnualViewModel) {
    val calendar = anualViewModel.generarCalendar()
    LineChart(
        modifier = Modifier.height(300.dp),
        data = remember {
            listOf(
                Line(
                    label = "Teórico",
                    values = anualViewModel.calcularHorasTeoricasPorMes(calendar),
                    color = SolidColor(Color(0xFFF4A900)),
                    firstGradientFillColor = Color(0xFFFFCF65).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                ),
                Line(
                    label = "Horas realizadas",
                    values = anualViewModel.calcularHorasPorMes(),
                    color = SolidColor(Color(0xFF23af92)),
                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                    secondGradientFillColor = Color.Transparent,
                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                    gradientAnimationDelay = 1000,
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                    dotProperties = DotProperties(
                        enabled = true,
                        color = SolidColor(Color.White),
                        strokeWidth = 2.dp,
                        radius = 5.dp,
                        strokeColor = SolidColor(Color.Black),
                    )
                )
            )
        },
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 500L
        }),
        maxValue = 200.0
    )
}

