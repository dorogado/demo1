package ui.tasksboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.models.Project
import data.models.ProjectBoard
import data.models.Task
import data.network.NetworkService
import data.network.PreferencesManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@Composable
fun CustomGrid(
    items: List<Task>,
    columns: Int = 3,
    modifier: Modifier = Modifier,
    onClick: (Task) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { task ->
            Card(task = task, onClick = onClick)
        }
    }
}

@Composable
fun Card(
    task: Task,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick(task) }
            .size(150.dp)
            .padding(4.dp)
            .background(Color.LightGray) // Фон по умолчанию
    ) {
        // Ваш код для отображения изображения (если нужно)
        Text(
            text = task.title,
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .padding(10.dp)
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.5f))
        )
    }
}


@Composable
fun TaskBoardScreen(
    project: Project,
    board: ProjectBoard,
    isLoading: Boolean,
    onBack: () -> Unit,
    onAddTask: (String) -> Unit,
    message: String?,
    isError: Boolean
) {

    var newTaskTitle by remember { mutableStateOf("") }
    var isAddingTask by remember { mutableStateOf(false) }

    val cardsList = remember { mutableStateListOf<Task>() }

    for (item in board.tasks) {
        cardsList.add(item)
    }

    val inProcessCardsList = remember { mutableStateListOf<Task>() }
    val completedCardsList = remember { mutableStateListOf<Task>() }

    var onDismissRequest by remember { mutableStateOf(false) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onDismissRequest = true }) {
                Icon(Icons.Default.Add, null)
            }
        },
        topBar = { Text(text = "Проекты", modifier = Modifier.clickable { onBack() }) }
    ) {

        if (onDismissRequest) {
            Dialog(onCloseRequest = {onDismissRequest = false}) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(value = newTaskTitle, onValueChange = { newTaskTitle = it })
                    Button(onClick = { onAddTask(newTaskTitle) }) {
                        Text("Добавить")
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
                .border(BorderStroke(15.dp, Color.Yellow))
        ) {
            Row() {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(10f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = "",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 30.sp
                    )

                    CustomGrid(completedCardsList) { task ->
                        completedCardsList.remove(task)
                    }

                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                        .height(500.dp)
                        .align(Alignment.CenterVertically)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(10f)
                ) {
                    Text(
                        text = "",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 30.sp
                    )
                    CustomGrid(inProcessCardsList) {

                    }

                    CustomGrid(inProcessCardsList) {task ->
                        completedCardsList.add(task)
                        inProcessCardsList.remove(task)
                    }


                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                        .height(500.dp)
                        .align(Alignment.CenterVertically)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(10f)
                ) {
                    LazyColumn {
                        items(cardsList) { task ->
                            Card(task = task, onClick = {
                            inProcessCardsList.add(task)
                            cardsList.remove(task)
                        })

                        }
                    }

                    Text(
                        text = "",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 30.sp
                    )
                }

            }
        }
    }

}
