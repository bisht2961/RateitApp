package com.cu.rateitapp.Daos

import com.cu.rateitapp.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class UserDao {
    
    var database =FirebaseFirestore.getInstance()
    val usersRef = database.collection("Users")
    fun addUser(user: User?){
        user?.let {
            GlobalScope.launch (Dispatchers.IO){
                usersRef.document(user.getUid()).set(it)
            }
        }
    }
    fun updateUser(user:User){
        user.let {

            GlobalScope.launch (Dispatchers.IO){
                usersRef.document(user.getUid()).set(it)
            }
        }
    }
    fun getUserByID(uid: String): Task<DocumentSnapshot> {
        return usersRef.document(uid).get()
    }

}