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
import com.cu.rateitapp.databinding.ActivityAddPostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class AddPostActivity : AppCompatActivity() {

    private var storageReference: StorageReference? = null
    private lateinit var binding:ActivityAddPostBinding
    private var ImageUrl: String = ""
    private lateinit var filePath:Uri
    private val pickImages = registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
        uri?.let {
            filePath = it
            uploadImage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storageReference = FirebaseStorage.getInstance().reference
        binding.postPhoto.setOnClickListener {
            pickImages.launch("image/*")
        }
        binding.postButton.setOnClickListener { postUpload() }
    }
    private fun uploadImage() {
        binding.postProgressBar.visibility = View.VISIBLE
        val fileRef = storageReference!!.child("Posts_Image").child(System.currentTimeMillis().toString()+".jpg")
        val uploadTask: StorageTask<*>
        uploadTask  = fileRef.putFile(filePath)
        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
            if( !task.isSuccessful ){
                task.exception?.let { throw it }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener{ task ->
            if ( task.isSuccessful ){
                val downloadUrl = task.result
                ImageUrl = downloadUrl.toString()
                binding.postProgressBar.visibility = View.GONE
                binding.image.visibility = View.VISIBLE
                Glide.with(binding.image.context).load(downloadUrl).into(binding.image)
            }else{
                Toast.makeText(this@AddPostActivity,"Error Choosing Image",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun postUpload() {
        if( ImageUrl.isNotEmpty()){
            binding.postProgressBar.visibility = View.VISIBLE
            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
            val postId = ref.push().key.toString()
            val postDao = PostDao()
            val postText = binding.postInput.text.toString()
            postDao.addPost(postId,postText,ImageUrl)
            Toast.makeText(this,"Post Uploaded Successfully",Toast.LENGTH_SHORT).show()
            binding.postProgressBar.visibility = View.GONE
            sendToMain()
        }
    }
    private fun sendToMain() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}