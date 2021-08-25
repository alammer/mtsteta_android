package ru.mtsteta.flixnet.detailinfo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActorSpaceItemDecoration(private val horizontalspacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        parent.adapter?.let { _ ->
            val itemPos = parent.getChildAdapterPosition(view)

            rect.right = horizontalspacing

            if (itemPos > 0) rect.left = horizontalspacing
        }
    }

}