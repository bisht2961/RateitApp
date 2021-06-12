package com.cu.rateitapp.Daos

import com.cu.rateitapp.Models.Post
import com.cu.rateitapp.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    val auth = Firebase.auth
    fun addPost(postId:String,postText:String, imageUrl:String) {
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = Post(postId,postText,user,currentTime,imageUrl)
            postCollection.document().set(post)
        }
    }
    fun getPostById(postId: String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }
    fun updateUpVote(postId:String){
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch (Dispatchers.IO){
            val post = getPostById(postId).await().toObject(Post::class.java)
            val voted = post?.getUpVote()?.contains(currentUser)
            val downVote = post?.getDownVote()?.contains(currentUser)
            if( downVote  == true ){
                post.getDownVote().remove(currentUser)
            }
            if( voted == true){
                post.getUpVote().remove(currentUser)
            }else{
                post?.getUpVote()?.add(currentUser)
            }
            postCollection.document(postId).set(post!!)
        }
    }
    fun updateDownVote(postId:String){
        val currentUser = auth.currentUser!!.uid
        GlobalScope.launch (Dispatchers.IO){
            val post = getPostById(postId).await().toObject(Post::class.java)
            val voted = post?.getDownVote()?.contains(currentUser)
            val upVoted = post?.getUpVote()?.contains(currentUser)
            if( upVoted == true ){
                post.getUpVote().remove(currentUser)
            }
            if( voted == true){
                post.getDownVote().remove(currentUser)
            }else{
                post?.getDownVote()?.add(currentUser)
            }
            postCollection.document(postId).set(post!!)
        }
    }
}