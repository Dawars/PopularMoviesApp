package me.dawars.popularmoviesapp.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import me.dawars.popularmoviesapp.R;

public class ProportionalImageView extends ImageView {

    private static float ASPECT_RATIO;

    public ProportionalImageView(Context context) {
        super(context);
        ASPECT_RATIO = 1.5f;
    }

    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ProportionalImageView,
                0, 0);

        try {
            ASPECT_RATIO = a.getFloat(R.styleable.ProportionalImageView_ratio, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width * ASPECT_RATIO);
        setMeasuredDimension(width, height);
    }
}