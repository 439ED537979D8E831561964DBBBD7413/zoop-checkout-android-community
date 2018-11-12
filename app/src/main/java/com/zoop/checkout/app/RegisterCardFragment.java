package com.zoop.checkout.app;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.zoop.checkout.app.Model.AssociateToken;
import com.zoop.checkout.app.Model.Card;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopCardUtils;

import org.json.JSONObject;

public class RegisterCardFragment extends Fragment {
    WalletActivity walletActivity;
    View v;
    JSONObject joCard=null;

    public void setActivity(WalletActivity walletActivity) {
        this.walletActivity = walletActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_card_register, container, false);

        return v;
    }

    EditText eCard_holder;
    EditText eCard_number;
    EditText eExpiration_date;
    EditText eCVV;
    EditText eValue;
    private TextWatcher mDate;
    String cardbrand="";
    Spinner sInstallments;

    Bundle registerCardBundle = new Bundle();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eCard_holder = (EditText) v.findViewById(R.id.card_holder);
        eCard_number = (EditText) v.findViewById(R.id.card_number);
        eCard_number.addTextChangedListener(new FourDigitCardFormatWatcher());

        eExpiration_date = (EditText) v.findViewById(R.id.expiration_date);
        mDate = Mask.insert("##/##", eExpiration_date);
        eExpiration_date.addTextChangedListener(mDate);
        eCVV = (EditText) v.findViewById(R.id.CVV);
        eValue= (EditText) v.findViewById(R.id.buyer_value);
        sInstallments=(Spinner) v.findViewById(R.id.installments);

        Button btnNext = (Button) v.findViewById(R.id.btnNext);
        Button btnPrevious = (Button) v.findViewById(R.id.btnPrevious);
        try {

            eCard_number.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                   setCardBrand(eCard_number.getText().toString().replace(" ",""));
                    }
                }
            );

           if(Card.getInstance().getNumcard()!=null) {
              /*  eExpiration_date.setText(Card.getInstance().getExpirationMonth() + Card.getInstance().getExpirationMYear());
                eCard_holder.setText(Card.getInstance().getHolder_name());
               eCard_number.setText(sCardNumber);

               eCVV.setText(Card.getInstance().getCVCcard());*/
                String sCardNumber = Card.getInstance().getNumcard();
                setCardBrand(sCardNumber);

            }



        }catch (Exception e){
            ZLog.t(e.toString());

        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validation = true;
                String msgValidation = "";
                if (TextUtils.isEmpty(eCard_holder.getText().toString())) {
                    validation = false;
                    msgValidation = "- Nome do titular do cartão não informado";
                } else {
                    Card.getInstance().setHolder_name(eCard_holder.getText().toString());
                }
                if (TextUtils.isEmpty(eCard_number.getText().toString())) {
                    validation = false;
                    msgValidation += "\n- Número do cartão não informado";
                } else {
                    Boolean validPan=false;
                    try {
                        validPan=joCard.getBoolean("validPAN");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ((cardbrand.equals(""))||!validPan){
                        validation = false;
                        msgValidation += "\n- Número do cartão inválido";
                    } else {
                        Card.getInstance().setNumcard(eCard_number.getText().toString().replace(" ",""));
                    }
                }


                if (TextUtils.isEmpty(eExpiration_date.getText().toString())) {
                    validation = false;
                    msgValidation += " \n- data de expiração não informado";
                } else {
                    String[] exp_date=eExpiration_date.getText().toString().split("/");
                    if(exp_date.length>0) {
                        Card.getInstance().setExpirationMYear(Integer.parseInt(exp_date[1]));
                        Card.getInstance().setExpirationMonth(Integer.parseInt(exp_date[0]));
                    }else {
                        validation = false;
                        msgValidation += " \n- data de expiração inválida";

                    }

                }
                if (TextUtils.isEmpty(eCVV.getText().toString())) {
                    validation = false;
                    msgValidation += " \n- CVV não informado";
                } else {
                    Card.getInstance().setCVCcard((eCVV.getText().toString()));
                }

                int installments=sInstallments.getSelectedItemPosition()+1;
                AssociateToken.getInstance().setInstallmentOptions(installments);
                AssociateToken.getInstance().setInstallmentOptionstext(sInstallments.getSelectedItem().toString());

                if (validation) {
                    registerCardBundle.putString("status", "success");
                    CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_cardnumber", registerCardBundle);
                   Extras.hideKeyboard(getActivity());
                    walletActivity.cardNotPresentConfirmationFragment.getConfirmation();
                    walletActivity.pager.setCurrentItem(2);

                } else {
                    registerCardBundle.putString("status", "failed");
                    CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_cardnumber", registerCardBundle);
                    Toast.makeText(getActivity(), msgValidation, Toast.LENGTH_LONG).show();
                }


            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCardBundle.putString("status", "canceled");
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_cardnumber", registerCardBundle);
                walletActivity.pager.setCurrentItem(0);
            }
        });

      /*  v.findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent scanIntent = new Intent(walletActivity, CardIOActivity.class);
                        scanIntent.putExtra(CardIOActivity.EXTRA_NO_CAMERA, false)
                                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
                                .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
                                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true)
                                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
                                .putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false)
                                .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true)
                                .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
                                .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, false)
                                .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, false)
                                .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
                                .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false)
                                .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.GREEN)
                                .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, false)
                                .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, false)
                                .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

                        walletActivity.startActivityForResult(scanIntent, 0);
                    }
                });*/
    }

   /* public void setCardInfo(Intent data) {
        CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
        if (scanResult.isExpiryValid()) {
            String expiryMonth=String.valueOf(scanResult.expiryMonth);
            if(expiryMonth.length()<=1){
                expiryMonth="0"+expiryMonth;
            }
            eExpiration_date.setText(expiryMonth+String.valueOf(scanResult.expiryYear-2000));
        }
        if (scanResult.cvv != null) {
            eCVV.setText(scanResult.cvv);
        } else {

            Toast.makeText(walletActivity, "Escaneie o verso do cartão para leitura do CVV", Toast.LENGTH_LONG).show();
        }

        if (scanResult.cardholderName != null) {
            eCard_holder.setText(scanResult.cardholderName);
        }
        if (scanResult.cardNumber != null) {
            eCard_number.setText(scanResult.cardNumber);
            try {
                setCardBrand(scanResult.cardNumber);

            }catch (Exception e){

            }

        }

        Bitmap card = CardIOActivity.getCapturedCardImage(data);
    }*/

    public void setCardBrand(String number){
        ZoopCardUtils cardUtils=new ZoopCardUtils();
        cardbrand="";
        String lCardBrand="";
        try {
            joCard= cardUtils.getCardProductForCardnumberAndKeyedEntry(number);
            lCardBrand=joCard.getString("label");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(lCardBrand.equalsIgnoreCase("Mastercard")) {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_master);
            imgMaster.setImageResource(R.drawable.master);
            imgMaster.setAlpha(1f);
            cardbrand = "MasterCard";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.master);
        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_master);
            imgMaster.setImageResource(R.drawable.masterpb);
            imgMaster.setAlpha((float) 0.3);
        }
        if(lCardBrand.equalsIgnoreCase("Visa")){
            ImageView imgVisa=(ImageView) v.findViewById(R.id.brand_visa);
            imgVisa.setImageResource(R.drawable.visa);
            imgVisa.setAlpha(1f);
            cardbrand="Visa";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.visa);
        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_visa);
            imgMaster.setImageResource(R.drawable.visapb);
            imgMaster.setAlpha((float) 0.3);
        }
        if(lCardBrand.equalsIgnoreCase("American Express")){
            ImageView imgAmerican=(ImageView) v.findViewById(R.id.brand_amex);
            imgAmerican.setImageResource(R.drawable.amex);
            imgAmerican.setAlpha(1f);
            cardbrand="America Express";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.amex);
        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_amex);
            imgMaster.setImageResource(R.drawable.amexpb);
            imgMaster.setAlpha((float) 0.3);
        }

        if(lCardBrand.equalsIgnoreCase("Diners")) {
            ImageView imgDiners = (ImageView) v.findViewById(R.id.brand_dinner);
            imgDiners.setImageResource(R.drawable.dinner);
            imgDiners.setAlpha(1f);
            cardbrand = "Diners";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.dinner);
        }
            else {
                ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_dinner);
                imgMaster.setImageResource(R.drawable.dinnerpb);
                imgMaster.setAlpha((float) 0.3);
            }


            if(lCardBrand.equalsIgnoreCase("Discover")){
            ImageView imgDiscover=(ImageView) v.findViewById(R.id.brand_discover);
            imgDiscover.setImageResource(R.drawable.discover);
            imgDiscover.setAlpha(1f);
            cardbrand="Discover";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.discover);

        } else {
           ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_discover);
           imgMaster.setImageResource(R.drawable.discoverpb);
           imgMaster.setAlpha((float) 0.3);
       }
        if(lCardBrand.equalsIgnoreCase("JCB")) {
            ImageView imgJCB = (ImageView) v.findViewById(R.id.brand_jcb);
            imgJCB.setImageResource(R.drawable.jcb);
            imgJCB.setAlpha(1f);
            cardbrand = "JCB";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.jcb);
        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_jcb);
            imgMaster.setImageResource(R.drawable.jcbpb);
            imgMaster.setAlpha((float) 0.3);
        }

        if(lCardBrand.equalsIgnoreCase("Aura")){
            ImageView imgAura=(ImageView) v.findViewById(R.id.brand_aura);
            imgAura.setImageResource(R.drawable.aura);
            imgAura.setAlpha(1f);
            cardbrand="Aura";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.aura);

        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_aura);
            imgMaster.setImageResource(R.drawable.aurapb);
            imgMaster.setAlpha((float) 0.3);
        }
        if(lCardBrand.equalsIgnoreCase("Elo")){
            ImageView imgElo=(ImageView) v.findViewById(R.id.brand_elo);
            imgElo.setImageResource(R.drawable.elo);
            imgElo.setAlpha(1f);
            cardbrand="ELO";
            Card.getInstance().setCardBrand(cardbrand);
            Card.getInstance().setImgCard_brand(R.drawable.elo);

        }else {
            ImageView imgMaster = (ImageView) v.findViewById(R.id.brand_elo);
            imgMaster.setImageResource(R.drawable.elopb);
            imgMaster.setAlpha((float) 0.3);
        }



    }
    public static class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }

}
