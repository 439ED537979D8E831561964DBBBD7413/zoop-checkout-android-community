package com.zoop.checkout.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Created by mainente on 11/05/15.
 */
public class TutorialPairPAXActivity extends ZCLMenuWithHomeButtonActivity {

    Button buttonPrev, buttonNext;
    ViewFlipper viewFlipper;

    Animation slide_in_left, slide_out_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorialpairpax);

        buttonPrev = (Button) findViewById(R.id.prev);
        buttonNext = (Button) findViewById(R.id.next);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);

        slide_in_left = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
        slide_out_right = AnimationUtils.loadAnimation(this, R.anim.push_left_out);

        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_right);

        buttonPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewFlipper.showPrevious();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {





                if (viewFlipper.getCurrentView().getTag().equals("4")) {
                    Intent intent = new Intent(TutorialPairPAXActivity.this, PairPinPadActivity.class);
                    startActivity(intent);


                }else {
                    viewFlipper.showNext();
                }

            }
        });
        ;
    }


    private void showPaginaAtual() {
        String teste= (String) viewFlipper.getCurrentView().getTag();

        Toast.makeText(this,
       teste,

        Toast.LENGTH_SHORT).show();

    }


}

