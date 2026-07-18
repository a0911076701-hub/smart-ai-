package com.yourcompany.appname.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * لوحة الإدارة. تُفتح فقط بعد إدخال كلمة المرور الصحيحة.
 * في أول تشغيل (لا يوجد هاش محفوظ في Firestore) تعمل كشاشة "تعيين كلمة مرور".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(topBar = { TopAppBar(title = { Text("لوحة الإدارة") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            if (!uiState.isUnlocked) {
                AdminLoginSection(
                    isFirstTime = uiState.isFirstTimeSetup,
                    error = uiState.passwordError,
                    onSubmit = { viewModel.submitPassword(it) }
                )
            } else {
                AdminDashboard(viewModel, uiState)
            }
        }
    }
}

@Composable
private fun AdminLoginSection(isFirstTime: Boolean, error: String?, onSubmit: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    Column {
        Text(if (isFirstTime) "أول دخول: قم بتعيين كلمة مرور المدير" else "أدخل كلمة مرور المدير")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("كلمة المرور") },
            modifier = Modifier.fillMaxWidth()
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onSubmit(password) }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isFirstTime) "تعيين وتسجيل الدخول" else "دخول")
        }
    }
}

@Composable
private fun AdminDashboard(viewModel: AdminViewModel, uiState: AdminUiState) {
    var showAddModelDialog by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("الإحصائيات", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("المستخدمون", uiState.stats.totalUsers.toString())
                StatCard("المحادثات", uiState.stats.totalConversations.toString())
                StatCard("طلبات اليوم", uiState.stats.requestsToday.toString())
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("إدارة النماذج", style = MaterialTheme.typography.titleLarge)
                Button(onClick = { showAddModelDialog = true }) { Text("+ إضافة نموذج") }
            }
        }

        items(uiState.models) { model ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(model.name, style = MaterialTheme.typography.titleMedium)
                        Text(model.type, style = MaterialTheme.typography.bodySmall)
                    }
                    Row {
                        Switch(
                            checked = model.isActive,
                            onCheckedChange = { viewModel.toggleModelActive(model.id, it) }
                        )
                        IconButton(onClick = { viewModel.deleteModel(model.id) }) {
                            Text("🗑")
                        }
                    }
                }
            }
        }

        item {
            Text("المستخدمون المسجلون", style = MaterialTheme.typography.titleLarge)
        }
        items(uiState.users) { user ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(user.displayName.ifBlank { "بدون اسم" }, style = MaterialTheme.typography.titleMedium)
                    Text(user.email)
                    Text("الدور: ${user.role}")
                }
            }
        }

        item {
            Text("أدوات أخرى", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { viewModel.exportData() }) { Text("تصدير JSON") }
                var showWipeConfirm by remember { mutableStateOf(false) }
                OutlinedButton(onClick = { showWipeConfirm = true }) { Text("مسح كل البيانات") }
                if (showWipeConfirm) {
                    AlertDialog(
                        onDismissRequest = { showWipeConfirm = false },
                        title = { Text("تأكيد المسح") },
                        text = { Text("سيتم حذف جميع المحادثات نهائيًا. هل أنت متأكد؟") },
                        confirmButton = {
                            TextButton(onClick = { viewModel.wipeAllData(); showWipeConfirm = false }) { Text("نعم، امسح") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showWipeConfirm = false }) { Text("إلغاء") }
                        }
                    )
                }
            }
            uiState.exportedJson?.let {
                Spacer(Modifier.height(8.dp))
                Text("تم التصدير (${it.length} حرف) - انسخه من الـ Log أو أضف مشاركة ملف حسب الحاجة.")
            }
        }
    }

    if (showAddModelDialog) {
        AddModelDialog(
            onDismiss = { showAddModelDialog = false },
            onConfirm = { name, url, keyRef, type ->
                viewModel.addModel(name, url, keyRef, type)
                showAddModelDialog = false
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

@Composable
private fun AddModelDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, apiUrl: String, apiKeyRef: String, type: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var keyRef by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("text") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة نموذج جديد") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم النموذج") })
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("رابط API") })
                OutlinedTextField(
                    value = keyRef, onValueChange = { keyRef = it },
                    label = { Text("مرجع مفتاح API (وليس المفتاح نفسه)") }
                )
                Text("النوع: $type")
                Row {
                    listOf("text", "image", "audio").forEach { t ->
                        FilterChip(selected = type == t, onClick = { type = t }, label = { Text(t) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, url, keyRef, type) }) { Text("إضافة") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
