package com.zoop.checkout.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;

import com.zoop.checkout.app.Model.AssociateToken;
import com.zoop.checkout.app.Model.Buyer;
import com.zoop.checkout.app.Model.Card;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by mainente on 25/01/17.
 */

public class WalletActivity extends ZCLMenuWithHomeButtonActivity {
    LockableViewPager pager;
    RegisterCardFragment registerCardFragment;
    CardNotPresentConfirmationFragment cardNotPresentConfirmationFragment;
    CardNotPresentSuccessfulFragment cardNotPresentSuccessfulFragment;



    public void onDestroy(){
        Buyer.getInstance().setInstance(null);
        Card.getInstance().setInstance(null);
        AssociateToken.getInstance().setInstance(null);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        FragmentPagerAdapter adapter = new ZoopPaymentAdapter(getFragmentManager());
        pager = (LockableViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        pager.setCurrentItem(0);
    }
    class ZoopPaymentAdapter extends FragmentPagerAdapter {
        //  	private Vector<OrderFragment> pagePaymentAdapters = new Vector<OrderFragment>();
        final FragmentManager fm;

        public ZoopPaymentAdapter(FragmentManager pfm) {
            super(pfm);
            fm = pfm;
        }


        @Override
        public Fragment getItem(int position) {
            if (0 == position) {
                ChargeFragment chargeFragment = new ChargeFragment();
                chargeFragment.setActivity(WalletActivity.this);
                return chargeFragment;
            } else if (1 == position) {
                registerCardFragment = new RegisterCardFragment();
                registerCardFragment.setActivity(WalletActivity.this);
                return registerCardFragment;
            } else if (2 == position) {
                RegisterBuyerFragment registerBuyerFragment = new RegisterBuyerFragment();
                registerBuyerFragment.setActivity(WalletActivity.this);
                return registerBuyerFragment;
            }else if (3 == position) {
                cardNotPresentConfirmationFragment = new CardNotPresentConfirmationFragment();
                cardNotPresentConfirmationFragment.setActivity(WalletActivity.this);
                return cardNotPresentConfirmationFragment;



            }else if (4 == position) {
                cardNotPresentSuccessfulFragment = new CardNotPresentSuccessfulFragment();
                cardNotPresentSuccessfulFragment.setActivity(WalletActivity.this);
                return cardNotPresentSuccessfulFragment;

            }
            return null;
        }

        @Override
        public String getPageTitle(int position) {


            return null;
        }

        @Override
        public int getCount() {

            return 5;

        }

        @Override
        public Parcelable saveState() {
            // Do Nothing
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == 0) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {

                registerCardFragment.setCardInfo(data);

            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results*/
    }




}