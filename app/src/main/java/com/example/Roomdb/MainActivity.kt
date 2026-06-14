 package com.example.Roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.Roomdb.ui.theme.TestsTheme
import com.example.Roomdb.ui.view.PostListScreen

 class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestsTheme {
                PostListScreen()
            }
        }
    }
}
