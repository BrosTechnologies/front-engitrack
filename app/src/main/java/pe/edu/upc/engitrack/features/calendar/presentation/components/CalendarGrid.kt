package pe.edu.upc.engitrack.features.calendar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.engitrack.features.calendar.presentation.CalendarProject
import pe.edu.upc.engitrack.features.projects.domain.models.Priority
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarGrid(
    projects: List<CalendarProject>,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    
    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    
    val dayNames = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with month navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentMonth = Calendar.getInstance().apply {
                            time = currentMonth.time
                            add(Calendar.MONTH, -1)
                        }
                    }
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                }
                
                Text(
                    text = "${monthNames[currentMonth.get(Calendar.MONTH)]} ${currentMonth.get(Calendar.YEAR)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                IconButton(
                    onClick = {
                        currentMonth = Calendar.getInstance().apply {
                            time = currentMonth.time
                            add(Calendar.MONTH, 1)
                        }
                    }
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                }
            }
            
            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayNames.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar grid
            val daysInMonth = getDaysInMonth(currentMonth)
            val today = Calendar.getInstance()
            
            // Grid usando Column y Row para mayor compatibilidad
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Crear 6 filas de 7 días cada una
                for (week in 0 until 6) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (dayOfWeek in 0 until 7) {
                            val dayIndex = week * 7 + dayOfWeek
                            val day = if (dayIndex < daysInMonth.size) daysInMonth[dayIndex] else null
                            
                            CalendarDayCell(
                                day = day,
                                isToday = isSameDay(day, today),
                                projects = getProjectsForDay(day, projects),
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Calendar?,
    isToday: Boolean,
    projects: List<CalendarProject>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { /* TODO: Handle day click */ }
            .background(
                color = when {
                    isToday -> Color(0xFF007AFF).copy(alpha = 0.1f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) Color(0xFF007AFF) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (day != null) {
                // Day number
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = if (isToday) Color(0xFF007AFF) else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${day.get(Calendar.DAY_OF_MONTH)}",
                        fontSize = 14.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (isToday) Color.White else Color.Black
                    )
                }
                
                // Indicador simple de eventos  
                if (projects.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    val priorityColor = when {
                        projects.any { Priority.fromString(it.priority ?: "MEDIUM") == Priority.HIGH } -> Color(0xFFE53E3E)
                        projects.any { Priority.fromString(it.priority ?: "MEDIUM") == Priority.MEDIUM } -> Color(0xFFFF9800)
                        else -> Color(0xFF4CAF50)
                    }
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(priorityColor)
                    )
                }
            }
        }
    }
}

private fun getDaysInMonth(month: Calendar): List<Calendar?> {
    val firstDay = Calendar.getInstance().apply {
        time = month.time
        set(Calendar.DAY_OF_MONTH, 1)
    }
    
    val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = firstDay.get(Calendar.DAY_OF_WEEK) - 1
    
    val days = mutableListOf<Calendar?>()
    
    // Add empty days for the beginning of the month
    repeat(firstDayOfWeek) {
        days.add(null)
    }
    
    // Add all days of the month
    for (day in 1..daysInMonth) {
        days.add(Calendar.getInstance().apply {
            time = month.time
            set(Calendar.DAY_OF_MONTH, day)
        })
    }
    
    // Fill the rest to complete the grid (6 weeks = 42 cells)
    while (days.size < 42) {
        days.add(null)
    }
    
    return days
}

private fun isSameDay(cal1: Calendar?, cal2: Calendar): Boolean {
    if (cal1 == null) return false
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun getProjectsForDay(day: Calendar?, projects: List<CalendarProject>): List<CalendarProject> {
    if (day == null) return emptyList()
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayString = dateFormat.format(day.time)
    
    return projects.filter { project ->
        project.endDate == dayString
    }
}