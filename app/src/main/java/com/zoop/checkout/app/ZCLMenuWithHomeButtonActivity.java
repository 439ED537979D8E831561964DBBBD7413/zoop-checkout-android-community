package com.zoop.checkout.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;

public class ZCLMenuWithHomeButtonActivity extends ZCLMenuActivity {

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//		   getActionBar().setDisplayHomeAsUpEnabled(true);
		//	getActionBar().setHomeAsUpIndicator(R.drawable.arrowhome);


		}

        ImageView ivConfigurations = (ImageView) findViewById(R.id.imageViewConfigurations);
        ivConfigurations.setVisibility(View.GONE);

    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    if (android.R.id.home == item.getItemId()) {
	    	//	NavUtils.navigateUpTo(this, new Intent(this, ChargeActivity.class));
			finish();
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
