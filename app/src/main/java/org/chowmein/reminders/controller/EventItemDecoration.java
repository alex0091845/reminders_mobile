package org.chowmein.reminders.controller;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Used to style each RecyclerView item more properly.
 */
public class EventItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    /**
     * Constructor that sets a custom spacing between items
     * @param spacing spacing
     */
    public EventItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // only put a top spacing if the item is the first in the list
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = this.spacing;
        }

        // default, common spacing for all items
        outRect.left = this.spacing;
        outRect.right = this.spacing;
        outRect.bottom = this.spacing;
    }
}
