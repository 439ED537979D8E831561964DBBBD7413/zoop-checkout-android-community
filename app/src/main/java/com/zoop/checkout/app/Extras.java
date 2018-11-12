package com.zoop.checkout.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.view.inputmethod.InputMethodManager;
import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsCheckout;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressLint("NewApi")
public class Extras {

    private static Extras instance = null;
    private DecimalFormat currencyDecimalFormatter;
    private DecimalFormat numberDecimalFormatter;
    private String sCurrencySymbol;
    public String sReplacingURL = null;
    private static BluetoothSocket mBtSocketUniqueAndStatic = null;
    private static OutputStream outputStreamStaticAndUnique = null;
    public final int ZOOP_POST = 2;
    public final int ZOOP_GET = 3;
    public int ZOOP_DELETE = 4;

    public static Extras getInstance() {
        if (null == instance) {
            instance = new Extras();
        }
        return instance;
    }

    private Extras() {
        currencyDecimalFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.getDefault());
        currencyDecimalFormatter.setGroupingUsed(true);
        currencyDecimalFormatter.setDecimalSeparatorAlwaysShown(true);
        currencyDecimalFormatter.setParseBigDecimal(true);
        currencyDecimalFormatter.setMinimumFractionDigits(2);
        currencyDecimalFormatter.setMaximumFractionDigits(2);
        try {
            currencyDecimalFormatter.setRoundingMode(RoundingMode.HALF_DOWN);
        } catch (Exception e) {
            ZLog.exception(677314, e);
        }
        sCurrencySymbol = currencyDecimalFormatter.getDecimalFormatSymbols().getCurrencySymbol();

        numberDecimalFormatter = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        numberDecimalFormatter.setGroupingUsed(true);
        numberDecimalFormatter.setDecimalSeparatorAlwaysShown(true);
        numberDecimalFormatter.setParseBigDecimal(true);
        numberDecimalFormatter.setMinimumFractionDigits(2);
        numberDecimalFormatter.setMaximumFractionDigits(2);
        try {
            numberDecimalFormatter.setRoundingMode(RoundingMode.HALF_DOWN);
        } catch (Exception e2) {
            ZLog.exception(677315, e2);
        }

//		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
//		otherSymbols.setDecimalSeparator(',');
//		otherSymbols.setGroupingSeparator('.'); 
//		DecimalFormat df = new DecimalFormat(formatString, otherSymbols);
//		L.d("LOCALE="+Locale.getDefault());
        //currencyDecimalFormatter.set
//		currencyDecimalFormatter.setC
        //currencyDecimalFormatter.getDecimalFormatSymbols().setCurrencySymbol("");
    }


    public static Date getDateFromFullZoopAPITimestampString(String sTimestamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzzz");
        Date parsedData = new Date();
        parsedData = dateFormat.parse(sTimestamp);
        return parsedData;
    }

    public static Date getDateFromTimestampStringAtTimezone(String sTimestamp, String sTimezoneId) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone(sTimezoneId));
        Date parsedData = new Date();
        parsedData = dateFormat.parse(sTimestamp);
        return parsedData;
    }


    public static String getFormattedDate(Date date, String sDateFormat) {
        return DateFormat.format(sDateFormat, date).toString();
    }

    public static String getFormattedDateForUTCTimestamp(long timestamp) {
        Calendar utcDate = Calendar.getInstance(TimeZone.getDefault());
        utcDate.setTimeInMillis(timestamp);
        //Date date = new Date(utcDate);
        return utcDate.get(Calendar.DAY_OF_MONTH) + "/" + (utcDate.get(Calendar.MONTH) + 1) + "/" + utcDate.get(Calendar.YEAR);
    }

    public static String getPaymentTypeInLocalLanguage(String paymentType) throws Exception {
        String[] paymentTypes = {"credit", "debit"};
        String[] localLanguagePaymentTypes = {"crédito", "débito"};
        for (int i = 0; i < paymentTypes.length; i++) {
            if (0 == paymentType.compareToIgnoreCase(paymentTypes[i])) {
                return localLanguagePaymentTypes[i];
            }
        }
        throw new Exception("Unexpected error: Payment type not found");
    }

    public static BigDecimal addDigitToBigDecimal(String sCurrentValue, int intDigitValue) throws Exception {
        try {

            BigDecimal newValue = null;
            //String customValueString =  getInstance().sCurrencySymbol+sCurrentValue;
            BigDecimal customValue = (BigDecimal) getInstance().numberDecimalFormatter.parse(sCurrentValue);
            newValue = customValue.movePointRight(3);
            newValue = newValue.add(new BigDecimal(intDigitValue));
            newValue = newValue.movePointLeft(2);
            return newValue;
        } catch (Exception exception) {
            L.e("Error parsing custom value for re-formatting, etc", exception);
            throw exception;
        }
    }

/*	public static boolean validatePhoneNumber(String phoneNumber) {
		if (-1 != phoneNumber.indexOf('+')) {
			
		}
		else if
	}
*/

    public static String getValidZoopSMSGatewayNumberFromUserInputPhoneNumber(String number, String countryCodeNumeric) {
        String cityCodeNumber = "21";
        if (-1 == number.indexOf('+')) {
            String sNewNumber = "";
            // No country code? Add country code...
            if (-1 == number.indexOf(countryCodeNumeric)) {
                sNewNumber = countryCodeNumeric;
            }
            // ToDo: Remove RIO auto-complete number. Currently because Rio has more users.
            // Will get from different sources in the future
            // Missing city code? Try to autocomplete with RIO!
            if (0 != number.indexOf(cityCodeNumber)) {
                if ((number.length() == 9) || (number.length() == 8)) {
                    sNewNumber += cityCodeNumber;
                }
            }
            sNewNumber += number;
            number = sNewNumber;
        }
        number = number.replace("+", "");
        number = number.replace("-", "");
        number = number.replace(" ", "");
        number = number.replace(",", "");
//ZLog.t("Number used: "+number);		
        return number;
    }

    // For backspace pressed in value entry
    //@SuppressLint("NewApi")
    public BigDecimal divideBy10(BigDecimal value) throws ParseException {
        BigDecimal newValue = value.divide(new BigDecimal(10), 2, RoundingMode.DOWN);
        return newValue;
    }

    // Transaction can only be
    public static boolean checkIfTransactionWasCancelled(JSONObject joTransaction) {
        try {
            if (joTransaction.getBoolean("voided")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ZLog.exception(300015, e);
            return false;
        }
    }

    public static boolean checkIfTransactionWasRejected(JSONObject joTransaction) {
        try {
            if (0 == joTransaction.getString("status").compareTo("reversed")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ZLog.exception(300015, e);
            return false;
        }
    }


    public String getPlanInfo(String paymentOption, BigDecimal valuePaid, int numberInstallments) throws JSONException {

        JSONObject joPlanSubscription;
        JSONArray joFee_details;

        String planInfoPayment = null;

        joPlanSubscription = APIParametersCheckout.getInstance().getPlanSubscription();

        joFee_details = joPlanSubscription.getJSONArray("fee_details");
        BigDecimal taxadd = new BigDecimal(0);
        BigDecimal amountadd = new BigDecimal(0);
        for (int j = 0; j < joFee_details.length(); j++) {
            JSONObject jObject = ((JSONObject) joFee_details.get(j));


            if ((joFee_details.getJSONObject(j).getString("payment_type").equals(paymentOption)) && (joFee_details.getJSONObject(j).getInt("number_installments") == 1) && (joFee_details.getJSONObject(j).getInt("number_installments") == numberInstallments)) {


                BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount"));
                tax = tax.divide(new BigDecimal(100));
                taxadd = taxadd.add(tax);

                BigDecimal bValueZoop = ((taxadd.multiply(valuePaid)).divide(new BigDecimal(100)));


                BigDecimal bValueTotal = valuePaid.subtract(bValueZoop);
                if (joFee_details.getJSONObject(j).getDouble("percent_amount") > 0) {

                    BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                    amountadd = amountadd.add(amount);
                    bValueTotal = bValueTotal.subtract(amountadd);


                }


                if (joPlanSubscription.getString("name").equals("Plano Pro")) {

                    planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal) + " será depositado hoje ou no próximo dia util.";
                } else if (joPlanSubscription.getString("name").equals("Plano Standard")) {

                    if (paymentOption.equals("debit")) {


                        planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal) + " será depositado em até dois dias úteis.";


                    } else if (paymentOption.equals("credit")) {

                        planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal) + " será depositado em até 30 dias.";


                    }


                } else {

                    planInfoPayment = joPlanSubscription.getString("description") + ". Valor a receber: " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal);
                }

            } else if ((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getInt("number_installments") == numberInstallments) && (numberInstallments > 1)) {


                BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount"));
                tax = tax.divide(new BigDecimal(100));
                taxadd = taxadd.add(tax);

                BigDecimal bValueZoop = ((taxadd.multiply(valuePaid)).divide(new BigDecimal(100)));


                BigDecimal bValueTotal = valuePaid.subtract(bValueZoop);
                if (joFee_details.getJSONObject(j).getDouble("percent_amount") > 0) {

                    BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                    amountadd = amountadd.add(amount);
                    bValueTotal = bValueTotal.subtract(amountadd);


                }


                if (joPlanSubscription.getString("name").equals("Plano Pro")) {
                    planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal) + " será depositado hoje ou no próximo dia util.";
                } else if (joPlanSubscription.getString("id").equals("Plano Standard")) {
                    BigDecimal bValueinstallment = null;
                    try {
                        BigDecimal number = new BigDecimal(numberInstallments);
                        bValueinstallment = bValueTotal.divide(number, 2, RoundingMode.DOWN);
                    } catch (Exception e) {
                        ZLog.exception(300073, e);
                    }
                    planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueinstallment) + " será depositado a cada 30 dias";
                } else {
                    planInfoPayment = joPlanSubscription.getString("description") + ". Valor a receber: " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal);
                }
            } else {
                if (joPlanSubscription.getString("id").equals("pgto_instantaneo_high_volume_d0") && numberInstallments > 1) {
                    JSONObject joPlan = APIParametersCheckout.getInstance().getPlan();
                    int length = joPlan.getJSONArray("items").length();
                    for (int i = 0; i < length; i++) {
                        JSONArray joFee_detailsPlan = joPlan.getJSONArray("items").getJSONObject(i).getJSONArray("fee_details");
                        for (int z = 0; z < joFee_detailsPlan.length(); z++) {
                            if ((joFee_detailsPlan.getJSONObject(z).getString("payment_type").equals("credit")) && ((joFee_detailsPlan.getJSONObject(z).getInt("number_installments") == numberInstallments) && (numberInstallments > 1))) {
                                BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount"));
                                tax = tax.divide(new BigDecimal(100));
                                taxadd = taxadd.add(tax);

                                BigDecimal bValueZoop = ((taxadd.multiply(valuePaid)).divide(new BigDecimal(100)));

                                BigDecimal bValueTotal = valuePaid.subtract(bValueZoop);
                                if (joFee_details.getJSONObject(j).getDouble("percent_amount") > 0) {

                                    BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                                    amountadd = amountadd.add(amount);
                                    bValueTotal = bValueTotal.subtract(amountadd);
                                }
                                planInfoPayment = "O valor de " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(bValueTotal) + " será depositado hoje ou no próximo dia util.";
                            }
                        }
                    }
                }
            }
        }
        return planInfoPayment;
    }

    public String getNamePlan() {

        String namePlan = "";

        JSONObject joPlanSubscription = APIParametersCheckout.getInstance().getPlanSubscription();

        try {
            namePlan = joPlanSubscription.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return namePlan;

    }

    public ArrayList<String> getPlansArray(JSONArray joFee_details) throws Exception {
        String percent_amount_debit = null;
        String percent_amount_credit = null;
        String percent_amount_creditInstallments = null;


        ArrayList<String> plans = new ArrayList<String>();

        BigDecimal taxaddDebit = new BigDecimal(0);
        BigDecimal taxaddCredit = new BigDecimal(0);
        BigDecimal amountaddDebit = new BigDecimal(0);
        BigDecimal amountaddCredit = new BigDecimal(0);
        String amountInfoCredit = "";
        String amountInfoDebit = "";
        String amountInfoCreditInstallments = "";


        for (int j = 0; j < joFee_details.length(); j++) {
            JSONObject jObject = ((JSONObject) joFee_details.get(j));


            if (joFee_details.getJSONObject(j).getString("payment_type").equals("debit")) {
                BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));


                taxaddDebit = taxaddDebit.add(tax);
                if (joFee_details.getJSONObject(j).getDouble("dollar_amount") > 0) {

                    BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                    amountaddDebit = amountaddDebit.add(amount);
                    amountInfoDebit = " + " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(amountaddDebit);


                }


            } else if ((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getString("number_installments").equals("1"))) {
                BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                taxaddCredit = taxaddCredit.add(tax);

                if (joFee_details.getJSONObject(j).getDouble("dollar_amount") > 0) {

                    BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                    amountaddCredit = amountaddCredit.add(amount);
                    amountInfoCredit = " + " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(amountaddCredit);


                }


            }


        }
        percent_amount_debit = "Débito  à vista (" + String.valueOf(taxaddDebit) + "%)" + amountInfoDebit;
        percent_amount_credit = "Crédito  à vista (" + String.valueOf(taxaddCredit) + "%)" + amountInfoCredit;
        plans.add(percent_amount_debit);
        plans.add(percent_amount_credit);
        for (int i = 2; i <= 12; i++) {
            BigDecimal taxaddCreditInstallments = new BigDecimal(0);
            BigDecimal amountaddCreditInstallments = new BigDecimal(0);

            for (int j = 0; j < joFee_details.length(); j++) {
                JSONObject jObject = ((JSONObject) joFee_details.get(j));

                if ((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getInt("number_installments") == i)) {


                    BigDecimal tax = new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                    taxaddCreditInstallments = taxaddCreditInstallments.add(tax);
                    if (joFee_details.getJSONObject(j).getDouble("dollar_amount") > 0) {

                        BigDecimal amount = new BigDecimal(joFee_details.getJSONObject(j).getDouble("dollar_amount")).divide(new BigDecimal(100));
                        amountaddCreditInstallments = amountaddCreditInstallments.add(amount);
                        amountInfoCreditInstallments = " + " + Extras.getInstance().formatBigDecimalAsLocalMoneyString(amountaddCreditInstallments);


                    }
                }
            }
            percent_amount_creditInstallments = "Crédito  em " + i + "x (" + String.valueOf(taxaddCreditInstallments) + "%)" + amountInfoCreditInstallments;
            plans.add(percent_amount_creditInstallments);

        }

        return plans;


    }


    public static boolean checkIfTransactionWasPending(JSONObject joTransaction) {
        try {
            if (0 == joTransaction.getString("status").compareToIgnoreCase("pending")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ZLog.exception(300015, e);
            return false;
        }
    }

    public static boolean checkIfTransactionWasFailed(JSONObject joTransaction) {
        try {
            if (0 == joTransaction.getString("status").compareToIgnoreCase("Failed")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ZLog.exception(300015, e);
            return false;
        }
    }

    public static boolean checkIfTransactionWasCancel(JSONObject joTransaction) {
        try {
            if (0 == joTransaction.getString("status").compareToIgnoreCase("canceled")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            ZLog.exception(300015, e);
            return false;
        }
    }


    // Transaction can only be
    public static boolean checkIfTransactionCanBeCancelled(JSONObject joTransaction) {
        try {
            //"status":"canceled",
            // if the status is not succeeded, we can't cancel.
            if (0 == joTransaction.getString("status").compareTo("succeeded")) {
                //if ( (false == joTransaction.getBoolean("voided")) && (false == joTransaction.getBoolean("refunded")) ) {
                String sTransactionDateTime = joTransaction.getString("created_at");
                Date transactionDate = Extras.getDateFromTimestampStringAtTimezone(sTransactionDateTime, "UTC");
                Date currentDate = Calendar.getInstance().getTime();
                //Calendar transactionDate = Calendar.getInstance().getTime();
                //Date currentTime = Date.

//				Calendar c = Calendar.getInstance(); 
                if (
                        (transactionDate.getYear() == currentDate.getYear()) &&
                                (transactionDate.getMonth() == currentDate.getMonth()) &&
                                (transactionDate.getDay() == currentDate.getDay())
                        ) {
                    return true;
                }
            }
        } catch (Exception e) {
            ZLog.exception(300013, e);
        }
        return false;
    }

    //NumberFormat.getCurrencyInstance().parse((String) tvCustomAmount.getText()).doubleValue()

    public String formatBigDecimalAsLocalString(BigDecimal value) {
        String sFormattedValue = numberDecimalFormatter.format(value);
        return sFormattedValue;
    }

    public String formatBigDecimalAsLocalMoneyString(BigDecimal value) {
        String sFormattedValue = currencyDecimalFormatter.format(value);
        return sFormattedValue;
    }

    public String getUSDecimalStringAsLocalMoneyString(String sDecimalValue) {
        BigDecimal bdValue = getBigDecimalFromDecimalString(sDecimalValue);
        return formatBigDecimalAsLocalMoneyString(bdValue);
    }

    public BigDecimal getBigDecimalFromDecimalString(String sMoneyValue) {
        try {
/*			sMoneyValue = sMoneyValue.replace(",", "%");
			sMoneyValue = sMoneyValue.replace(".", ",");
			sMoneyValue = sMoneyValue.replace("%", ".");
*/			
/*
//			sMoneyValue = "R$3.431,56";
			int iCommaPosition = sMoneyValue.lastIndexOf(",");
			int iPointPosition = sMoneyValue.lastIndexOf(".");
			
			String decimalSeparator;
			if (iCommaPosition > iPointPosition) {
				decimalSeparator = ",";
			}
			else if (iCommaPosition < iPointPosition) {
				decimalSeparator = ".";
			}
			else {
				ZLog.t(677233, sMoneyValue);
				throw new Exception("Error formatting money value");
			}
			
			sMoneyValue = sMoneyValue.replaceAll("[^0-9\\"+decimalSeparator+"]", "");
			if (decimalSeparator.compareTo(",")==0) {
				sMoneyValue = sMoneyValue.replace(",", ".");
				
			}
			L.d("Valor="+sMoneyValue);
//			sMoneyValue = sMoneyValue.replace(",", "");
			return new BigDecimal(sMoneyValue);
//			return (BigDecimal) NumberFormat.getCurrencyInstance().parse(sMoneyValue);
*/
            //sMoneyValue = sCurrencySymbol+sMoneyValue;
            BigDecimal value = (BigDecimal) numberDecimalFormatter.parse(sMoneyValue);
            return value;

        } catch (Exception e) {
            ZLog.error(677481, sMoneyValue);
            ZLog.exception(677481, e);
            try {
                getBigDecimalFromDecimalStringInZoopPaymentsFormat(sMoneyValue);
            } catch (Exception e2) {
                ZLog.exception(677482, e2);
            }
            return null;
        }
    }

    public BigDecimal getBigDecimalFromDecimalStringInZoopPaymentsFormat(String sDecimal) {
        try {
            BigDecimal value = getBigDecimalFromDecimalStringInZoopPaymentsFormatRaw(sDecimal);
            return value;
        } catch (Exception e) {
            ZLog.error(677480, sDecimal);
            ZLog.exception(677480, e);
            return new BigDecimal(0);
        }
    }

 /*   public void signin(Context c) {


        String sCheckoutPublicKey = APIParameters.getInstance().getStringParameter("sCheckoutPublicKey");
        String sUsername = APIParameters.getInstance().getStringParameter("currentLoggedinUsername");
        String sPassword = APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken");

        RequestParams requestParams = new RequestParams();
        requestParams.put("email", sUsername);
        requestParams.put("password", sPassword);

        APIParameters ap = APIParameters.getInstance();


        ap.putStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);

        JSONObject joResponse = null;
        try {
            //String[] aSigninURLs = {"http://64.62.167.81/Zoop/tools/signinTemp.php", "http://dev.api.checkout.pagzoop.com/user/signin", "https://api.zoop.ws/v1/users/signin"};
            //String sSigninURL = "http://64.62.167.81/Zoop/tools/signinTemp.php";
            //String sSigninURL = "http://fazzan.com/h";

            String sURL = "https://api.zoopcheckout.com/v1/sessions";

//			String sURL = Extras.getInstance().getCheckoutWebservicesBaseURL()+"/sessions";

            //joResponse = ZoopSession.getInstance().postSynchronousRequest(sURL, sCheckoutPublicKey, requestParams);

            //	String t=CookieManager.getInstance().getCookie(sURL);
            //	ZLog.t(t);


            SyncHttpClient client = new SyncHttpClient();

            //client.setBasicAuth(sCheckoutPublicKey, "");
            PersistentCookieStore myCookieStore = new PersistentCookieStore(c);


            client.setCookieStore(myCookieStore);

            client.post(sURL, requestParams, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });


            client = new SyncHttpClient();

            client.setBasicAuth(sCheckoutPublicKey, "");
            myCookieStore = new PersistentCookieStore(c);


            client.setCookieStore(myCookieStore);

            client.post(sURL, requestParams, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });


            HttpContext httpContext = client.getHttpContext();
            CookieStore cookieStore = (CookieStore) httpContext.getAttribute(ClientContext.COOKIE_STORE);
            List<cz.msebera.android.httpclient.cookie.Cookie> t = cookieStore.getCookies();

            String sCookie = t.get(0).getValue();

            APIParameters.getInstance().putStringParameter("cookie", sCookie);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/
    public BigDecimal getBigDecimalFromDecimalStringInZoopPaymentsFormatRaw(String sDecimal) throws ParseException {
        DecimalFormat numberZoopAPIDecimalFormatter = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        numberZoopAPIDecimalFormatter.setParseBigDecimal(true);

        try {
            numberZoopAPIDecimalFormatter.setRoundingMode(RoundingMode.HALF_DOWN);
        } catch (Exception e2) {
            ZLog.exception(677315, e2);
        }
        BigDecimal value = (BigDecimal) numberZoopAPIDecimalFormatter.parse(sDecimal);
        return value;
    }

    public static String leftPadZerosInteger(int number, int numberLength) {
        return String.format("%0" + numberLength + "d", number);
    }

    public static void hideKeyboard(Activity a) {
        try {
            InputMethodManager inputManager = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            ZLog.t(300036);
        }
    }

    public static void startNewChargeActivity(Activity activity) {
        Intent newCharge = new Intent(activity, ChargeActivity.class);
        newCharge.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(newCharge);
    }


    public static void showVoidTransactionConfirmationDialog(final ReceiptActivity activityParent, final JSONObject joTransactionResponse) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(activityParent);

            alert.setTitle(activityParent.getResources().getString(R.string.dialog_void_receipt_transaction_title));
            String sConfirmationMessage = activityParent.getResources().getString(R.string.dialog_void_receipt_transaction_text_confirm_void);
            sConfirmationMessage = sConfirmationMessage.replace("[transaction_value]", joTransactionResponse.getString("amount"));
            sConfirmationMessage = sConfirmationMessage.replace("[masked_card_number]", joTransactionResponse.getJSONObject("payment_method").getString("first4_digits"));
            alert.setMessage(sConfirmationMessage);

            alert.setPositiveButton("Estornar Venda", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    VoidTransactionDialog voidTransactionDialog = new VoidTransactionDialog(activityParent, joTransactionResponse);

                }
            });

            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ZLog.t(300006);
                }
            });

            alert.show();
        } catch (Exception e) {
            ZLog.exception(300005, e);
        }
    }


    public String buildInstallmentSummaryString(BigDecimal chargeTotal, int numberOfInstallments) {
        try {
            //DecimalFormat currencyFormatter;
            //currencyFormatter = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            //currencyFormatter.setParseBigDecimal(true);
            String sTotal = formatBigDecimalAsLocalMoneyString(chargeTotal);
            BigDecimal totalToCharge;
            //totalToCharge = (BigDecimal) currencyDecimalFormatter.parse(chargeTotal);
            totalToCharge = chargeTotal;
            BigDecimal installmentTotal = totalToCharge.divide(new BigDecimal(numberOfInstallments), 2, BigDecimal.ROUND_CEILING);
            String s = numberOfInstallments + " vezes de " + formatBigDecimalAsLocalMoneyString(installmentTotal) + ", total " + sTotal;
            return s;
        } catch (Exception e) {
            ZLog.exception(677296, e);
            return null;
        }
    }

    public void setCheckoutWebservicesBaseURL(String sURL) {
        sReplacingURL = sURL;
    }

    public String getCheckoutWebservicesBaseURL() {
        return sReplacingURL;
/*		if (null == sReplacingURL) {
			return URL;
		}
		else {
			// Should use https://prefix.
			String newURL = URL.replace(sRegularURL, sReplacingURL);
			Log.d("Checkout API", "URL="+newURL);
			return newURL;
		}
*/
    }


    public static String lPadChar(String s, char c, int length) {
        int sLength = s.length();
        String ns = " ";
        for (int i = 0; i < length; i++) {
            ns += c;
        }
        return ns + s;
    }

    public static String lPadChar2(String s, String c, int length) {
        int sLength = s.length();
        String ns = " ";
        for (int i = 0; i < length; i++) {
            ns += c;
        }
        return ns + s;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String getDeviceTypeString(Context context) {
        if (isTablet(context)) {
            return context.getResources().getString(R.string.device_type_tablet);
        } else {
            return context.getResources().getString(R.string.device_type_smartphone);
        }
    }

    public static void checkZoopTerminalsAndGoToNextStep(Activity currentActivity) {
        try {

            // If the user is logging in again and a terminal has been selected,
            // there is no need to talk about selecting terminal.
            if (null == TerminalListManager.getCurrentSelectedZoopTerminal()) {

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent welcomeCheckoutIntent = new Intent(currentActivity, WelcomeCheckoutActivity.class);
                    welcomeCheckoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


                    currentActivity.startActivity(welcomeCheckoutIntent);
                }
                ZLog.t(677169);
                int numberOfZoopTerminalsFound = 0;
                BluetoothDevice btDeviceLast = null;
                for (BluetoothDevice bt : pairedDevices) {
                    ZLog.t(677170, bt.getName() + "/ " + bt.getAddress(), bt.getBondState());
                    // Datecs terminal name starts with PP
                    String regexTerminalsAcceptedt = APIParameters.getInstance().getStringParameter("AS.regexTerminalsAcceptedt");
                    if (bt.getName().matches(regexTerminalsAcceptedt)) {
                        numberOfZoopTerminalsFound++;
                        btDeviceLast = bt;
                    }

                }

                if (0 == numberOfZoopTerminalsFound) {
                    Intent welcomeCheckoutIntent = new Intent(currentActivity, WelcomeCheckoutActivity.class);
                    welcomeCheckoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    currentActivity.startActivity(welcomeCheckoutIntent);
                    currentActivity.finish();
                    //xx Is this finish needed?
                    //	                        finish();
                } else if (1 == numberOfZoopTerminalsFound) {
                    // XX1.9
                    //ZoopTerminal.setSelectedTerminal(btDeviceLast);
                    // Gambiarra - maneira fácil de notificar sobre o 1 terminal
                    ChargeActivity.addMessageUponLogin(ChargeActivity.SHOW_MESSAGE_ZOOP_TERMINAL_AUTOMATICALLY_SELECTED_BY_USE_BY_CHECKOUT_APP);
                    Intent chargeIntent = new Intent(currentActivity, ChargeActivity.class);
                    chargeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    currentActivity.startActivityForResult(chargeIntent, 0);
                } else if (numberOfZoopTerminalsFound >= 2) {
                    ConfigPinPadActivity.bShowQuickInstructions = true;
                    Intent configPinpadIntent = new Intent(currentActivity, ConfigPinPadActivity.class);
                    configPinpadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    currentActivity.startActivity(configPinpadIntent);
                    currentActivity.finish();
                }
            } else {
                Intent chargeIntent = new Intent(currentActivity, ChargeActivity.class);
                chargeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                currentActivity.startActivityForResult(chargeIntent, 0);
            }
        } catch (Exception e) {
            ZLog.exception(300057, e);
            Intent welcomeCheckoutIntent = new Intent(currentActivity, WelcomeCheckoutActivity.class);
            welcomeCheckoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            currentActivity.startActivity(welcomeCheckoutIntent);
        }
    }


    public static boolean checkZoopTerminalsAndGoToNextStepStartupActivity(Activity currentActivity) {
        try {

            // If the user is logging in again and a terminal has been selected,
            // there is no need to talk about selecting terminal.
            if (null == TerminalListManager.getCurrentSelectedZoopTerminal()) {

                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (!mBluetoothAdapter.isEnabled()) {
                    return false;
                }
                ZLog.t(677169);
                int numberOfZoopTerminalsFound = 0;
                BluetoothDevice btDeviceLast = null;
                for (BluetoothDevice bt : pairedDevices) {
                    ZLog.t(677170, bt.getName() + "/ " + bt.getAddress(), bt.getBondState());
                    // Datecs terminal name starts with PP
                    String regexTerminalsAcceptedt = APIParameters.getInstance().getStringParameter("AS.regexTerminalsAcceptedt");
                    if (bt.getName().matches(regexTerminalsAcceptedt)) {
                        numberOfZoopTerminalsFound++;
                        btDeviceLast = bt;
                    }

                }

                if (0 == numberOfZoopTerminalsFound) {
                    return false;
                    //xx Is this finish needed?
                    //	                        finish();
                } else if (numberOfZoopTerminalsFound >= 1) {
                    return true;

                }
            } else {
                return true;

            }
        } catch (Exception e) {
            ZLog.exception(300057, e);
        }
        return false;
    }


    public void showUnexpectedErrorMessage(String message) {

    }

/*

    public JSONObject requestWithCookie(int zoopVerb, String URL, String authUser, RequestParams requestParams, Context c) throws Exception {
        final int ZOOP_PUT = 1;
        final int ZOOP_POST = 2;
        final int ZOOP_GET = 3;
        int ZOOP_DELETE = 4;
        JSONObject joResponse = null;
        String cookie = APIParameters.getInstance().getStringParameter("cookie");


        try {

            if (zoopVerb == ZOOP_PUT) {


            } else if (zoopVerb == ZOOP_POST) {

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().postSynchronousRequest(URL, authUser, requestParams, "laravel_session=" + cookie);

            } else if (zoopVerb == ZOOP_GET) {

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().getSynchronousRESTRequest(URL, authUser, "laravel_session=" + cookie);

            } else if (zoopVerb == ZOOP_DELETE) {

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().deleteSynchronousRESTRequest(URL, authUser, requestParams, "laravel_session=" + cookie);


            }


        } catch (Exception e) {

            if (zoopVerb == ZOOP_PUT) {


            } else if (zoopVerb == ZOOP_POST) {

                Extras.getInstance().signin(c);
                cookie = APIParameters.getInstance().getStringParameter("cookie");

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().postSynchronousRequest(URL, authUser, requestParams, "laravel_session=" + cookie);


            } else if (zoopVerb == ZOOP_GET) {

                Extras.getInstance().signin(c);
                cookie = APIParameters.getInstance().getStringParameter("cookie");

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().getSynchronousRESTRequest(URL, authUser, "laravel_session=" + cookie);


            } else if (zoopVerb == ZOOP_DELETE) {

                Extras.getInstance().signin(c);
                cookie = APIParameters.getInstance().getStringParameter("cookie");

                joResponse = com.zoop.checkout.app.ZoopSession.getInstance().deleteSynchronousRESTRequest(URL, authUser, requestParams, "laravel_session=" + cookie);
            }
        }

        return joResponse;

    }
*/



    public static void fetchReceivingPlanFromServer(String sMarketplaceId, String sSellerId, String publishableKey, Activity a) throws Exception {
        APIParameters ap = APIParameters.getInstance();
        String sPlanURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/plans");
        JSONObject joPlan= ZoopSessionsPayments.getInstance().syncGet(sPlanURL, publishableKey, a);
        String sPlanSubscriptionsUrl = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId + "/subscriptions");
        JSONObject joPlanSubscriptions= ZoopSessionsPayments.getInstance().syncGet(sPlanSubscriptionsUrl, publishableKey, a);
        if (joPlanSubscriptions.getJSONArray("items").length() < 1) {
            int length = joPlan.getJSONArray("items").length();
            for (int i = 0; i < length; i++) {
                if (joPlan.getJSONArray("items").getJSONObject(i).getString("name").equals("Plano Standard")) {
                    ap.putStringParameter("planSubscription", joPlan.getJSONArray("items").getJSONObject(i).toString());
                    ap.putStringParameter("planSubscriptionId", joPlan.getJSONArray("items").getJSONObject(i).getString("id"));
                }
            }
        } else {
            ap.putStringParameter("planSubscription", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getJSONObject("plan").toString());
            ap.putStringParameter("planSubscriptionId", joPlanSubscriptions.getJSONArray("items").getJSONObject(0).getString("id"));
        }
        ap.putStringParameter("plan", joPlan.toString());

    }

/*
    public static void fetchReceivingDocumentsFromServer(String merchant, String sCheckoutPublicKey, Activity activity) throws Exception {
        APIParameters ap = APIParameters.getInstance();
        JSONObject joSeller = new JSONObject(ap.getStringParameter("Seller"));
        String statusJoSeller = joSeller.getString("status");
        if ((statusJoSeller.equals("active")) || (statusJoSeller.equals("enabled"))) {
            ap.putBooleanParameter("seller_activate", true);
        } else {
            ap.putBooleanParameter("seller_activate", false);
            String sDocumentsUrl = "https://api.zoopcheckout.com/v1/documents/" + merchant;
            String cookie = APIParameters.getInstance().getStringParameter("cookie");
            JSONObject joDocuments;
            joDocuments = Extras.getInstance().requestWithCookie(Extras.getInstance().ZOOP_GET, sDocumentsUrl, sCheckoutPublicKey, null, activity);
            ap.putStringParameter("joDocuments", joDocuments.toString());
        }
    }
*/


    public void loginMigrate(Activity activity) throws Exception {
        String sUsername = APIParameters.getInstance().getStringParameter("currentLoggedinUsername");
        String sPassword = APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken");
        String sCheckoutPublicKey;
        // If there is any server suffix, separate it and send as 2 parameters
        String regexGetServerSuffixEnvironmentMagic = APIParameters.getInstance().getStringParameter(APISettingsConstants.ZoopCheckout_AlternateServerSuffixRegex);
        Matcher m = Pattern.compile(regexGetServerSuffixEnvironmentMagic).matcher(sUsername);
        String sAdditionalLoginString = null;
        if (m.find()) {
            sAdditionalLoginString = m.group(1);
            APIParameters.getInstance().putStringParameter(Integer.toString(APISettingsConstants.ZoopCheckout_AdditionalLoggedInString), sAdditionalLoginString);
        }
        if (sUsername.indexOf("#") > 0) { // If there is a additional login string (#xxx#)
            sUsername = sUsername.substring(0, sUsername.indexOf("#"));
        }
        sCheckoutPublicKey = Preferences.getInstance().loadUFUAndGetCheckoutPublicKey(sUsername, sAdditionalLoginString);
        APIParameters.getInstance().putStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("email", sUsername);
        requestParams.put("password", sPassword);
        APIParameters ap = APIParameters.getInstance();
        JSONObject joResponse = null;
        String sURL = "https://api.zoopcheckout.com/v1/sessions";
        ZLog.t(sUsername);
        ZLog.t(sPassword);
        ZLog.t(sURL);
        ZLog.t(sCheckoutPublicKey);
        joResponse = ZoopSessionsCheckout.getInstance().syncPost(sURL, sCheckoutPublicKey, requestParams, activity);
        JSONObject joContent = joResponse.getJSONObject("content");
        JSONObject joUser = joContent.getJSONObject("user");
        String sMarketplaceId = joResponse.getJSONObject("content").getJSONArray("marketplace").getString(0);
        JSONObject joMerchant = joUser.getJSONObject("merchant");
        String publishableKey = joContent.getJSONArray("publishable_key").getString(0);
        String sFirstName = joUser.getString("firstName");
        String slastName = joUser.getString("lastName");
        String merchant = joResponse.getJSONObject("content").getString("merchant");
        String sSellerId = joMerchant.getString("sellerId");
        // Get the first seller in the list.
        // For simple testers, there will always be a seller selected.
        //sellerId = "50b44c1890f44de58a76c722b37e4362"; // U Street Café
        //Zoop Seller ID produção - sellerId = "6fbf7ec8fa7c4429a3b03eb85dd43364";
        String sSellersURL = UFUC.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId);
        JSONObject joSeller = ZoopSessionsPayments.getInstance().syncGet(sSellersURL, publishableKey, activity);
        if (com.zoop.zoopandroidsdk.commons.Configuration.DEBUG_MODE) {
            ap.dumpAPISettings();
        }
        ap.putStringParameter("publishableKey", publishableKey);
        ap.putStringParameter("marketplaceId", sMarketplaceId);
        ap.putStringParameter("phoneddd", joUser.getString("phone").substring(1, 3));
        ap.putStringParameter("currentLoggedinUsername", sUsername);
        ap.putStringParameter("currentLoggedinSecurityToken", sPassword);
        ap.putStringParameter("firstname", sFirstName);
        ap.putStringParameter("lastname", slastName);
        ap.putStringParameter("Seller", joSeller.toString());
        ap.putStringParameter("seller_id", sSellerId);
        ap.putStringParameter("merchant", merchant);
        String statusJoSeller = joSeller.getString("status");
        if ((statusJoSeller.equals("active")) || (statusJoSeller.equals("enabled"))) {
            ap.putBooleanParameter("seller_activate", true);
        } else {
            ap.putBooleanParameter("seller_activate", false);
        }
        CallUpdateApiParameters.getInstance().initializeApiParameters(activity);


    }
	/*public void loginMigrate(Activity activity) throws Exception {
        String sUsername = APIParameters.getInstance().getStringParameter("currentLoggedinUsername");
        String sPassword = APIParameters.getInstance().getStringParameter("currentLoggedinSecurityToken");
        String sCheckoutPublicKey;
        // If there is any server suffix, separate it and send as 2 parameters
        String regexGetServerSuffixEnvironmentMagic = APIParameters.getInstance().getStringParameter(APISettingsConstants.ZoopCheckout_AlternateServerSuffixRegex);
        Matcher m = Pattern.compile(regexGetServerSuffixEnvironmentMagic).matcher(sUsername);
        String sAdditionalLoginString = null;
        if (m.find()) {
            sAdditionalLoginString = m.group(1);
            APIParameters.getInstance().putStringParameter(Integer.toString(APISettingsConstants.ZoopCheckout_AdditionalLoggedInString), sAdditionalLoginString);
        }
        if (sUsername.indexOf("#") > 0) { // If there is a additional login string (#xxx#)
            sUsername = sUsername.substring(0, sUsername.indexOf("#"));
        }
        sCheckoutPublicKey = Preferences.getInstance().loadUFUAndGetCheckoutPublicKey(sUsername, sAdditionalLoginString);
        APIParameters.getInstance().putStringParameter("sCheckoutPublicKey", sCheckoutPublicKey);
        RequestParams requestParams = new RequestParams();
        requestParams.put("email", sUsername);
        requestParams.put("password", sPassword);
        APIParameters ap = APIParameters.getInstance();
        JSONObject joResponse = null;
        String sURL = "https://api.zoopcheckout.com/v1/sessions";
        ZLog.t(sUsername);
        ZLog.t(sPassword);
        ZLog.t(sURL);
        ZLog.t(sCheckoutPublicKey);
        joResponse = com.zoop.commons.ZoopSession.getInstance().postSynchronousRequest(sURL, sCheckoutPublicKey, requestParams);
        JSONObject joContent = joResponse.getJSONObject("content");
        JSONObject joUser = joContent.getJSONObject("user");
        String sMarketplaceId = joResponse.getJSONObject("content").getJSONArray("marketplace").getString(0);
        JSONObject joMerchant = joUser.getJSONObject("merchant");
        String publishableKey = joContent.getJSONArray("publishable_key").getString(0);
        String sFirstName = joUser.getString("firstName");
        String slastName = joUser.getString("lastName");
        String merchant = joResponse.getJSONObject("content").getString("merchant");
        String sSellerId = joMerchant.getString("sellerId");
        // Get the first seller in the list.
        // For simple testers, there will always be a seller selected.
        //sellerId = "50b44c1890f44de58a76c722b37e4362"; // U Street Café
        //Zoop Seller ID produção - sellerId = "6fbf7ec8fa7c4429a3b03eb85dd43364";
        String sSellersURL = ZoopAPI.getUFU("https://api.zoop.ws/v1/marketplaces/" + sMarketplaceId + "/sellers/" + sSellerId);
        JSONObject joSeller = com.zoop.commons.ZoopSession.getInstance().getSynchronousRESTRequest(sSellersURL, publishableKey);
        if (com.zoop.commons.Configuration.DEBUG_MODE) {
            ap.dumpAPISettings();
        }
        ZoopAPI.getInstance().initialize(activity.getApplication(), sMarketplaceId, joSeller.getString("id"), publishableKey);
        ap.putStringParameter("publishableKey", publishableKey);
        ap.putStringParameter("marketplaceId", sMarketplaceId);
        ap.putStringParameter("phoneddd", joUser.getString("phone").substring(1, 3));
        ap.putStringParameter("currentLoggedinUsername", sUsername);
        ap.putStringParameter("currentLoggedinSecurityToken", sPassword);
        ap.putStringParameter("firstname", sFirstName);
        ap.putStringParameter("lastname", slastName);
        ap.putStringParameter("Seller", joSeller.toString());
        ap.putStringParameter("seller_id", sSellerId);
        ap.putStringParameter("merchant", merchant);
        String statusJoSeller = joSeller.getString("status");
        if ((statusJoSeller.equals("active")) || (statusJoSeller.equals("enabled"))) {
            ap.putBooleanParameter("seller_activate", true);
        } else {
            ap.putBooleanParameter("seller_activate", false);

        }
    }*/

    public static OutputStream getSingleOutputStreamForBTSPPDeviceMACAddress(String sBTMACAddress) {
        try {
            BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(sBTMACAddress);
            UUID SPP_UUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            mBtSocketUniqueAndStatic = btDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            mBtSocketUniqueAndStatic.connect();
            outputStreamStaticAndUnique = mBtSocketUniqueAndStatic.getOutputStream();
            return outputStreamStaticAndUnique;
        } catch (IOException e) {
            ZLog.exception(677594, e);
            return null;
        }
    }


    public static void closeSingleBTSPPConnection() {
        try {
            outputStreamStaticAndUnique.close();
            mBtSocketUniqueAndStatic.close();
        } catch (IOException e) {
            ZLog.exception(677595, e);
        }
    }


	public String getCardBrand(String number){
		String cardBrand="";
		CardType card=CardType.detect(number);
		if(card==CardType.MASTERCARD){
			cardBrand="MASTERCARD";
		}else if(card==CardType.VISA){
			cardBrand="VISA";
		}else if(card==CardType.AMERICAN_EXPRESS){
			cardBrand="AMERICAN EXPRESS";
		}else if(card==CardType.DINERS_CLUB){
			cardBrand="DINERS CLUB";
		}else if(card==CardType.DISCOVER){
			cardBrand="DISCOVER";
		}else if(card==CardType.JCB){
			cardBrand="JCB";
		}else if(card==CardType.DANKORT){
			cardBrand="DANKORT";
		}else if(card==CardType.ELECTRON){
			cardBrand="ELECTRON";
		}else if(card==CardType.MAESTRO){
			cardBrand="MAESTRO";
		}else if(card==CardType.INTERPAYMENT){
			cardBrand="INTERPAYMENT";
		}else if(card==CardType.UNIONPAY){
			cardBrand="UNIONPAY";
		}else if(card==CardType.ELO){
			cardBrand="ELO";
		}
		else if(card==CardType.UNKNOWN) {
			cardBrand = "Desconhecido";
		}
		return cardBrand;
	}


}


