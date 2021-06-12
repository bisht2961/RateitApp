package com.cu.rateitapp.Models


class Post {
    private var postId:String = ""
    private var postText:String = ""
    private var createdBy:User = User()
    private var createdAt:Long = 0L
    private var photoUrl:String = ""
    private val upVote: ArrayList<String> = ArrayList()
    private val downVote:ArrayList<String> = ArrayList()
    constructor()
    constructor(postId:String,postText:String,createdBy:User,createdAt:Long, photoUrl:String){
        this.postId = postId
        this.postText = postText
        this.createdBy = createdBy
        this.createdAt = createdAt
        this.photoUrl = photoUrl
    }
    fun getPostId():String{
        return postId
    }
    fun getPostText():String{
        return postText
    }
    fun getCreatedBy():User{
        return createdBy
    }
    fun getCreatedAt():Long{
        return createdAt
    }
    fun getPhotoUrl():String{
        return photoUrl
    }
    fun getUpVote():ArrayList<String>{
        return upVote
    }
    fun getDownVote():ArrayList<String>{
        return downVote
    }
}
