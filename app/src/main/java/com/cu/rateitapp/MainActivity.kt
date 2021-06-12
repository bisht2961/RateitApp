package com.cu.rateitapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cu.rateitapp.Daos.PostDao
import com.cu.rateitapp.Models.Post
import com.cu.rateitapp.databinding.ActivityMainBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var adapter: PostAdapter
    private lateinit var postDao: PostDao
    private lateinit var auth :FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener{
            startActivity(Intent(this,AddPostActivity::class.java))
        }
        postDao = PostDao()
        auth = Firebase.auth
        setUpRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.rateit_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.log_out -> {
                FirebaseAuth.getInstance().signOut()
                val signInActivity = Intent(this, SignInActivity::class.java)
                startActivity(signInActivity)
                finish()
                return true
            }
            R.id.user_profile -> {
                val userProfile = Intent(this, UserProfile::class.java)
                startActivity(userProfile)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpRecyclerView() {
        val postsCollections = postDao.postCollection
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        adapter = PostAdapter(recyclerViewOptions,this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onUpvoteClicked(postId: String){
        postDao.updateUpVote(postId)
    }

    override fun onDownVoteClicked(postId: String) {
        postDao.updateDownVote(postId)
    }

    override fun showUserProfile(postId: String) {
        val intent = Intent(this,UserProfile::class.java)
        intent.putExtra("post_id",postId)
        startActivity(intent)
    }
}