package com.cu.rateitapp.Models

class User {
    private var uid:String = ""
    private var photoUrl:String = ""
    private var userName:String = ""
    private var displayName:String = ""
    constructor()
    constructor(uid:String, photoUrl:String, displayName: String?, userName:String ){
        this.uid = uid
        this.photoUrl = photoUrl
        this.displayName = displayName!!
        this.userName = userName
    }
    fun getUserName():String{
        return userName
    }
    fun getDisplayName():String{
        return displayName
    }
    fun getPhotoUrl():String{
        return photoUrl
    }
    fun getUid():String{
        return uid
    }
}