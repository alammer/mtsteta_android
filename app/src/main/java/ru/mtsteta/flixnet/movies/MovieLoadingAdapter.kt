package ru.mtsteta.flixnet.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.mtsteta.flixnet.R

class MovieLoadingAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MovieLoadingAdapter.LoadingStateViewHolder>() {

    class LoadingStateViewHolder(itemView: View, retry: () -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val tvLoadError: TextView = itemView.findViewById(R.id.tvLoadError)
        private val loadBar: ProgressBar = itemView.findViewById(R.id.pbLoad)
        private val btnRetry: MaterialButton = itemView.findViewById(R.id.btnRetry)

        init {
            btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bindState(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                tvLoadError.text = loadState.error.localizedMessage
            }
            loadBar.isVisible = loadState is LoadState.Loading
            tvLoadError.isVisible = loadState !is LoadState.Loading
            btnRetry.isVisible = loadState !is LoadState.Loading
        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item_state, parent, false)
        return LoadingStateViewHolder(view, retry)
    }
}