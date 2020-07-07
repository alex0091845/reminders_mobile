package org.chowmein.reminders;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class EventItemDecoration extends RecyclerView.ItemDecoration {
    int spacing;

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
        if(parent.getChildAdapterPosition(view) == 0) {
            outRect.top = this.spacing;
        }
        outRect.left = this.spacing;
        outRect.right = this.spacing;
        outRect.bottom = this.spacing;
    }
}
