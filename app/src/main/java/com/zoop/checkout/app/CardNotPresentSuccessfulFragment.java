package com.zoop.checkout.app;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.checkout.app.Model.AssociateToken;
import com.zoop.checkout.app.Model.Buyer;
import com.zoop.checkout.app.Model.Card;
import com.zoop.checkout.app.Model.JsonTransactionNotPresent;

import java.math.BigDecimal;

public class CardNotPresentSuccessfulFragment extends Fragment {

	WalletActivity walletActivity;
	View v;



	public void setActivity(WalletActivity walletActivity){
		this.walletActivity=walletActivity;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 v = inflater.inflate(R.layout.payment_successful_fragment, container, false);
		return v;
	}
	@Override
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);


	}
	public void setStatusTransaction(Boolean successful){
        final TextView tvValue = (TextView) v.findViewById(R.id.editTextValueToCharge);
        Button btnSendReceipt=(Button)v.findViewById(R.id.buttonSendReceipt);
        Button btnNewTransaction=(Button)v.findViewById(R.id.buttonNewTransaction);
        ImageView iStatus=(ImageView)v.findViewById(R.id.imageViewChargeStatus);
        int iValue=AssociateToken.getInstance().getValue();
        Float value= Float.valueOf(iValue);
        value=value/100;
        tvValue.setText(Extras.getInstance().formatBigDecimalAsLocalString(new BigDecimal((value))));
        if(successful) {
            btnSendReceipt.setText("Comprovante");
            btnSendReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent receiptIntent = new Intent(getActivity(), ReceiptActivity.class);
                    Bundle b = new Bundle();
                    b.putString("transactionJSON", JsonTransactionNotPresent.getInstance().getJoTransaction());
                    receiptIntent.putExtras(b); //Put your id to your next Intent
                    getActivity().startActivity(receiptIntent);
                }
            });

            btnNewTransaction.setText("Nova Venda");
            btnNewTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*				Intent newCharge = new Intent(getActivity(), WalletActivity.class);
				newCharge.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				getActivity().startActivity(newCharge);*/
                    getActivity().finish();

                }
            });
            Buyer.getInstance().setInstance(null);
            Card.getInstance().setInstance(null);
            AssociateToken.getInstance().setInstance(null);
            iStatus.setImageResource(R.drawable.aproved);
        }else {
            btnSendReceipt.setText("Tentar Novamente");
            btnSendReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "try_again");
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_transaction_fail", bundle);
                 walletActivity.pager.setCurrentItem(0);
                }
            });
            btnNewTransaction.setText("Cancelar");
            btnNewTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "cancel");
                CheckoutApplication.getFirebaseAnalytics().logEvent("cnp_transaction_fail", bundle);
/*				Intent newCharge = new Intent(getActivity(), WalletActivity.class);
				newCharge.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				getActivity().startActivity(newCharge);*/
                    getActivity().finish();
                }
            });
            iStatus.setImageResource(R.drawable.refused);
            Toast.makeText(getActivity(),"Transação não autorizada",Toast.LENGTH_LONG).show();

        }



    }
}
