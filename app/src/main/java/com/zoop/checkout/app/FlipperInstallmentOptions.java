package com.zoop.checkout.app;

import java.awt.font.NumericShaper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.Fragment;

import org.json.JSONException;

public class FlipperInstallmentOptions extends ZoopFlipperPane {

    //String sTotalToCharge;
    BigDecimal totalToCharge;
    int numberOfInstallments = 2;
    BigDecimal minimumInstallmentAmount = null;
    boolean bImageViewIncrementInstallmentsIsEnabled = false;
    boolean bImageViewDecreaseInstallmentsIsEnabled = false;

    public void setTotalValue(BigDecimal pTotalToCharge) {
        totalToCharge = pTotalToCharge;
    }

    public int getLayoutResourceId() {
        return R.layout.flipper_installment_options;
    }

    @Override
    public void onFlip() {
        minimumInstallmentAmount = APIParameters.getInstance().getBigDecimalParameter(APISettingsConstants.Payment_MinimumInstallmentAmount);

//		ViewPaymentOption viewPaymentOption = new ViewPaymentOption(getCurrentActivity(), (ViewGroup) getCurrentActivity().findViewById(R.id.linearLayoutInstallmentOptions));
//		((LinearLayout) getCurrentActivity().findViewById(R.id.linearLayoutInstallmentOptions)).addView(viewPaymentOption, 0);

        final ViewPaymentOption viewPaymentOption = (ViewPaymentOption) getCurrentActivity().findViewById(R.id.viewPaymentCreditWithInstallments);
        viewPaymentOption.setPaymentOption(ZoopTerminalPayment.CHARGE_TYPE_CREDIT_WITH_INSTALLMENTS);
        viewPaymentOption.setSelected(true);


        final TextView textViewNumberOfInstallments = (TextView) getCurrentActivity().findViewById(R.id.textViewNumberOfInstallments);
        textViewNumberOfInstallments.setText(Integer.toString(numberOfInstallments));
        ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentSummary)).setText(Extras.getInstance().buildInstallmentSummaryString(totalToCharge, numberOfInstallments));

        try {
            ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentPlanInfo)).setText("(" + Extras.getInstance().getPlanInfo("credit", totalToCharge, numberOfInstallments) + ")");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button info=(Button)viewPaymentOption.findViewById(R.id.bInfoPlan);

        final Boolean bPlan=APIParameters.getInstance().getBooleanParameter("enablePlan",true);

        if(bPlan) {
            info.setVisibility(View.VISIBLE);

        }else {
            info.setVisibility(View.GONE);

        }

        ImageView imageViewIncrementInstallments = (ImageView) getCurrentActivity().findViewById(R.id.imageViewIncrementInstallments);
        setIncrementNumberOfInstallmentsButtonStatus(numberOfInstallments+1);
        setDecreaseNumberOfInstallmentsButtonStatus(numberOfInstallments - 1);




        Button infoDebit=(Button)viewPaymentOption.findViewById(R.id.bInfoPlan);

        infoDebit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutApplication.getFirebaseAnalytics().logEvent("payment_method_info", null);

                String infoPlan= null;
                try {
                    infoPlan = Extras.getInstance().getPlanInfo("credit", totalToCharge
                            ,numberOfInstallments );
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getCurrentActivity().getFragmentManager();

                DialogChangePlan dialog = new DialogChangePlan();
                dialog.setPositionBasedOnView(viewPaymentOption);
                dialog.setPlanInfo(infoPlan);


                dialog.show(fragmentManager, "dialog");




            }
        });

        imageViewIncrementInstallments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutApplication.getFirebaseAnalytics().logEvent("new_sale_installment_increment", null);

                if (bImageViewIncrementInstallmentsIsEnabled) {
                    if (numberOfInstallments < 12) {
                        numberOfInstallments++;
                    }
                    textViewNumberOfInstallments.setText(Integer.toString(numberOfInstallments));
                    String installmentSummary = Extras.getInstance().buildInstallmentSummaryString(totalToCharge, numberOfInstallments);
                    ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentSummary)).setText(installmentSummary);
                    try {
                        ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentPlanInfo)).setText("("+ Extras.getInstance().getPlanInfo("credit",totalToCharge, numberOfInstallments)+")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((ChargeActivity) getCurrentActivity()).setNumberOfInstallments(numberOfInstallments);

                    setIncrementNumberOfInstallmentsButtonStatus(numberOfInstallments+1);
                    setDecreaseNumberOfInstallmentsButtonStatus(numberOfInstallments-1);
                }
                else {
                    String minimumPerInstallmentMessage = getCurrentActivity().getResources().getString(R.string.credit_with_installments_less_than_minimum);
                    BigDecimal minimumPerInstallment = APIParameters.getInstance().getBigDecimalParameter(APISettingsConstants.Payment_MinimumInstallmentAmount);
                    minimumPerInstallmentMessage = minimumPerInstallmentMessage.replace("[minimum_installment_amount]", com.zoop.zoopandroidsdk.commons.Extras.formatBigDecimalAsMoneyString(minimumPerInstallment));
                    Toast.makeText(getCurrentActivity(), minimumPerInstallmentMessage , Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imageViewDecreaseInstallments = (ImageView) getCurrentActivity().findViewById(R.id.imageViewDecreaseInstallments);
        imageViewDecreaseInstallments.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutApplication.getFirebaseAnalytics().logEvent("new_sale_installment_decrement", null);
                if (bImageViewDecreaseInstallmentsIsEnabled) {
                    if (numberOfInstallments >2) {
                        numberOfInstallments--;
                    }
                    textViewNumberOfInstallments.setText(Integer.toString(numberOfInstallments));
                    String installmentSummary = Extras.getInstance().buildInstallmentSummaryString(totalToCharge, numberOfInstallments);
                    ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentSummary)).setText(installmentSummary);
                    try {
                        ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentPlanInfo)).setText("("+ Extras.getInstance().getPlanInfo("credit",totalToCharge, numberOfInstallments)+")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ((ChargeActivity) getCurrentActivity()).setNumberOfInstallments(numberOfInstallments);

                    setIncrementNumberOfInstallmentsButtonStatus(numberOfInstallments+1);
                    setDecreaseNumberOfInstallmentsButtonStatus(numberOfInstallments-1);
                }
                else {
                    String messageMinimumNumberOfInstallments = getCurrentActivity().getResources().getString(R.string.credit_with_installments_must_have_at_least_2_installments);
                    Toast.makeText(getCurrentActivity(), messageMinimumNumberOfInstallments, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void setIncrementNumberOfInstallmentsButtonStatus(int numberOfInstallments) {
        ImageView imageViewIncrementInstallments = (ImageView) getCurrentActivity().findViewById(R.id.imageViewIncrementInstallments);
        if (totalToCharge.compareTo( FlipperInstallmentOptions.this.minimumInstallmentAmount.multiply(new BigDecimal(numberOfInstallments))  ) < 0) {
            //imageViewIncrementInstallments.setBackgroundResource(0);
            imageViewIncrementInstallments.setBackgroundColor(Color.parseColor("#b1b1b1"));
            bImageViewIncrementInstallmentsIsEnabled = false;
        }
        else {
            imageViewIncrementInstallments.setBackgroundColor(currentActivity.getResources().getColor(R.color.zcolor_regular_button));
            //imageViewIncrementInstallments.setEnabled(true);
            bImageViewIncrementInstallmentsIsEnabled = true;
        }
    }

    public void setDecreaseNumberOfInstallmentsButtonStatus(int numberOfInstallments) {
        ImageView imageViewDecreaseInstallments = (ImageView) getCurrentActivity().findViewById(R.id.imageViewDecreaseInstallments);
        if ((numberOfInstallments) < 2) {
            imageViewDecreaseInstallments.setBackgroundColor(Color.parseColor("#b1b1b1"));
            //imageViewDecreaseInstallments.setEnabled(false);
            bImageViewDecreaseInstallmentsIsEnabled = false;
        }
        else {
            imageViewDecreaseInstallments.setBackgroundColor(currentActivity.getResources().getColor(R.color.zcolor_regular_button));
            //imageViewDecreaseInstallments.setEnabled(true);
            bImageViewDecreaseInstallmentsIsEnabled = true;
        }
    }
    public void setInfoPlan() {

        try {
            final Boolean bPlan=APIParameters.getInstance().getBooleanParameter("enablePlan",true);
            if(bPlan) {
                ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentPlanInfo)).setText("(" + Extras.getInstance().getPlanInfo("credit", totalToCharge, numberOfInstallments) + ")");
            }else {
                ((TextView) getCurrentActivity().findViewById(R.id.textViewInstallmentPlanInfo)).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
