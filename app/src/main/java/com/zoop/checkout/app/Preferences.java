package com.zoop.checkout.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.util.Log;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.UFUC;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preferences {
	

	//public static final String[] marketplaceIds = {"ada14d58c7d641f58f43712bc227c991", "0162893710a6495e86542eeff192baa1", "3249465a7753536b62545a6a684b0000", "3d4c70933f134d3ba6c4f72d463ff9ac", "81c03b0856a54150b6c8900914904408", "81c03b0856a54150b6c8900914904408"};
	//public static final String[] publishableKeys = {"zpk_test_fbznH0GjanSO4rOENLzMBXiD", "zpk_prod_O6Wo7kRzYX11U0KQCy5Yl9hx", "zpk_test_EzCkzFFKibGQU6HFq7EYVuxI", "zpk_prod_eNjFbSk393HyzcumKUwkAHtF", "zpk_test_8UYXRSaZzCi6XVstTemLNiZS", "zpk_test_8UYXRSaZzCi6XVstTemLNiZS"};

	//private static final int LOGIN_TYPE_TEST = 0;
	//private static final int LOGIN_TYPE_STAGING = 1;
	//private static final int LOGIN_TYPE_PRODUCTION = 2;
	
	private static Preferences instance = null;
	private static Application _application = null;
	
	private Preferences() {
		super();
	}

	/*
	public static void initialize(Application pApplication) {
		_application = pApplication;
	}
	*/
	public static Preferences getInstance() {
		if (null == instance) {
			instance = new Preferences();
		}
/*		if (null == _application) {
			Log.e("Zoop App Core", "ERROR: Preferences.initialize(context) must be called before usage");
			return null;
		}
*/
		return instance;
	}
/*
	public void setKeyValuePref(String key, String value) {
		SharedPreferences settings = _context.getSharedPreferences(ApplicationConfiguration.PREFS_ZOOP_API_DEMO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();		
	}
*/
	//public String getValueForKey(String key) {
	//	return getValueForKey(key, null);
	//}

	//public String getValueForKey(String key, String defaultValue) {
	//	SharedPreferences settings = _context.getSharedPreferences(PREFS_ZOOP_API_DEMO, Context.MODE_PRIVATE);
	//	return settings.getString(key, defaultValue);
		
	//}
	
	public void clearPreferences()  {
		APIParameters.getInstance().deleteGlobalParameter("loginType");
		APIParameters.getInstance().deleteGlobalParameter("currentLoggedinUsername");
		APIParameters.getInstance().deleteGlobalParameter("currentLoggedinSecurityToken");
		APIParameters.getInstance().deleteGlobalParameter("marketplaceId");
		APIParameters.getInstance().deleteGlobalParameter("sellerId");
		APIParameters.getInstance().deleteGlobalParameter("publishableKey");
		APIParameters.getInstance().deleteGlobalParameter("cookie");
		APIParameters.getInstance().processAPIParametersInitialization(null);



	}
	
	public void logout(Activity currentActivity) {
		try {
			Preferences.getInstance().clearPreferences();
			Intent loginIntent = new Intent(currentActivity, LoginActivity.class);
			loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			currentActivity.startActivity(loginIntent);
			//currentActivity.startActivity(logginIntent);
			//ZoopAPI.getInstance().detachMarketplaceAndSeller();
		}
		catch (Exception e) {
			System.out.println("Exception "+e.getMessage());
		}		
	}

	public String getRegisteredSellerId() {
		return APIParameters.getInstance().getStringParameter("sellerId");
	}

	public void setApplicationSellerAttributes(JSONObject joSeller) {
		try {
            APIParameters.getInstance().putStringParameter("sellerName", getSellerNameAsString(joSeller));
            APIParameters.getInstance().putStringParameter("sellerId", joSeller.getString("id"));
			if (joSeller.has("ein")) {
                APIParameters.getInstance().putStringParameter("sellerTaxIdType", "CNPJ");
                APIParameters.getInstance().putStringParameter("sellerTaxId", joSeller.getString("ein"));
			}
			else if (joSeller.has("taxpayer_id")) {
                APIParameters.getInstance().putStringParameter("sellerTaxIdType", "CPF");
                APIParameters.getInstance().putStringParameter("sellerTaxId", joSeller.getString("taxpayer_id"));
			}
			
		}
		catch (Exception e) {
			System.out.println("Exception getting seller information, "+e.toString());
		}
	}
	
	
	public String getSellerNameAsString(JSONObject joSeller) {
		try {
			if (true != joSeller.isNull("business_name")) {
				return joSeller.getString("business_name");
			}
			else  {
				String sSellerName = null;
				if (!joSeller.isNull("first_name")) {
					sSellerName = joSeller.getString("first_name");
				}
				if (!joSeller.isNull("last_name")) {
					if (null == sSellerName) { // No first name...
						sSellerName = joSeller.getString("last_name");
					}
					else {
						sSellerName += " "+joSeller.getString("last_name");
					}
				}
				if (null == sSellerName) {// No seller name at all....
					sSellerName = joSeller.getString("id");
				}
				return sSellerName;
			}
		}
		catch (Exception e) {
			System.out.println("Exception getting seller information, "+e.toString());
		}
		return null;
	}

	
	public String loadUFUAndGetCheckoutPublicKey(String sUsername, String sAdditionalLoginString) {
        APIParameters ap = APIParameters.getInstance();
		String sCheckoutPublicKey = null;
		String regexTestUsersMagic = ap.getStringParameter(APISettingsConstants.ZoopCheckout_TestUsernamesPublicKeyMagic);
		String regexStagingUsersMagic = ap.getStringParameter(APISettingsConstants.ZoopCheckout_StagingUsernamesPublicKeyMagic);

		String sUFUReplacingURLForZoopPayments = null;

		if (sUsername.matches(regexTestUsersMagic)) {
            sCheckoutPublicKey = ApplicationConfiguration.CHECKOUT_API_TEST_PUBLIC_KEY;
           // ZoopAPI.setUFU(null);
            //Extras.getInstance().sReplacingURL = null;
        }
        else if (sUsername.matches(regexStagingUsersMagic)) {
            sCheckoutPublicKey = ApplicationConfiguration.CHECKOUT_API_PRODUCTION_PUBLIC_KEY;
            //ZoopAPI.setUFU("https://staging.");
            //Extras.getInstance().sReplacingURL = "https://staging.dashboard.pagzoop.com/ckt/v1/";
            sAdditionalLoginString = "staging";
			sUFUReplacingURLForZoopPayments = "api.staging.pagzoop.com";
        }
        else  {
            sCheckoutPublicKey = ApplicationConfiguration.CHECKOUT_API_PRODUCTION_PUBLIC_KEY;
        }

        String sZoopCheckoutWebservicesBaseURL = ap.getStringParameter("ZCWBU_");
        if (null != sAdditionalLoginString) {
			// OK, a alternate server has been chosen. Now we lookup if the string is the literal server prefix chosen of if we
			// should fetch a alternate URLs/ servers from APISettings.
			// This was created to give lots of flexibility in runtime changes of environment without the need to redistribute new APK
			// if, for some reason, a new alternate server is needed.
            String sTEMPZoopCheckoutWebservicesBaseURL = ap.getStringParameter("ZCWBU_"+sAdditionalLoginString);
            if (null != sTEMPZoopCheckoutWebservicesBaseURL) {
                sZoopCheckoutWebservicesBaseURL = sTEMPZoopCheckoutWebservicesBaseURL;
            }
            // If there is no 	replacement for the base URL, then load the base one
		}

        if (null != sAdditionalLoginString) {
            UFUC.setZoopPaymentsReplacementURL("https://" + sUFUReplacingURLForZoopPayments );
        }

        Extras.getInstance().setCheckoutWebservicesBaseURL(sZoopCheckoutWebservicesBaseURL);
        //Extras.getInstance().sReplacingURL = null;
        return sCheckoutPublicKey;
    }
	
/*	
	public void setValidUsernameAndPassword(String username, String password, String marketplaceId, String publishableKey) {
		SharedPreferences settings = getSharedPreferences(PREFS_ZOOP_API_DEMO, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.putString("marketplaceId", marketplaceId);
		editor.putString("publishableKey", publishableKey);
		editor.commit();		
	}
	
	public String[] getValidUsernameAndPassword() {
		SharedPreferences settings = getSharedPreferences(PREFS_ZOOP_API_DEMO, Context.MODE_PRIVATE);
		String[] results = new String[4];
		results[0] = settings.getString("username", null);		
		results[1] = settings.getString("password", null);
		results[2] = settings.getString("marketplaceId", null);
		results[3] = settings.getString("publishableKey", null);
		return results;
	}
	public void setSelectedSellerId(String sellerId) {
		SharedPreferences settings = getSharedPreferences(PREFS_ZOOP_API_DEMO, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("sellerId", sellerId);
		editor.commit();		
	}
	
	public String getSelectedSellerId() {
		SharedPreferences settings = getSharedPreferences(PREFS_ZOOP_API_DEMO, MODE_PRIVATE);
		String results;
		results = settings.getString("sellerId", null);		
		return results;
	}
*/	
}
