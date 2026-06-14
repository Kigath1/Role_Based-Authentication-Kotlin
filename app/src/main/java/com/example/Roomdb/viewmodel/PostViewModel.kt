package com.example.Roomdb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Roomdb.MainApplicationInstance
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    val postDao = MainApplicationInstance.postsDatabase.getPosts()

    val postsList : StateFlow<List<Post>> =  postDao.getAllPosts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        fetchingPosts()
    }

    private fun fetchingPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiResponse = RetrofitInstance.api.getPosts()
                postDao.insertPosts(apiResponse)
            } catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts", e)
            }
        }
    }

    private fun fetchingPost(){
        viewModelScope.launch {
            try {
                val apiResponse = RetrofitInstance.api.getPosts()
                postDao.insertPosts(apiResponse)
            }catch (e: Exception) {
                Log.e("PostViewModel", "Error fetching posts", e)
            }
        }
    }




//    private val _posts = mutableStateOf<List<Post>>(emptyList())
//    val posts : State<List<Post>> = _posts
//
//    init {
//        fetchPosts()
//    }
//    private fun fetchPosts() {
//        viewModelScope.launch {
//            Log.d("PostViewModel", "Starting fetchPosts()")
//            try {
//                val response = RetrofitInstance.api.getPosts()
//                _posts.value = response
//                Log.d("PostViewModel", "Fetched ${response.size} posts successfully")
//            } catch (e: Exception) {
//                Log.e("PostViewModel", "Error fetching posts", e)
//            }
//        }
//    }

}