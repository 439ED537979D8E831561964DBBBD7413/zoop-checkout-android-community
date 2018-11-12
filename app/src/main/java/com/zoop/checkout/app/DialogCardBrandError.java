package com.zoop.checkout.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoop.zoopandroidsdk.commons.ZLog;

public class DialogCardBrandError extends DialogFragment {

    public static final int HEIGHT = LinearLayout.LayoutParams.WRAP_CONTENT;
    public static final int WIDTH = LinearLayout.LayoutParams.MATCH_PARENT;

    View viewRelativeTo;
    Activity activity;


    public void setPositionBasedOnView(View relativeToThisView) {
        viewRelativeTo = relativeToThisView;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.popup_brand_error, container);
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
        wmlp.y = screenPositions[1] - 200;
        wmlp.x = screenPositions[0];
        ZLog.t("wmlp.x =" + wmlp.x + ", wmlp.y=" + wmlp.y);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getDialog().getWindow().setAttributes(wmlp);
        View lBrandDetails = (View) view.findViewById(R.id.lbrand);
        TextView txtchipInfo = (TextView) lBrandDetails.findViewById(R.id.txtchipInfo);
        TextView txtchipTarja = (TextView) lBrandDetails.findViewById(R.id.txtTarjaInfo);
        txtchipInfo.setTextColor(Color.RED);
        txtchipTarja.setTextColor(Color.RED);
        Button newTransaction = (Button) view.findViewById(R.id.buttonNewTransaction2);
        newTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChargeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                getDialog().dismiss();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(WIDTH, HEIGHT);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
