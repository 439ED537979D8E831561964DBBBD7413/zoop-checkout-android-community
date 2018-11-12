package com.zoop.checkout.app;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.zoop.zoopandroidsdk.commons.ZLog;

public class DialogChangePlan extends DialogFragment {
	
	public static final int HEIGHT = 330;
	public static final int WIDTH = 310;
	
	View viewRelativeTo;
	String planInfo;




	public void setPositionBasedOnView(View relativeToThisView) {
		viewRelativeTo = relativeToThisView;		
	}

	public void setPlanInfo(String planInfo){

		this.planInfo=planInfo;


	}
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	// Inflate the layout to use as dialog or embedded fragment
    	View view = inflater.inflate(R.layout.popup_plan_change, container);
        // R.layout.dialog_color_picker is the custom layout of my dialog
        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

		//Rect r = new Rect();
        int[] screenPositions = new int[2];
        viewRelativeTo.getLocationOnScreen(screenPositions);
        DisplayMetrics dm = viewRelativeTo.getResources().getDisplayMetrics();
/*        if ((screenPositions[1]-40+HEIGHT) > dm.heightPixels) {
        	wmlp.y = dm.heightPixels - (HEIGHT+10);
        }
        else {
    		wmlp.y = screenPositions[1]-40;
        }
*/
		wmlp.y = screenPositions[1]-200;
		wmlp.x = screenPositions[0];
		ZLog.t("wmlp.x =" + wmlp.x + ", wmlp.y=" + wmlp.y);        
		//getDialog().getWindow().setLayout(40, 40);

//		wmlp.width = 300;
//		wmlp.height = 500;

		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().setAttributes(wmlp);
//       getDialog().getWindow().setLayout(200, 300);


		final JustifiedTextView tPlanInfo = (JustifiedTextView) view.findViewById(R.id.tInfoPlandetails);

		String questionPlan="\nAtualmente seu plano é o "+Extras.getInstance().getNamePlan()+".";

		tPlanInfo.setText("\n"+planInfo+questionPlan);


		Button changePlan=(Button)view.findViewById(R.id.changePlan);
		changePlan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				getDialog().dismiss();
				Intent intent = new Intent(getActivity(), PlansActivity.class);
				intent.putExtra("ChangePlanTransaction",true);
				getActivity().startActivityForResult(intent, 3);
			}
		});

//



        return view;
    }




    
	@Override
	public void onStart()
	{
	  super.onStart();
	  Resources r = getResources();
	  int width =0, height=0;
      if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
    	  width = (Double.valueOf(WIDTH * 0.8).intValue());
    	  height = (Double.valueOf(HEIGHT * 0.9).intValue());
      }
      else {
    	  width = WIDTH;
    	  height = HEIGHT;
      }
	  int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, r.getDisplayMetrics());
	  int py = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, r.getDisplayMetrics());
	  getDialog().getWindow().setLayout(px, py);
	  
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }


}
