package com.yourcompany.appname.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdminScreen(viewModel: AdminViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var passwordInput by remember { mutableStateOf("") }

    if (!state.isUnlocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("لوحة الإدارة - مطلوب كلمة المرور", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = passwordInput,
                onValueChange = { passwordInput = it },
                label = { Text("كلمة المرور") },
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { viewModel.tryUnlock(passwordInput) }, modifier = Modifier.fillMaxWidth()) {
                Text("دخول")
            }
            state.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
        return
    }

    var showAddModel by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newUrl by remember { mutableStateOf("") }
    var newKeyRef by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("text") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("لوحة الإدارة", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard("المستخدمون", state.userCount.toString())
            StatCard("المحادثات", state.conversationCount.toString())
            StatCard("طلبات اليوم", state.dailyRequestCount.toString())
        }

        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("إدارة النماذج", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { showAddModel = true }) { Text("+ إضافة نموذج") }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.models) { model ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(model.name, style = MaterialTheme.typography.titleSmall)
                            Text(model.type, style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            Switch(
                                checked = model.isActive,
                                onCheckedChange = { viewModel.toggleModel(model.id, it) }
                            )
                            TextButton(onClick = { viewModel.setDefaultModel(model.id) }) {
                                Text(if (model.isDefault) "افتراضي ✓" else "تعيين كافتراضي")
                            }
                            TextButton(onClick = { viewModel.deleteModel(model.id) }) {
                                Text("حذف", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { /* TODO: تصدير JSON عبر FileWriter/Storage */ }) {
                Text("تصدير البيانات")
            }
            OutlinedButton(onClick = { viewModel.clearAllData() }) {
                Text("مسح جميع البيانات", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showAddModel) {
        AlertDialog(
            onDismissRequest = { showAddModel = false },
            title = { Text("إضافة نموذج جديد") },
            text = {
                Column {
                    OutlinedTextField(newName, { newName = it }, label = { Text("اسم النموذج") })
                    OutlinedTextField(newUrl, { newUrl = it }, label = { Text("رابط API") })
                    OutlinedTextField(
                        newKeyRef, { newKeyRef = it },
                        label = { Text("مرجع مفتاح API (وليس المفتاح نفسه)") }
                    )
                    OutlinedTextField(newType, { newType = it }, label = { Text("النوع: text / image / audio") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addModel(newName, newUrl, newKeyRef, newType)
                    showAddModel = false
                    newName = ""; newUrl = ""; newKeyRef = ""; newType = "text"
                }) { Text("إضافة") }
            },
            dismissButton = {
                TextButton(onClick = { showAddModel = false }) { Text("إلغاء") }
            }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.width(110.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
