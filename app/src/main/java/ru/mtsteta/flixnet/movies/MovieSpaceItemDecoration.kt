package ru.mtsteta.flixnet.movies

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MovieSpaceItemDecoration(private val spacingTop: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            val itemPos = parent.getChildAdapterPosition(view)
            rect.top = when (itemPos) {
                RecyclerView.NO_POSITION,
                0,
                1 -> 0
                else -> spacingTop
            }
            if (itemPos % 2 == 0) {
                rect.left = 0
                rect.right = 25
            } else
            {
                rect.right = 0
                rect.left = 25
            }

        }
    }

}