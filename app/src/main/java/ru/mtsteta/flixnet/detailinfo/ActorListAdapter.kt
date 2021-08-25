package ru.mtsteta.flixnet.detailinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.network.MovieCrew

class ActorListAdapter :  ListAdapter<MovieCrew, ActorListAdapter.ActorViewHolder>(ActorDiffCallback()) {

    class ActorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val actorImage: ImageView =
            itemView.findViewById(R.id.imgActor)

        private val nameTextView: TextView = itemView.findViewById(R.id.tvActorName)

        fun bind(item: MovieCrew, position: Int) {
            nameTextView.text = item.name
            actorImage.load(BuildConfig.BASE_PROFILE_URL + item.imageUrl) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.broken_image)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActorViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.actor_rv_item, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ActorViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), position)
    }
}

private class ActorDiffCallback : DiffUtil.ItemCallback<MovieCrew>() {
    override fun areItemsTheSame(oldItem: MovieCrew, newItem: MovieCrew): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MovieCrew, newItem: MovieCrew): Boolean {
        return oldItem.name == newItem.name
    }
}