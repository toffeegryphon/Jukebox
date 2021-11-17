package edu.illinois.cs465.jukebox;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by anthonykiniyalocts on 12/8/16.
 *
 * Quick way to add padding to first and last item in recyclerview via decorators
 */

public class RecyclerViewCustomEdgeDecorator extends RecyclerView.ItemDecoration {

    private final int topEdgePadding;
    private final int bottomEdgePadding;
    private final boolean top;
    private final boolean bottom;

    public RecyclerViewCustomEdgeDecorator(int topEdgePadding, int bottomEdgePadding, boolean top, boolean bottom) {
        this.topEdgePadding = topEdgePadding;
        this.bottomEdgePadding = bottomEdgePadding;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int itemCount = state.getItemCount();

        final int itemPosition = parent.getChildAdapterPosition(view);

        int origPaddingLeft = view.getPaddingLeft();
        int origPaddingRight = view.getPaddingRight();
        int origPaddingTop = view.getPaddingTop();
        int origPaddingBottom = view.getPaddingBottom();
        int paddingTop = origPaddingTop;
        int paddingBottom = origPaddingBottom;

        if (top) {
            paddingTop = topEdgePadding;
        }
        if (bottom) {
            paddingBottom = bottomEdgePadding;
        }

        // no position, leave it alone
        if (itemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        // first item
        if (itemPosition == 0) {
            outRect.set(origPaddingLeft, paddingTop, origPaddingRight, origPaddingBottom);
        }
        // last item
        else if (itemCount > 0 && itemPosition == itemCount - 1) {
            outRect.set(origPaddingLeft, origPaddingTop, origPaddingRight, paddingBottom);
        }
        // every other item
        else {
            outRect.set(origPaddingLeft, origPaddingTop, origPaddingRight, origPaddingBottom);
        }
    }
}
