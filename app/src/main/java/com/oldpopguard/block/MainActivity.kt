package com.oldpopguard.block

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oldpopguard.block.data.RuleRepository
import com.oldpopguard.block.worker.RuleSyncWorker
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RuleSyncWorker.schedule(this)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
                    GuardHomePage()
                }
            }
        }
    }
}

@Composable
fun GuardHomePage() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var ruleCount by remember { mutableStateOf(0) }
    var isSyncing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ruleCount = RuleRepository.getInstance(context).getRuleCount()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text("老年弹窗防护", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✓ 防护已开启", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(12.dp))
                Text("正在自动为您关闭广告弹窗", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
                Text("当前已加载 $ruleCount 条拦截规则", fontSize = 16.sp, color = Color(0xFF666666))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().height(72.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("前往开启无障碍权限", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isSyncing = true
                scope.launch {
                    RuleRepository.getInstance(context).syncRules()
                    ruleCount = RuleRepository.getInstance(context).getRuleCount()
                    isSyncing = false
                }
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp),
            enabled = !isSyncing
        ) {
            Text("手动更新拦截规则", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("使用说明", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "• 自动识别并关闭广告弹窗\n• 银行、支付等重要应用不受影响\n• 所有处理均在手机本地完成",
                    fontSize = 16.sp, color = Color(0xFF1565C0), lineHeight = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
