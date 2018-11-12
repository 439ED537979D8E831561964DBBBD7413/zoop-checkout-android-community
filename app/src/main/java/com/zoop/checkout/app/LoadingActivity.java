package com.zoop.checkout.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mainente on 24/02/16.
 */
public  abstract class LoadingActivity extends ZCLMenuWithHomeButtonActivity {

    private View mLoginStatusView;
    TextView mLoginStatusMessageView;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View viewForm, String msg) {


        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.

        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) mLoginStatusView.findViewById(R.id.login_status_message);

       mLoginStatusMessageView.setText(msg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE
                            : View.GONE);
                }
            });

            viewForm.setVisibility(View.VISIBLE);
            viewForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewForm.setVisibility(show ? View.GONE
                            : View.VISIBLE);
                }
            });
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            viewForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
