package com.techsavanna.shiloahmsk.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.techsavanna.shiloahmsk.R;

public class NavigationIconClickListener implements View.OnClickListener {
    private final AnimatorSet animatorSet = new AnimatorSet();
    private boolean backdropShown = false;
    private Drawable closeIcon;
    private Context context;
    private int height;
    private Interpolator interpolator;
    private Drawable openIcon;
    private View sheet;

    NavigationIconClickListener(Context context2, View view, Interpolator interpolator2, Drawable drawable, Drawable drawable2) {
        this.context = context2;
        this.sheet = view;
        this.interpolator = interpolator2;
        this.openIcon = drawable;
        this.closeIcon = drawable2;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context2).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.height = displayMetrics.heightPixels;
    }

    public void onClick(View view) {
        this.backdropShown = !this.backdropShown;
        this.animatorSet.removeAllListeners();
        this.animatorSet.end();
        this.animatorSet.cancel();
        updateIcon(view);
        int dimensionPixelSize = this.height - this.context.getResources().getDimensionPixelSize(R.dimen.product_grid_reveal_height);
        View view2 = this.sheet;
        float[] fArr = new float[1];
        fArr[0] = this.backdropShown ? (float) dimensionPixelSize : 0.0f;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view2, "translationY", fArr);
        ofFloat.setDuration(500);
        Interpolator interpolator2 = this.interpolator;
        if (interpolator2 != null) {
            ofFloat.setInterpolator(interpolator2);
        }
        this.animatorSet.play(ofFloat);
        ofFloat.start();
    }

    private void updateIcon(View view) {
        Drawable drawable;
        Drawable drawable2 = this.openIcon;
        if (drawable2 != null && (drawable = this.closeIcon) != null) {
            if (!(view instanceof ImageView)) {
                throw new IllegalArgumentException("updateIcon() must be called on an ImageView");
            } else if (this.backdropShown) {
                ((ImageView) view).setImageDrawable(drawable);
            } else {
                ((ImageView) view).setImageDrawable(drawable2);
            }
        }
    }
}
