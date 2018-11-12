package com.zoop.checkout.app;

import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.checkout.app.R;
import com.zoop.zoopandroidsdk.commons.APIParameters;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ViewPaymentOption extends LinearLayout {

	ImageView icon;
	TextView textViewCaption;
	boolean enabled = true;
	int paymentOption = -1;
	
    public ViewPaymentOption(Context context) {
        super(context);
        updateView();
    }

    public ViewPaymentOption(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateView();
    }

    @SuppressLint("NewApi")
	public ViewPaymentOption(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        updateView();
    }
    
/*    	
        View v = inflate(getContext(), R.layout.layout_payment_option, this);
        textViewCaption = (TextView)findViewById(R.id.textViewPaymentOptionCaption);
        icon = (ImageView)findViewById(R.id.imageViewPaymentOptionIcon);
        View viewDetail = v.findViewById(R.id.viewDetail);
        if (enabled) {
        	viewDetail.setBackgroundColor(getResources().getColor(R.color.zcolor_regular_button));
        }
        else {
        	viewDetail.setBackgroundColor(0xb1b1b1);
        }
*/        
    
    public void updateView() {    
    	
    	if (-1 != paymentOption) {    		
        	TextView textViewPaymentOptionCommentCaption;
        	textViewPaymentOptionCommentCaption = (TextView) findViewById(R.id.textViewPaymentOptionCommentCaption);
        	icon = (ImageView)findViewById(R.id.imageViewPaymentOptionIcon);
        	textViewCaption = (TextView)findViewById(R.id.textViewPaymentOptionCaption);
    		
	    	if (ZoopTerminalPayment.CHARGE_TYPE_CREDIT == paymentOption) {
	    		textViewCaption.setText(getResources().getString(R.string.label_credit));
	    		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_credito));
	    	}
	    	else if (ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS == paymentOption) {
	    		textViewCaption.setText(getResources().getString(R.string.label_credit_with_installments));
	    		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_credito_parcelado));
	    			  
	    		textViewPaymentOptionCommentCaption.setVisibility(View.VISIBLE);
	    		textViewPaymentOptionCommentCaption.setText(getResources().getString(R.string.label_credit_with_installments_comment));
	    	}
	    	else if (ZoopTerminalPayment.CHARGE_TYPE_DEBIT == paymentOption) {
	    		textViewCaption.setText(getResources().getString(R.string.label_debit));
	    		icon.setImageDrawable(getResources().getDrawable(R.drawable.icon_debito));
	    	}
	    	
	    	View viewDetail = findViewById(R.id.viewDetail);
            View viewBackground = findViewById(R.id.layoutPaymentOption);
	    	if (enabled) {
	    		viewBackground.setBackgroundColor(getResources().getColor(R.color.zcolor_button_font_color));

                viewDetail.setBackgroundColor(getResources().getColor(R.color.zcolor_view_deital_payment_option));
	        	textViewCaption.setTextColor(getResources().getColor(R.color.zcolor_regular_button));
	        	textViewPaymentOptionCommentCaption.setTextColor(getResources().getColor(R.color.zcolor_regular_button));
	        }
	        else {
	    		viewBackground.setBackgroundColor(Color.parseColor("#818181"));
	        	viewDetail.setBackgroundColor(Color.parseColor("#b1b1b1"));
	        	textViewCaption.setTextColor(Color.parseColor("#ffffff"));
	        	textViewPaymentOptionCommentCaption.setTextColor(Color.parseColor("#ffffff"));

	        }
	    }
    	else {
            View v = inflate(getContext(), R.layout.layout_payment_option, this);
            textViewCaption = (TextView)findViewById(R.id.textViewPaymentOptionCaption);
    	}
    	
    }
    
    @Override
    public void setEnabled(boolean pEnabled) {
    	enabled = pEnabled;
    	updateView();
    }
    
    @Override
    public void setSelected(boolean selected) {
    	if (enabled) {
	    	super.setSelected(selected);
			TextView textViewPaymentOptionCommentCaption = (TextView) findViewById(R.id.textViewPaymentOptionCommentCaption);

	    	if (selected) {
	            View v = findViewById(R.id.layoutPaymentOption);
				Button info = (Button) v.findViewById(R.id.bInfoPlan);
				if (ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS != paymentOption) {
					final Boolean bPlan = APIParameters.getInstance().getBooleanParameter("enablePlan", true);
					if(bPlan) {
						info.setVisibility(VISIBLE);
					}else {
						info.setVisibility(GONE);
					}
				}


				v.setBackgroundColor(getResources().getColor(R.color.zcolor_regular_button));
		    	
		    	((View) findViewById(R.id.viewDetail)).setBackgroundColor(getResources().getColor(R.color.zcolor_regular_button));
				textViewCaption.setTextColor(getResources().getColor(R.color.zcolor_button_font_color));
				
	    		textViewPaymentOptionCommentCaption.setTextColor(getResources().getColor(R.color.zcolor_button_font_color));

	            View viewDetail = v.findViewById(R.id.viewDetail);
            	viewDetail.setBackgroundColor(getResources().getColor(R.color.zcolor_button_font_color));

	    	}
	    	else {
	            View v = findViewById(R.id.layoutPaymentOption);
				v.setBackgroundColor(getResources().getColor(R.color.zcolor_button_font_color));
				Button info = (Button) v.findViewById(R.id.bInfoPlan);
				info.setVisibility(GONE);

	            
				textViewCaption.setTextColor(getResources().getColor(R.color.zcolor_regular_button));
	    		textViewPaymentOptionCommentCaption.setTextColor(getResources().getColor(R.color.zcolor_regular_button));

	    		View viewDetail = v.findViewById(R.id.viewDetail);
                viewDetail.setBackgroundColor(getResources().getColor(R.color.zcolor_view_deital_payment_option));

            }
    	}
    }
    
    public void setPaymentOption(int pPaymentOption) {
    	paymentOption = pPaymentOption;
    	updateView();
    }
    
    public void setComment(String sComment) {
		TextView textViewPaymentOptionCommentCaption = (TextView) findViewById(R.id.textViewPaymentOptionCommentCaption);
    	if (null == sComment) {
    		textViewPaymentOptionCommentCaption.setVisibility(View.GONE);
    	}
    	else {
			textViewPaymentOptionCommentCaption.setVisibility(View.VISIBLE);
			textViewPaymentOptionCommentCaption.setText(sComment);
    	}
    }


	public void setInfoPlan(String sComment) {
        TextView textViewPaymentOptionCommentCaption = (TextView) findViewById(R.id.textViewPaymentInfoPlan);
        final Boolean bPlan = APIParameters.getInstance().getBooleanParameter("enablePlan", true);
        if (bPlan) {
            if (null == sComment) {
                textViewPaymentOptionCommentCaption.setVisibility(View.GONE);
            } else {
                textViewPaymentOptionCommentCaption.setVisibility(View.VISIBLE);
                textViewPaymentOptionCommentCaption.setText(sComment);
                Button info = (Button) findViewById(R.id.bInfoPlan);

                info.setVisibility(VISIBLE);
            }
        }else {
            textViewPaymentOptionCommentCaption.setVisibility(View.GONE);
            Button info = (Button) findViewById(R.id.bInfoPlan);

            info.setVisibility(GONE);
        }
    }




	public void addTouchToHelpMessage(String sTouchToHelpText) {
    	
    }
	
/*	
	Activity currentActivity;
	View viewPaymentOption;
	
	public ViewPaymentOption(Activity pCurrentActivity, ViewGroup holderView) {
		super(pCurrentActivity.getApplicationContext());
		currentActivity = pCurrentActivity;
		viewPaymentOption = currentActivity.getLayoutInflater().inflate(R.layout.layout_payment_option, holderView, false);
	}
	
	public void makeSelected() {
		viewPaymentOption.setBackgroundColor(currentActivity.getResources().getColor(R.color.zcolor_regular_button));
		
		((TextView) viewPaymentOption.findViewWithTag("title")).setTextColor(currentActivity.getResources().getColor(R.color.zcolor_button_font_color));

	}
	
	public void makeUnselected() {
		
	}
*/
}
