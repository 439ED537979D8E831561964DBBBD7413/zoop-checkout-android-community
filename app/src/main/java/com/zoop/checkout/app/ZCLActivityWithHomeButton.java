/**
 * Created by rodrigo on 24/10/16.
 */
package com.zoop.checkout.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;

import com.zoop.zoopandroidsdk.commons.ZLog;

public class ZCLActivityWithHomeButton extends ZCLActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		getSupportActionBar().setTitle("ADS KDSAS");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setCustomView(R.layout.zce_action_bar_basic);
        getSupportActionBar().setIcon( new ColorDrawable(getResources().getColor(android.R.color.transparent)));


        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imageViewZoopLogo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZLog.t(300035);
            }
        });

/*        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        }

        ImageView ivConfigurations = (ImageView) findViewById(R.id.imageViewConfigurations);
        ivConfigurations.setVisibility(View.GONE);
*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            NavUtils.navigateUpTo(this, new Intent(this, ChargeActivity.class));
            return true;
            //Intent i = new Intent();
            //i.setClass(ZCLMenuWithHomeButtonActivity.this, ChargeActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(i);
            //return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
//	    return false;
    }
}

