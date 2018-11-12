package com.zoop.checkout.app;

import java.util.Stack;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.support.v4.app.Fragment;

public abstract class ZoopFlipperPane {
	
	Activity currentActivity;
	int layoutResourceId;
	
	private static Stack<Integer> history = null;
	private static ViewFlipper viewFlipper = null;
	
	public ZoopFlipperPane() {
		if (null == history) {
			history = new Stack<Integer>();
		}
	}
		
	public static void initializeHistory() {
        if (null != viewFlipper) {
            viewFlipper.removeAllViews();
        }
        viewFlipper = null;
        if (null != history) {
            history.clear();
        }
        //history = new Stack<Integer>();
	}
	
	public static void setBaseView(ViewFlipper v) {
		viewFlipper = v;
	//	return this;
	}
	
	public Activity getCurrentActivity() {
		return currentActivity;
	}
	
	public abstract int getLayoutResourceId();
	
	public void flipToPane(Activity pCurrentActivity) {
		
		currentActivity = pCurrentActivity;
		layoutResourceId = getLayoutResourceId();
		
//		ViewFlipper viewFlipper = (LinearLayout) currentActivity.findViewById(R.id.LinearLayoutPaymentRightPaneFlipper);
//		linearLayoutPaymentPane.removeAllViews();
	
//viewFlipper = (ViewFlipper) currentActivity.findViewById(R.id.LinearLayoutPaymentRightPaneFlipper);
//viewFlipper.removeAllViews();

		View child = currentActivity.getLayoutInflater().inflate(layoutResourceId, viewFlipper, false);
		viewFlipper.addView(child, history.size());

	
		if (history.size() == 0) {
			onFlip();
		}
		else {
			viewFlipper.setInAnimation(AnimationUtils.loadAnimation(currentActivity,  R.anim.push_left_in));
			viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(currentActivity, R.anim.push_left_out));
			onFlip();
			viewFlipper.showNext();		
		}
		
		history.add(layoutResourceId);
		
/*
		Button buttonCancel = (Button) child.findViewById(R.id.buttonPaymentCancel);
		buttonCancel.setOnClickListener( new OnClickListener() {			
			@Override
			public void onClick(View v) {
				datecs.cancel();
			}
		});
		 
  		((TextView) child.findViewById(R.id.textViewPaymentChipAndPINMessage)).setText("Initializing Card Terminal");
 
		
	    datecs = new DatecsBluepad50();
		try {
			
			datecs.charge(ZoopDB.getInstance().getCurrentOrderTotalPlusTaxes());
		}
		catch (Exception e) {
			ZLog.exception("DATECS error on Zoop Checkout", e);
		}
*/		
	}

	public abstract void onFlip();
	
	public void onDestroy() {
	}
	
	public static void showFirst(Activity currentActivity) {
		viewFlipper.setDisplayedChild(0);
		
/*		for (int i= history.size()-1; i>=0; i--) {
			viewFlipper.removeViewAt(i);
			history.remove(i);	
		}
		View child = currentActivity.getLayoutInflater().inflate(history.get(0), viewFlipper, false);
		viewFlipper.addView(child, 0);

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(currentActivity,  R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(currentActivity, R.anim.push_right_out));
		viewFlipper.showPrevious();
*/				
	}
	
	public void showPrevious() {
		
		onDestroy();
		history.pop();
//		viewFlipper.sho
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(currentActivity,  R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(currentActivity, R.anim.push_right_out));
		viewFlipper.showPrevious();
	}
}
