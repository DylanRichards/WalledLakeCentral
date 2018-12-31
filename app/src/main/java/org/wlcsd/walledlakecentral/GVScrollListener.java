package org.wlcsd.walledlakecentral;

import android.content.Context;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;

public class GVScrollListener implements AbsListView.OnScrollListener{

    private final Context context;

    public GVScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            Glide.with(context).resumeRequests();
        } else {
            Glide.with(context).pauseRequests();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Do nothing.
    }

}
