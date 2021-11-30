package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Keep;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.snackbar.Snackbar;

@Keep
public class MoveUpwardBehavior extends CoordinatorLayout.Behavior<View> {

    public MoveUpwardBehavior() {
        super();
    }

    public MoveUpwardBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.min(0, ViewCompat.getTranslationY(dependency) - dependency.getHeight());

        //Dismiss last SnackBar immediately to prevent from conflict when showing SnackBars immediately after eachother
        ViewCompat.animate(child).cancel();

        //Move entire child layout up that causes objects on top disappear
        child.setTranslationY(translationY);

        //Set top padding to child layout to reappear missing objects
        //If you had set padding to child in xml, then you have to set them here by <child.getPaddingLeft(), ...>
        child.setPadding(child.getPaddingLeft(), -Math.round(translationY), child.getPaddingRight(), child.getPaddingBottom());

        return true;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        //Reset paddings and translationY to its default
        child.setPadding(child.getPaddingLeft(), child.getPaddingTop(), child.getPaddingRight(), child.getPaddingBottom());
        ViewCompat.animate(child).translationY(0).start();
    }
}
