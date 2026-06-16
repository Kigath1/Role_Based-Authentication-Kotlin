 package com.example.Roomdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.Roomdb.ui.theme.TestsTheme
import com.example.Roomdb.ui.view.PostListScreen
import com.example.Roomdb.viewmodel.PostViewModel
import com.example.Roomdb.viewmodel.PostViewModelFactory

 class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestsTheme {
                // Create ViewModel with factory
                val factory = PostViewModelFactory(MainApplicationInstance.postRepository)
                val viewModel: PostViewModel = viewModel(factory = factory)

                PostListScreen(viewModel = viewModel)
            }
        }
    }
}
