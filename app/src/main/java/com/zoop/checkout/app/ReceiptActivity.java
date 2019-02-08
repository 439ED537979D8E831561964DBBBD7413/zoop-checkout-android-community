package com.zoop.checkout.app;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zoop.checkout.app.API.ReceiptService;
import com.zoop.checkout.app.API.RetrofitInstance;
import com.zoop.zoopandroidsdk.api.Receipts;
import com.zoop.zoopandroidsdk.api.ZoopSignatureView;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;
import com.zoop.zoopandroidsdk.terminal.ReceiptDeliveryListener;
import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.APISettingsConstants;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopCommons;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiptActivity extends ZCLMenuWithHomeButtonActivity implements ReceiptDeliveryListener {

    JSONObject fee_details;
    JSONObject joPaymentMethod;
    String sCompleteAddress;
    JSONObject joInstallmentPlan;
    String paymentmethods;
    String AUTO;
    String CV;
    String InstallmentType;
    String sTerminalId = null;
    String sEC;
    String sFirst6Digits;
    String sFirst6DigitsPaddedWithAsterisks;
    String sSellerPhoneNumer;
    String CardDetails;
    Button buttonSendReceiptViaSMS;
    Button buttonSendReceiptViaEmail;
    String sSignatureData;
    String sStateAndCountry;
    String sTransactionJSON;

    View mProgressStatusView;
    View mReceiptView;

    JSONObject joZoopReceipt = null;
    JSONObject joTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadReceipt(getIntent());
    }

    public  void loadReceipt(Intent data){
        Bundle b = data.getExtras();
        APIParameters ap = APIParameters.getInstance();

        sTransactionJSON = b.getString("transactionJSON");

        if (ap.getBooleanParameter(APISettingsConstants.Receipt_UseGlobalPaymentsReceipt)) {
            setContentView(R.layout.activity_receipt_gp);
            mProgressStatusView = findViewById(R.id.progress_status_receipt_gp);
            mReceiptView = findViewById(R.id.layout_receipt_gp);
            showProgress(true);

            try {
                joTransaction = new JSONObject(sTransactionJSON);
                String receiptId = joTransaction.getString("sales_receipt");
                getReceipt(receiptId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//
//            mReceiptLoadingProgress = new AsyncTaskReceiptLoadingProgress();
//            mReceiptLoadingProgress.execute((Void) null);


        } else {
            setContentView(R.layout.activity_receipt);
        }

    }


    public void setUITransactionForVoidStatus(final JSONObject pjoTransactionResponse) {
        Button buttonVoidReceiptTransaction = (Button) findViewById(R.id.buttonVoidReceiptTransaction);
        if (Extras.checkIfTransactionCanBeCancelled(pjoTransactionResponse)) {
            buttonVoidReceiptTransaction.setVisibility(View.VISIBLE);
            buttonVoidReceiptTransaction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //Extras.showVoidTransactionConfirmationDialog(ReceiptActivity.this, pjoTransactionResponse);
                        //xx
                        Intent intent = new Intent(ReceiptActivity.this, VoidTransactionActivity.class);
                        Bundle b = new Bundle();
                        b.putString("joTransaction", pjoTransactionResponse.toString());
                        intent.putExtras(b); //Put your id to your next Intent
                        startActivityForResult(intent,1);
                    } catch (Exception e) {
                        ZLog.exception(300001, e);
                    }
                }
            });

        } else {
            buttonVoidReceiptTransaction.setVisibility(View.GONE);
        }

        TextView textViewTransactionCancelled = (TextView) findViewById(R.id.textViewTransactionVoided);
        Button btnPrint = (Button) findViewById(R.id.buttonPrintReceipt);
        LinearLayout layoutWithPassword = (LinearLayout) findViewById(R.id.layoutWithPassword);

        if (Extras.checkIfTransactionWasCancelled(pjoTransactionResponse)) {
            //textViewTransactionCancelled.setVisibility(View.VISIBLE);
            //btnPrint.setEnabled(false);

            //AutofitHelper.create(textViewTransactionCancelled);


        } else if (Extras.checkIfTransactionWasRejected(pjoTransactionResponse)) {
            /*textViewTransactionCancelled.setVisibility(View.VISIBLE);
            textViewTransactionCancelled.setText("TRANSAÇÃO NÃO APROVADA");
            layoutWithPassword.setVisibility(View.GONE);
            btnPrint.setEnabled(false);
            */

        } else if (Extras.checkIfTransactionWasFailed(pjoTransactionResponse)) {
            /*
            textViewTransactionCancelled.setVisibility(View.VISIBLE);
            textViewTransactionCancelled.setText("TRANSAÇÃO RECUSADA");
            layoutWithPassword.setVisibility(View.GONE);
            btnPrint.setEnabled(false);
            */
        } else if (Extras.checkIfTransactionWasCancel(pjoTransactionResponse)) {
            /*
            textViewTransactionCancelled.setVisibility(View.VISIBLE);
            textViewTransactionCancelled.setText("TRANSAÇÃO ESTORNADA");
            layoutWithPassword.setVisibility(View.GONE);
            btnPrint.setEnabled(false);
            */
        } else if (Extras.checkIfTransactionWasPending(pjoTransactionResponse)) {
            /*
            textViewTransactionCancelled.setVisibility(View.VISIBLE);
            textViewTransactionCancelled.setText("PENDENTE");
            layoutWithPassword.setVisibility(View.GONE);
            btnPrint.setEnabled(false);
            */

        } else {
            /*
            // ((TextView) findViewById(R.id.textViewTransactionVoided)).setVisibility(View.GONE);
            textViewTransactionCancelled.setText("APROVADO");
            //buttonSendReceiptViaEmail.setEnabled(true);
            //buttonSendReceiptViaSMS.setEnabled(true);
            textViewTransactionCancelled.setTextColor(Color.GREEN);
            */

        }

    }

    public void notifyVoidTransactionSuccessful() {
        setUITransactionForVoidStatus(joTransaction);
        // Use as flag and pointer to PaymentsListActivity if that was the callee
        PaymentsListActivity.invalidateTransactionsListResultSet();
    }

    @Override
    public void smsReceiptResult(final int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ZoopTerminalPayment.RESULT_FAILED_INVALID_PHONE_NUMBER == result) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_receipt_not_delivered_via_sms_invalid_number), Toast.LENGTH_LONG).show();
                } else if (ZoopTerminalPayment.RESULT_FAILED_SMS_ERROR == result) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_receipt_not_delivered_via_sms_unknown_reason), Toast.LENGTH_LONG).show();
                } else if (ZoopTerminalPayment.RESULT_OK_SMS_SENT == result) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_receipt_delivered_via_sms), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_receipt_sms_unknown_status), Toast.LENGTH_SHORT).show();
                    ZLog.t(300059);
                }
            }
        });
    }

    @Override
    public void emailReceiptResult(int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.send_receipt_delivered_via_email), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @SuppressLint("NewApi")
    public void getZoopPrintedReceiptVersion(View v) throws Exception {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sBTPrinterAddress = APIParameters.getInstance().getStringParameter("BTPrinterMACAddress");
                    OutputStream printerOutputStream =  Extras.getSingleOutputStreamForBTSPPDeviceMACAddress(sBTPrinterAddress);
                    StringBuffer sb = getZoopPrintedReceiptForCustomer(joTransaction, joZoopReceipt);
                    String Receipt = Normalizer.normalize(sb.toString(), Normalizer.Form.NFD);
                    Receipt = Receipt.replaceAll("[^\\p{ASCII}]", "");
                    printerOutputStream.write(Receipt.getBytes("Windows-1252"));
                    Extras.closeSingleBTSPPConnection();
                }
                catch (IOException e) {
                    Log.e("", "IOException");
                    e.printStackTrace();
                    Toast.makeText(ReceiptActivity.this, "Erro ao imprimir recibo.", Toast.LENGTH_LONG).show();
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        t.start();
    }

    public StringBuffer getZoopPrintedReceiptForMerchant(JSONObject joTransaction, JSONObject joReceipt) throws Exception {
        String sVia = " - VIA DO ESTABELECIMENTO";
        return getZoopPrintedReceiptVersion(sVia, joTransaction, joReceipt);
    }

    public StringBuffer getZoopPrintedReceiptForCustomer(JSONObject joTransaction, JSONObject joReceipt) throws Exception {
        String sVia = " - VIA DO CLIENTE";
        return getZoopPrintedReceiptVersion(sVia, joTransaction, joReceipt);
    }

    public StringBuffer getZoopPrintedReceiptVersion(String sHeader, JSONObject joTransaction, JSONObject joReceipt) throws Exception {

        StringBuffer sb = new StringBuffer();
        APIParameters ap = APIParameters.getInstance();

        int iPaperWidthInColumns = ap.getIntParameter("printerColumns",47);


        PrinterTextFormatter p = new PrinterTextFormatter(iPaperWidthInColumns, sb);

        try {
            if (!(Extras.checkIfTransactionWasCancel(joTransaction) || Extras.checkIfTransactionWasCancelled(joTransaction))) {

                p.addLineCenterAligned("ZOOP BRASIL");
                p.addLineCenterAligned("Comprovante de Transação");
                p.addLineCenterAligned((joPaymentMethod.getString("card_brand") + sHeader).toUpperCase());
                if (!((ap.getStringParameter("receiptCredenciadora") == null))) {
                    p.addLineCenterAligned(ap.getStringParameter("receiptCredenciadora").toUpperCase());
                }
            }

            p.addLineCenterAligned(ap.getStringParameter("sellerName"));
            p.printSeparatorLine();
            p.addLineLeftAndRightText("EC:", sEC.toUpperCase());

            p.addLineLeftAndRightText(sCompleteAddress.toUpperCase(), sStateAndCountry);

            // ToDo: Mostrar pro Thiago o truque com o sellerTaxIdType e o sellerTaxId
            p.addLineLeftAndRightText(ap.getStringParameter("sellerTaxIdType") + ":" + ap.getStringParameter("sellerTaxId"), "TEL:" + sSellerPhoneNumer);
            p.addLineCenterAligned(CardDetails);

            if (joTransaction.has("transaction_number")) {
                p.addLineLeftAndRightText("CV:" + CV, "AUTO:" + AUTO);
            }

            p.addLineLeftAligned("CARTAO:" + sFirst6DigitsPaddedWithAsterisks);

            Date transactionDate = Extras.getDateFromFullZoopAPITimestampString(joTransaction.getString("created_at"));
            String sTransactionDate = Extras.getFormattedDate(transactionDate, getResources().getString(R.string.date_format));
            String sTransactionTime = Extras.getFormattedDate(transactionDate, getResources().getString(R.string.time_format));
            p.addLineLeftAndRightText(sTransactionDate, sTransactionTime);

            if (joReceipt.has("application_cryptogram")) {
                p.addLineLeftAligned("ARQC:" + joReceipt.getString("application_cryptogram"));
            }

            // Note: According to Global Payments spec "Global Payments/GP_PDV_EspecificacaoFuncional_v.01.01_ed007.pdf"
            // the cardholder name is not present in the receipt.
            /*
            String cardholderName;
            if (joPaymentMethod.isNull("holder_name")) {
                cardholderName = getResources().getString(R.string.unidentified_holder_name);
            } else {
                cardholderName = joPaymentMethod.getString("holder_name");
            }
            p.addLineCenterAligned(cardholderName);
            */

            if (paymentmethods.equals(" parcelado")) {
                p.addLineCenterAligned(InstallmentType);
            }
            if ((((Extras.checkIfTransactionWasCancel(joTransaction))) || (Extras.checkIfTransactionWasCancelled(joTransaction)))) {
                // ToDo: Mostrar para o @mainente: Sempre usar funções alto nível que encapsulam a inteligencia sobre o tipo de separador a ser utilizado
                p.addLineLeftAndRightText("VALOR CANCELADO", "R$" + joTransaction.getString("amount").replace(".", ","));
            } else {
                p.addLineLeftAndRightText("VALOR APROVADO:", "R$" + joTransaction.getString("amount").replace(".", ","));
            }

            if ((Extras.checkIfTransactionWasCancel(joTransaction) || Extras.checkIfTransactionWasCancelled(joTransaction))) {
                p.addLineLeftAndRightText("CV:" + CV, "AUTO:" + AUTO);
            }

            if (joReceipt.has("application_criptogram")) {
                p.addLineLeftAligned("ARQC:" + joReceipt.getString("application_criptogram"));
            }

            if (joReceipt.has("application_identifier")) {
            }

            if (joReceipt.has("application_identifier")) {
                p.addLineLeftAligned("AID:" + joReceipt.getString("application_identifier"));
            }

            if (null != sTerminalId) {
                p.addLineLeftAligned("TERM:" + sTerminalId);
            }

            p.feedLine();
            p.feedLine();
            p.printSeparatorLine();
            p.feedLine();

            if (Extras.checkIfTransactionWasCancelled(joTransaction)) {
                p.addLineCenterAligned("TRANSACÂO CANCELADA");
            } else if (Extras.checkIfTransactionWasRejected(joTransaction)) {
                p.addLineCenterAligned("TRANSAÇÃO NÃO APROVADA");
            } else if (Extras.checkIfTransactionWasFailed(joTransaction)) {
                p.addLineCenterAligned("TRANSAÇÃO RECUSADA");
            } else if (Extras.checkIfTransactionWasCancel(joTransaction)) {
                p.addLineCenterAligned("TRANSAÇÃO ESTORNADA");
            } else if (Extras.checkIfTransactionWasPending(joTransaction)) {
                p.addLineCenterAligned("PENDENTE");
            }

            if (null != ap.getStringParameter("receiptSoftwareHouse")) {
                p.addLineCenterAligned(ap.getStringParameter("receiptSoftwareHouse").toUpperCase());
            }

            return p.getStringBuffer();
        } catch (Exception e) {

        }
        return null;
    }

    public void setDefaultReceiptFields(JSONObject joTransaction) throws Exception {
        final LinearLayout linearLayoutAID = (LinearLayout) findViewById(R.id.linearLayoutAID);
        final LinearLayout linearLayoutAUT = (LinearLayout) findViewById(R.id.linearLayoutAUT);
        final LinearLayout linearLayoutNSU = (LinearLayout) findViewById(R.id.linearLayoutNSU);

        final LinearLayout layoutCustomerSignature = (LinearLayout) findViewById(R.id.viewCustomerSignature);

        final ZoopSignatureView signatureViewCustomerSignature = (ZoopSignatureView) findViewById(R.id.SignatureViewCustomerSignature);

        joPaymentMethod = joTransaction.getJSONObject("payment_method");
        final LinearLayout layoutCustomerSoftwareHouse = (LinearLayout) findViewById(R.id.layoutSoftwareHouse);
        final LinearLayout layoutWithPassword = (LinearLayout) findViewById(R.id.layoutWithPassword);

        if (joTransaction.has("transaction_number")) {
            linearLayoutAUT.setVisibility(View.VISIBLE);
            linearLayoutNSU.setVisibility(View.VISIBLE);
            String transaction_number[] = joTransaction.getString("transaction_number").split("-");
            AUTO = transaction_number[0].replace("W", "");
            CV = transaction_number[1];

            ((EditText) findViewById(R.id.editTextTransactionAUT)).setText(AUTO);
            ((EditText) findViewById(R.id.editTextTransactionNSU)).setText(CV);


        } else {
            linearLayoutAUT.setVisibility(View.GONE);
        }

        signatureViewCustomerSignature.setEnabled(false);
        if (!((APIParameters.getInstance().getStringParameter("receiptCredenciadora") == null))) {
            ((TextView) findViewById(R.id.txtcred)).setText(APIParameters.getInstance().getStringParameter("receiptCredenciadora"));
            ((TextView) findViewById(R.id.txtcred)).setVisibility(View.VISIBLE);
        }

        if (!((APIParameters.getInstance().getStringParameter("receiptSoftwareHouse") == null))) {
            layoutCustomerSoftwareHouse.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.SoftwareHouse)).setText(APIParameters.getInstance().getStringParameter("receiptSoftwareHouse"));
        }

        layoutCustomerSignature.setVisibility(View.GONE);

        if (joTransaction.has("sales_receipt")) {
            try {
                JSONObject joReceipt = fetchZoopReceipt(joTransaction.getString("sales_receipt"));
                if (joReceipt.has("signature")) {
                    if (!joReceipt.isNull("signature")) {
                        sSignatureData = joReceipt.getString("signature");
                        /*
                        signatureViewCustomerSignature.setViewSignatureData(sSignatureData);
                        signatureViewCustomerSignature.invalidate();
                        layoutCustomerSignature.setVisibility(View.VISIBLE);
                        layoutWithPassword.setVisibility(View.GONE);
                         */
                    }
                }

                /*
                String sURL = com.zoop.commons.Extras.getURL(APISettingsConstants.ZoopURL_RetrieveReceiptEndpoint);
                APIParameters ap = APIParameters.getInstance();
                sURL = sURL.replace("[MARKETPLACE_ID]", ap.getStringParameter("marketplaceId"));
                sURL = sURL.replace("[RECEIPT_ID]", joTransaction.getString("sales_receipt"));
                ZoopSession.getInstance().getAsyncRESTRequest(sURL, ap.getStringParameter("publishableKey"), new ZoopSession.HTTPJSONReceiver() {
                    @Override
                    public void requestOK(int requestId, JSONObject pjoReceipt) {
                        joReceipt = pjoReceipt;

                        try {
                            if (joReceipt.has("signature")) {
                                if (!joReceipt.isNull("signature")) {
                                    String sSignatureData = joReceipt.getString("signature");
                                    signatureViewCustomerSignature.setViewSignatureData(sSignatureData);
                                    signatureViewCustomerSignature.invalidate();
                                    layoutCustomerSignature.setVisibility(View.VISIBLE);
                                    layoutWithPassword.setVisibility(View.GONE);
                                    Signatureparam = sSignatureData;
                                }
                            }

                            sFirst6Digits = joReceipt.getJSONObject("card").getString("first6_digits");
                            if (null != sFirst6Digits) {
                                // ToDo: Add PAN size below
                                sFirst6DigitsPaddedWithAsterisks = com.zoop.commons.Extras.rPadCharToTotalLength(sFirst6Digits, '*', 16);
                            } else {
                                sFirst6DigitsPaddedWithAsterisks = "******";
                            }

                            EditText editTextMaskedCard = ((EditText) findViewById(R.id.editTextfirst4_digits));
                            ((EditText) findViewById(R.id.editTextfirst4_digits)).setVisibility(View.VISIBLE);
                            editTextMaskedCard.setText(sFirst6DigitsPaddedWithAsterisks);


                            if (joReceipt.has("terminal_id")) {
                                sTerminalId = joReceipt.getString("terminal_id");
                                ((EditText) findViewById(R.id.editTextTerm)).setText(sTerminalId);
                            } else {
                                ((EditText) findViewById(R.id.editTextTerm)).setVisibility(View.GONE);
                            }

                            LinearLayout linearLayoutARQC = (LinearLayout) findViewById(R.id.linearLayoutARQC);
                            if (joReceipt.has("application_cryptogram")) {
                                linearLayoutARQC.setVisibility(View.VISIBLE);
                                ((EditText) findViewById(R.id.editTextTransactionARQC)).setText(joReceipt.getString("application_cryptogram"));
                            } else {
                                linearLayoutARQC.setVisibility(View.GONE);
                            }

                            if (joReceipt.has("application_identifier")) {
                                linearLayoutAID.setVisibility(View.VISIBLE);
                                ((EditText) findViewById(R.id.editTextTransactionAID)).setText(joReceipt.getString("application_identifier"));
                            } else {
                                linearLayoutAID.setVisibility(View.GONE);
                            }

                        } catch (Exception e2) {
                            ZLog.exception(677432, e2);
                        }
                    }

                    @Override
                    public void requestFailed(int statusCode, Throwable e, JSONObject jo) {
                        ZLog.exception(677443, e);
                    }
                });
            */
            } catch (Exception e) {
                ZLog.exception(677431, e);
            }

        }

        /**
         * Display de transaction id in the receipt editText/ pane
         */

        /**
         * Display the total charged and confirmed by the Zoop Payments network
         */
        ((EditText) findViewById(R.id.editTextTransactionTotal)).setText("R$ " + joTransaction.getString("amount").replace(".", ","));


        InstallmentType = "";

        // ToDo: What to show when payment terminal is CNP/ Zoop Wallet? How to present to the user?
        // In this case, the else would be called. CNP don't have a receipt

        sEC = APIParametersCheckout.getInstance().getSeller().getString("id");
        sEC = sEC.substring(0, 21).toUpperCase();

        ((EditText) findViewById(R.id.editTextEC)).setText(sEC);

        if (APIParametersCheckout.getInstance().getSeller().has("phone_number")) {
            sSellerPhoneNumer = APIParametersCheckout.getInstance().getSeller().getString("phone_number");
        } else {
            sSellerPhoneNumer = "";
        }
        ((EditText) findViewById(R.id.editTextPhone)).setText(sSellerPhoneNumer);

        sCompleteAddress = "";
        sStateAndCountry = "";
        if (APIParametersCheckout.getInstance().getSeller().has("address")) {
            JSONObject address = APIParametersCheckout.getInstance().getSeller().getJSONObject("address");
            if (address.has("line1")) {
                if (!address.isNull(("line1"))) {
                    sCompleteAddress = address.getString("line1") + " ";
                }
            }

            if (address.has("line2")) {
                if (!address.isNull("line2")) {
                    sCompleteAddress = sCompleteAddress + address.getString("line2") + " ";
                }
            }

            if (address.has("city")) {
                if (!address.isNull("city")) {
                    sCompleteAddress = sCompleteAddress + address.getString("city") + " ";
                }
            }

            if (address.has("state")) {
                if (!address.isNull("state")) {
                    sStateAndCountry = sStateAndCountry + address.getString("state") + " ";
                }
            }
            if (address.has("country_code")) {
                if (!address.isNull("country_code")) {
                    sStateAndCountry = sStateAndCountry + address.getString("country_code");
                }
            }
        }
        ((EditText) findViewById(R.id.editTextEndereco)).setText(sCompleteAddress);


        if (joTransaction.isNull("installment_plan")) {
            paymentmethods = " a vista";
        } else {
            paymentmethods = " parcelado";
            joInstallmentPlan = joTransaction.getJSONObject("installment_plan");
            String Mode = joInstallmentPlan.getString("mode");
            if (Mode.equals("interest_free")) {
                InstallmentType = "Loja em " + joInstallmentPlan.getString("number_installments") + " parcelas";
            } else {
                InstallmentType = "Cliente em " +   joInstallmentPlan.getString("number_installments") + " parcelas";
            }
        }

        LinearLayout linearLayoutInstallment = (LinearLayout) findViewById(R.id.LinearTypeinstallment);

        if (paymentmethods.equals(" parcelado")) {
            linearLayoutInstallment.setVisibility(View.VISIBLE);
            ((EditText) findViewById(R.id.editTextTypeinstallment)).setText((InstallmentType).toUpperCase());

        }

        String paymentTypeLocalLanguage = Extras.getPaymentTypeInLocalLanguage(joTransaction.getString("payment_type"));
        CardDetails = (joPaymentMethod.getString("card_brand") + " - Venda " + paymentTypeLocalLanguage + paymentmethods).toUpperCase();
        ((TextView) findViewById(R.id.editTextTransactionCardDetails)).setText(CardDetails);
    }

    public void setGlobalPaymentsReceiptField(JSONObject joReceipt) {
        /*
        "original_receipt": {
        "sales_receipt_merchant": "C@@   MASTERCARD - Via Estabelecimento   @@TAXI- MARCELO MONTEIRO R@AV  CARNAUBEIRA 00080             RJBR@CNPJ: 78158818749          21995302656@EC:000022324                          @Credit@VENDA CREDITO A VISTA@************3010   @02/07/15                         09:07@VALOR APROVADO: R$ 40,00@@CV:000482184009            AUTO:003776@ @@TERM:GT0000C6       @@  TRANSACAO AUTORIZADA MEDIANTE SENHA @",
        "sales_receipt_cardholder": "C@@       MASTERCARD - Via Cliente       @@TAXI- MARCELO MONTEIRO R@AV  CARNAUBEIRA 00080             RJBR@CNPJ: 78158818749          21995302656@EC:000022324                          @Credit@VENDA CREDITO A VISTA@************3010   @02/07/15                         09:07@VALOR APROVADO: R$ 40,00@@CV:000482184009            AUTO:003776@ @@TERM:GT0000C6       @"
        },
         */
        try {
            String sReceipt = joReceipt.getJSONObject("original_receipt").getString("sales_receipt_merchant");
            ZLog.t("RECEIPT=" + sReceipt);
        } catch (Exception e) {
            ZLog.exception(300061, e);
        }
    }

    public JSONObject fetchZoopReceipt(String sReceiptId) throws Exception {
        String sURL = com.zoop.zoopandroidsdk.commons.Extras.getURL(APISettingsConstants.ZoopURL_RetrieveReceiptEndpoint);
        APIParameters ap = APIParameters.getInstance();


        String publishableKey=APIParameters.getInstance().getStringParameter("publishableKey");
        String marketplaceId=APIParameters.getInstance().getStringParameter("marketplaceId");
        String sellerId=APIParameters.getInstance().getStringParameter("sellerId");


        sURL = sURL.replace("[MARKETPLACE_ID]", marketplaceId);
        sURL = sURL.replace("[RECEIPT_ID]", sReceiptId);
        return ZoopSessionsPayments.getInstance().syncGet(sURL,  publishableKey, this);

    }

    public void getReceipt (String receiptId) {
        String sURL = com.zoop.zoopandroidsdk.commons.Extras.getURL(APISettingsConstants.ZoopURL_RetrieveReceiptEndpoint);

        String publishableKey=APIParameters.getInstance().getStringParameter("publishableKey");
        String marketplaceId=APIParameters.getInstance().getStringParameter("marketplaceId");
        String sellerId=APIParameters.getInstance().getStringParameter("sellerId");


        sURL = sURL.replace("[MARKETPLACE_ID]", marketplaceId);
        sURL = sURL.replace("[RECEIPT_ID]", receiptId);

        ReceiptService receiptService = RetrofitInstance.getRetrofitInstance().create(ReceiptService.class);

        Call<Object> receiptCall = receiptService.getReceipt("Basic " + Base64.encodeToString(String.format("%s:", publishableKey).getBytes(), Base64.NO_WRAP), sURL);

        receiptCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                try {
                    joZoopReceipt = new JSONObject(new Gson().toJson(response.body()));
                    Button buttonNewTransaction = (Button) findViewById(R.id.buttonNewTransaction);
                    buttonNewTransaction.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_new_transaction", null);
                            Intent newCharge = new Intent(ReceiptActivity.this, ChargeActivity.class);
                            newCharge.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(newCharge);	                            }
                    });

                    setUITransactionForVoidStatus(joTransaction);

                    buttonSendReceiptViaEmail = (Button) findViewById(R.id.buttonSendReceiptViaEmail);
                    buttonSendReceiptViaEmail.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Bundle emailBundle = new Bundle();
                            //buttonSendReceiptViaEmail.setBackgroundColor(Color.BLUE);

                            final AlertDialog.Builder alert = new AlertDialog.Builder(ReceiptActivity.this);
                            alert.setTitle(getResources().getString(R.string.dialog_send_receipt_via_email_title));
                            alert.setMessage(getResources().getString(R.string.dialog_send_receipt_via_email_message));

                            // Set an EditText view to get user input
                            final EditText input = new EditText(getApplicationContext());
                            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                            input.setTextColor(getResources().getColor(R.color.zcolor_regular_button_darker));
                            alert.setView(input);


                            alert.setPositiveButton(getResources().getString(R.string.send_receipt_confirm_action), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {

                                        String email=input.getText().toString();

                                        if(!email.equals("")) {

                                            if(email.contains("@")) {


                                                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                im.hideSoftInputFromWindow(input.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                                                emailBundle.putString("status", "success");
                                                CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_send_email", emailBundle);
                                                Receipts.sendEmailReceipt(joTransaction, input.getText().toString(), ReceiptActivity.this);
                                            }else {

                                                Toast.makeText(ReceiptActivity.this,"Email inválido",Toast.LENGTH_LONG).show();


                                            }
                                        }else{

                                            Toast.makeText(ReceiptActivity.this,"Email não informado",Toast.LENGTH_LONG).show();
                                        }


                                    } catch (Exception e) {

                                    }

                                }
                            });

                            alert.setNegativeButton(getResources().getString(R.string.send_receipt_cancel_action), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    emailBundle.putString("status", "canceled");
                                    CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_send_email", emailBundle);
                                    // Canceled.
                                }
                            });

                            final AlertDialog dialogWindow = alert.create();
                            dialogWindow.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            alert.show();
                        }
                    });

                    buttonSendReceiptViaSMS = (Button) findViewById(R.id.buttonSendReceiptViaSMS);
                    buttonSendReceiptViaSMS.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Bundle smsBundle = new Bundle();
                            AlertDialog.Builder alert = new AlertDialog.Builder(ReceiptActivity.this);

                            alert.setTitle(getResources().getString(R.string.dialog_send_receipt_via_sms_title));
                            alert.setMessage(getResources().getString(R.string.dialog_send_receipt_via_sms_message));

                            // Set an EditText view to get user input

                            TextWatcher telMask;



                            final EditText input = new EditText(getApplicationContext());
                            input.setInputType(InputType.TYPE_CLASS_PHONE);
                            telMask= Mask.insert("(##)####-#####", input);
                            input.addTextChangedListener(telMask);
/*
                            input.setText(APIParameters.getInstance().getStringParameter("phoneddd"));
*/

                            input.setTextColor(getResources().getColor(R.color.zcolor_regular_button_darker));
                            try {
                                JSONObject Seller = APIParametersCheckout.getInstance().getSeller();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                /*
                try {
                    //String CountryCode="+55";
                    String DDD=Seller.getString("phone_number").substring(0, 2);
                    String codecity=CountryCode+DDD;

                    input.setText(codecity);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */

                            alert.setView(input);

                            alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {
//                                NiftyNotificationView.build(ReceiptActivity.this, "Teste", Effects.slideIn, R.id.frameLayoutMaster).show();

                                        Extras.hideKeyboard(ReceiptActivity.this);
                                        //String sPhoneNumber = Extras.getValidZoopSMSGatewayNumberFromUserInputPhoneNumber(input.getText().toString(), "55");

                                        String sPhoneNumber = "55"+Mask.unmask(input.getText().toString());

                                        if(sPhoneNumber.length()>=10){

                                            Receipts.sendSMSReceipt(joTransaction, sPhoneNumber, ReceiptActivity.this);
                                            smsBundle.putString("status", "success");
                                            CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_send_sms", smsBundle);


                                        }else {
                                            Toast.makeText(ReceiptActivity.this,"Número de celular inválido, verifique se colocou o ddd.",Toast.LENGTH_LONG).show();
                                        }



                                    } catch (Exception e) {

                                    }

                                }
                            });

                            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    smsBundle.putString("status", "canceled");
                                    CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_send_sms", smsBundle);
                                    // Canceled.
                                }
                            });

                            alert.show();
                        }
                    });


                    ((Button) findViewById(R.id.buttonPrintReceiptCardholderCopy)).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_buyer", null);
                                StringBuffer sbReceipt;
                                String sReceiptText;
                                if (joZoopReceipt.has("original_receipt")) {
                                    sbReceipt = new StringBuffer();
                                    sbReceipt.append(joZoopReceipt.getJSONObject("original_receipt").getString("sales_receipt_cardholder"));
                                    sReceiptText = formatOriginalReceiptFromGPRawReceipt(sbReceipt.toString());
                                }
                                else {
                                    sbReceipt = getZoopPrintedReceiptForMerchant(joTransaction, joZoopReceipt);
                                    sReceiptText = sbReceipt.toString();
                                }


                                Intent intent = new Intent(ReceiptActivity.this, ReceiptPrint.class);
                                intent.putExtra("buffer", sReceiptText);
                                intent.putExtra("bufferprinter", sReceiptText);
                                intent.putExtra("withpassword", false);

                                startActivity(intent);
                            } catch (Exception e) {
                                ZLog.exception(300052, e);
                            }
                            //StringBuffer sbprinter= PrintReceiptFormat(" - VIA DO CLIENTE");
                        }
                    });

                    ((Button) findViewById(R.id.buttonPrintReceiptMerchantCopy)).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //" - VIA DO ESTABELECIMENTO"
                            try {
                                CheckoutApplication.getFirebaseAnalytics().logEvent("receipt_seller", null);
                                StringBuffer sbReceipt;
                                String sReceiptText;

                                if (joZoopReceipt.has("original_receipt")) {
                                    sbReceipt = new StringBuffer();
                                    sbReceipt.append(joZoopReceipt.getJSONObject("original_receipt").getString("sales_receipt_merchant"));
                                    sReceiptText = formatOriginalReceiptFromGPRawReceipt(sbReceipt.toString());
                                }
                                else {
                                    sbReceipt = getZoopPrintedReceiptForMerchant(joTransaction, joZoopReceipt);
                                    sReceiptText = sbReceipt.toString();
                                }

                                Intent intent = new Intent(ReceiptActivity.this, ReceiptPrint.class);
                                intent.putExtra("buffer", sReceiptText);
                                intent.putExtra("bufferprinter", sReceiptText);

                                intent.putExtra("Signatureparam", sSignatureData);
                                if (sSignatureData == null) {
                                    intent.putExtra("withpassword", true);
                                } else {
                                    intent.putExtra("withpassword", false);
                                }
                                startActivity(intent);
                            } catch (Exception e) {
                                ZLog.exception(300053, e);
                            }
                        }
                    });
                    ((View) findViewById(R.id.bannerZoop)).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ZLog.t(300039);
                        }
                    });

                    if (APIParameters.getInstance().getBooleanParameter(APISettingsConstants.Receipt_ShowReceiptLogoOnGPReceipt)) {
                        findViewById(R.id.linearLayoutReceiptLogo).setVisibility(View.VISIBLE);
                    }
                    else {
                        findViewById(R.id.linearLayoutReceiptLogo).setVisibility(View.GONE);
                    }


                    AutoResizeTextView autoResizeTextViewReceipt = (AutoResizeTextView) findViewById(R.id.autoResizeTextViewPrintReceipt);
                    String sReceiptText = joZoopReceipt.getJSONObject("original_receipt").getString("sales_receipt_merchant");
                    sReceiptText = formatOriginalReceiptFromGPRawReceipt(sReceiptText);
                    autoResizeTextViewReceipt.setText(sReceiptText);

                    sSignatureData = ZoopCommons.getCardholderSignature(joTransaction.getString("id"));
                }
                catch (JSONException e) {
                    ZLog.exception(300063, e);
                }

                showProgress(false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                showProgress(false);
            }
        });
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger( android.R.integer.config_shortAnimTime);

            mProgressStatusView.setVisibility(View.VISIBLE);
            mProgressStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    mProgressStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mReceiptView.setVisibility(show ? View.GONE : View.VISIBLE);
            /*mReceiptView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mReceiptView.setVisibility(show ? View.GONE : View.VISIBLE);
                            }
            });
            */
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mReceiptView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private String formatOriginalReceiptFromGPRawReceipt(String sRawReceipt) {
        sRawReceipt = sRawReceipt.replace('@', '\n');
        sRawReceipt = sRawReceipt.substring(2);
        return sRawReceipt;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1 == requestCode) {
            if (resultCode == RESULT_OK) {
                loadReceipt(data);

            }
        }
    }
}

