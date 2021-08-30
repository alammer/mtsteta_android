package ru.mtsteta.flixnet.movies

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MovieSpaceItemDecoration(private val spacingTop: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        parent.adapter?.let { _ ->
            val itemPos = parent.getChildAdapterPosition(view)

            when (parent.layoutManager) {

                is GridLayoutManager -> {
                    rect.top = when (itemPos) {
                        RecyclerView.NO_POSITION,
                        0,
                        1 -> 0
                        else -> spacingTop
                    }
                    if (itemPos % 2 == 0) {
                        rect.left = 0
                        rect.right = 25
                    } else {
                        rect.right = 0
                        rect.left = 25
                    }
                }
                else -> {
                    rect.set(25, 25, 25, 25)
                }

            }
        }
    }

}