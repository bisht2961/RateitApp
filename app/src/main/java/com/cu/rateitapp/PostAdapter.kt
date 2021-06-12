package com.cu.rateitapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cu.rateitapp.Models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class PostAdapter(options: FirestoreRecyclerOptions<Post>, private val listener:IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>
(options) {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val username: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val voteCount : TextView = itemView.findViewById(R.id.VoteCount)
        val userImage : ImageView = itemView.findViewById(R.id.userImage)
        val upVote : ImageView = itemView.findViewById(R.id.upVote)
        val downVote: ImageView = itemView.findViewById(R.id.DownVote)
        val photo : ImageView = itemView.findViewById(R.id.photo)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
        viewHolder.upVote.setOnClickListener{
            listener.onUpvoteClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.downVote.setOnClickListener{
            listener.onDownVoteClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        viewHolder.userImage.setOnClickListener {
            listener.showUserProfile(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
       return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        holder.postTitle.text = model.getPostText()
        holder.username.text = model.getCreatedBy().getUserName()
        Glide.with(holder.userImage.context).load(model.getCreatedBy().getPhotoUrl()).circleCrop().into(holder.userImage)
        holder.createdAt.text = Utils.getTimeAgo(model.getCreatedAt())
        Glide.with(holder.photo.context).load(model.getPhotoUrl()).into(holder.photo)
        val auth = Firebase.auth
        val currentUser = auth.currentUser!!.uid
        val isUpVoted = model.getUpVote().contains(currentUser)
        if( isUpVoted ){
            holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.upVote.context,R.drawable.ic_upvote_fill))
        }else{
            holder.upVote.setImageDrawable(ContextCompat.getDrawable(holder.upVote.context,R.drawable.ic_upvote_empty))
        }
        val isDownVoted = model.getDownVote().contains(currentUser)
        if( isDownVoted ){
            holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.downVote.context,R.drawable.ic_dislike_fill))
        }else{
            holder.downVote.setImageDrawable(ContextCompat.getDrawable(holder.downVote.context,R.drawable.ic_dislike_empty))
        }
        val vote = model.getUpVote().size - model.getDownVote().size
        holder.voteCount.text = vote.toString()
    }

}
interface IPostAdapter{
    fun onDownVoteClicked(postId:String){
    }
    fun onUpvoteClicked(postId:String ){
    }
    fun showUserProfile(postId:String){
    }
}