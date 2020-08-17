package com.iith.cabsharing.cabsharing;

import android.content.Context;
import android.widget.HorizontalScrollView;


public class HorizScrView extends HorizontalScrollView {
    private HorizontalScrollView controller;
    public HorizScrView(Context context, HorizontalScrollView hsv){
        super(context);
        controller = hsv;
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        controller.smoothScrollTo(l, t);
    }
}
