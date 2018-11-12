package com.zoop.checkout.app;

import android.content.Intent;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.uncopt.android.widget.text.justify.JustifiedTextView;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.GenderText;

public class FlipperWelcomeBuyTerminal extends ZoopFlipperPane {

	int paymentOption;
	
	public int getLayoutResourceId() {
		return R.layout.welcome_buy_terminal_pane;
	}
	

	@Override
	public void onFlip() {


		APIParameters ap = APIParameters.getInstance();

        boolean Tablet = Extras.isTablet(getCurrentActivity());
        // ToDo: @mainente use Extras.getDeviceType method
        String deviceType = Extras.getDeviceTypeString(getCurrentActivity());
        String myData2 = "Não tem uma maquininha?";
        String myData3 = null;
        if (ap.getBooleanParameter(APISettingsConstants.ZoopCheckout_PurchaseZoopTerminal_EnableButton, ApplicationConfiguration.ENABLE_PURCHASE_ZOOP_TERMINAL_BUTTON)) {
            myData3 = ap.getStringParameter(APISettingsConstants.ZoopCheckout_PurchaseZoopTerminal_ExplanationTextWhenEnabled);
        }
        else {
            myData3 = ap.getStringParameter(APISettingsConstants.ZoopCheckout_PurchaseZoopTerminal_ExplanationTextWhenDisabled) +
                    GenderText.getSingularPronom(ApplicationConfiguration.BRAND_GENDER) + " " + ApplicationConfiguration.APP_DESCRIPTOR;
        }
        JustifiedTextView dvText2 = (JustifiedTextView) getCurrentActivity().findViewById(R.id.dvText2);
        dvText2.setText(myData2);
        TextView txtlinkBuy = (TextView) getCurrentActivity().findViewById(R.id.textbuy);
        if (ap.getBooleanParameter(APISettingsConstants.ZoopCheckout_PurchaseZoopTerminal_EnableButton, ApplicationConfiguration.ENABLE_PURCHASE_ZOOP_TERMINAL_BUTTON)) {
            //https://portal.pagzoop.com/comprar-leitor-cartoes
            String sURLBuyZoopTerminal =ApplicationConfiguration.WEB_PORTAL_URL_WITH_SLUG+"#signin";
            txtlinkBuy.setText( Html.fromHtml("<a href=\""+sURLBuyZoopTerminal+"\">Comprar maquininha</a> "));
            txtlinkBuy.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else {
            txtlinkBuy.setVisibility(View.GONE);
        }



	}
}
