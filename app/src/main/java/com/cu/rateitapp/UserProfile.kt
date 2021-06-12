package com.cu.rateitapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.cu.rateitapp.Daos.PostDao
import com.cu.rateitapp.Daos.UserDao
import com.cu.rateitapp.Models.Post
import com.cu.rateitapp.Models.User
import com.cu.rateitapp.databinding.ActivityUserProfileBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class UserProfile : AppCompatActivity() {

    private lateinit var filePath: Uri
    private var ImageUrl: String = ""
    private var sotrageRef: StorageReference? = null
    private lateinit var binding: ActivityUserProfileBinding
    private var enable:Boolean = true
    val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        uri?.let {
            Glide.with(binding.userProfilePhoto.context).load(it.toString()).circleCrop().into(binding.userProfilePhoto)
            filePath = it
            uploadImage()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sotrageRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        val postId = intent.getStringExtra("post_id")
        if(postId != null) {
            enable = false
            binding.userUpdateProfile.visibility = View.GONE
            binding.userShowDisplayName.visibility = View.VISIBLE
            binding.userShowUserName.visibility = View.VISIBLE
            binding.userSetDisplayName.visibility = View.GONE
            binding.userSetUserName.visibility = View.GONE
            getUserByPostId(postId)
        }
        if( enable) {
            getUser()
            binding.userProfilePhoto.setOnClickListener {
                pickImages.launch("image/*")
            }
            binding.userUpdateProfile.setOnClickListener {
                updateUserProfile()
            }
        }
    }

    private fun getUserByPostId(postId: String) {
        val postDao = PostDao()
        GlobalScope.launch (Dispatchers.IO){
            val post = postDao.getPostById(postId).await().toObject(Post::class.java)
            val createdBy = post?.getCreatedBy()
            withContext(Dispatchers.Main){
                setUpNonUserProfile(createdBy!!)
            }
        }
    }

    private fun getUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        GlobalScope.launch (Dispatchers.IO){
            val userDao = UserDao()
            val user = userDao.getUserByID(currentUser).await().toObject(User::class.java)
            ImageUrl = user?.getPhotoUrl().toString()
            withContext(Dispatchers.Main){
                setUpProfile(user!!)
            }
        }
    }

    private fun setUpProfile(user:User) {
        binding.userSetDisplayName.setText(user.getDisplayName())
        binding.userSetUserName.setText(user.getUserName())
        Glide.with(binding.userProfilePhoto.context).load(user.getPhotoUrl()).circleCrop().into(binding.userProfilePhoto)
    }
    private fun setUpNonUserProfile(user: User){
        binding.userShowDisplayName.text = user.getDisplayName()
        binding.userShowUserName.text = user.getUserName()
        Glide.with(binding.userProfilePhoto.context).load(user.getPhotoUrl()).circleCrop().into(binding.userProfilePhoto)
    }

    private fun uploadImage() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val fileRef = sotrageRef!!.child("$currentUser.jpg")
        val uploadTask:StorageTask<*>
        uploadTask  = fileRef.putFile(filePath)
        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{ task ->
            if( !task.isSuccessful ){
                task.exception?.let { throw it }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result
                ImageUrl = downloadUrl.toString()
                Toast.makeText(this,"$ImageUrl",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun updateUserProfile() {
        when{
            binding.userSetDisplayName.text.isEmpty() -> Toast.makeText(this,"Display Name field is empty",Toast.LENGTH_SHORT).show()
            binding.userSetUserName.text.isEmpty() -> Toast.makeText(this,"User Name field is empty",Toast.LENGTH_SHORT).show()
            else -> {
                val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
                val userName = binding.userSetUserName.text.toString()
                val displayName = binding.userSetDisplayName.text.toString()
                val user = User(firebaseUserId, ImageUrl, displayName, userName)
                val usersDao = UserDao()
                usersDao.updateUser(user)
                sendToMain()
            }
        }
    }
    private fun sendToMain() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    companion object{
        const val TAG:String  = "User Profile Activity"
    }
}


