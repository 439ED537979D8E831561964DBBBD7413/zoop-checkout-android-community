package com.zoop.checkout.app;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.zoop.zoopandroidsdk.commons.APIParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;


/**
 * Created by mainente on 03/11/15.
 */
public class PlanCardAdapter extends RecyclerView.Adapter<PlanCardAdapter.PlanViewHolder> {

   JSONArray jotypePlan;
    Context c;
    JSONArray joFee_details;
    Activity aPlan;



    public PlanCardAdapter(Context c,JSONArray jotypePlanPlan,Activity aPlan) {
        this.jotypePlan=jotypePlanPlan;
        this.c=c;
        this.aPlan=aPlan;






    }

    @Override
    public int getItemCount() {
        return jotypePlan.length();

    }

    @Override
    public void onBindViewHolder(final PlanViewHolder planViewHolder, final int position) {



        try {


            planViewHolder.tInfoPlan.setText((jotypePlan.getJSONObject(position)).getString("description"));
            planViewHolder.tPlan.setText((jotypePlan.getJSONObject(position)).getString("name"));
            planViewHolder.tPlanid.setText((jotypePlan.getJSONObject(position)).getString("id"));






            joFee_details=jotypePlan.getJSONObject(position).getJSONArray("fee_details");

            JSONObject jObject = null;

           planViewHolder.cPlan.setAlpha((float) 0.3);
            //planViewHolder.iPlanActivate.setVisibility(View.GONE);


            if((jotypePlan.getJSONObject(position)).getString("id").equals(APIParameters.getInstance().getStringParameter("planSelected"))) {

                planViewHolder.cPlan.setAlpha((float) 1.0);
                //planViewHolder.iPlanActivate.setVisibility(View.VISIBLE);

            }









            String  percent_amount_debit = null;
            String  percent_amount_credit=null;
            BigDecimal taxaddDebit = new BigDecimal(0);
            BigDecimal taxaddCredit = new BigDecimal(0);


            for (int j = 0; j < joFee_details.length(); j++) {

                jObject = ((JSONObject) joFee_details.get(j));


                if(joFee_details.getJSONObject(j).getString("payment_type").equals("debit")) {

                    BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                    taxaddDebit=taxaddDebit.add(tax);




                    percent_amount_debit = String.valueOf(taxaddDebit);

                }else if((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getString("number_installments").equals("1"))){
                    BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                    taxaddCredit=taxaddCredit.add(tax);

                    percent_amount_credit= String.valueOf(taxaddCredit);

                }




            }







            planViewHolder.tTaxDebit.setText(percent_amount_debit+"%");



            planViewHolder.tTaxCredit.setText(percent_amount_credit + "%*");


            planViewHolder.bInfoPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String planInfo = "";


                    try {
                        joFee_details = jotypePlan.getJSONObject(position).getJSONArray("fee_details");
                        String percent_amount_debit_dialog="";
                        String percent_amount_credit_dialog="";
                        BigDecimal taxaddDebit = new BigDecimal(0);
                        BigDecimal taxaddCredit = new BigDecimal(0);


                        for (int j = 0; j < joFee_details.length(); j++) {

                            JSONObject jObject = ((JSONObject) joFee_details.get(j));


                            if(joFee_details.getJSONObject(j).getString("payment_type").equals("debit")) {

                                BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                                taxaddDebit=taxaddDebit.add(tax);




                                percent_amount_debit_dialog = String.valueOf(taxaddDebit);

                            }else if((joFee_details.getJSONObject(j).getString("payment_type").equals("credit")) && (joFee_details.getJSONObject(j).getString("number_installments").equals("1"))){
                                BigDecimal tax=new BigDecimal(joFee_details.getJSONObject(j).getDouble("percent_amount")).divide(new BigDecimal(100));
                                taxaddCredit=taxaddCredit.add(tax);

                                percent_amount_credit_dialog= String.valueOf(taxaddCredit);

                            }



                        }



                        if ((jotypePlan.getJSONObject(position)).getString("name").equals("Plano Pro")||(jotypePlan.getJSONObject(position)).getString("name").equals("Plano Top")) {
                            planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
                                    "4.99% no crédito + 2.39% por parcela;\n\n Ex: Transação parcelada em 3x:Taxa=4.99%+2.39%+2.39% \n" +
                                    "\n" + (jotypePlan.getJSONObject(position)).getString("description");
                        } else if ((jotypePlan.getJSONObject(position)).getString("name").equals("Plano Standard")) {
                            planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
                                    "4.39% no crédito de 2 a 6;\n4.69% no crédito de 7 a 12;\n\n" + (jotypePlan.getJSONObject(position)).getString("description");
                        }else {

                            planInfo = percent_amount_debit_dialog+ "% no débito;\n" + percent_amount_credit_dialog + "% no crédito à vista;\n" +
                                    "\n" + (jotypePlan.getJSONObject(position)).getString("description");




                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = aPlan.getFragmentManager();

                    DialogPlanDetails dialog = new DialogPlanDetails();
                    dialog.setPositionBasedOnView(v);
                    dialog.setPlanInfo(planInfo);


                    dialog.show(fragmentManager, "dialog");


                }
            });
            planViewHolder.cPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String percent_amount_debit=null;
                    String percent_amount_credit=null;

                    try {
                        joFee_details = jotypePlan.getJSONObject(position).getJSONArray("fee_details");

                        APIParameters.getInstance().putStringParameter("planSelected", (jotypePlan.getJSONObject(position)).getString("id"));


                        Spinner sgrid=(Spinner)aPlan.findViewById(R.id.sPlanSelected);

                        ArrayList<String> plans=new ArrayList<String>();
                        plans=Extras.getInstance().getPlansArray(joFee_details);




                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(aPlan, android.R.layout.simple_spinner_item, plans);
                        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        sgrid.setAdapter(spinnerArrayAdapter);




                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    notifyDataSetChanged();




                }
            });



        }catch (Exception e){

        }


    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.plancardnew, viewGroup, false);

        return new PlanViewHolder(itemView);
    }



    public static class PlanViewHolder extends RecyclerView.ViewHolder {

        private TextView tPlan;
        private Button bInfoPlan;
        private TextView  tTaxDebit;
        private TextView    tTaxCredit;
        private TextView tDebit;
        private TextView        tCredit;
        private TextView tInfoPlan;
        private RelativeLayout cPlan;
        private ImageView iPlanActivate;
        private TextView tPlanid;



        public PlanViewHolder(View v) {
            super(v);

            tPlan=(TextView)v.findViewById(R.id.tPlan);
            bInfoPlan=(Button)v.findViewById(R.id.bInfoPlan);
            tTaxDebit=(TextView)v.findViewById(R.id.tTaxDebit);
            tTaxCredit=(TextView)v.findViewById(R.id.tTaxCredt);
            tDebit=(TextView)v.findViewById(R.id.tDebit);
            tCredit=(TextView)v.findViewById(R.id.tCredit);
            tInfoPlan=(TextView)v.findViewById(R.id.tINfoPlan);
            cPlan=(RelativeLayout)v.findViewById(R.id.cPlan);
            iPlanActivate=(ImageView)v.findViewById(R.id.imgActivate);
            tPlanid=(TextView)v.findViewById(R.id.tPlanid);










        }


    }





}
