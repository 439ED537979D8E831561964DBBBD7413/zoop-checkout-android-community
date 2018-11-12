package com.zoop.checkout.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsCheckout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mainente on 05/02/16.
 */
public class DocumentsActivity extends LoadingActivity  {

    private RecyclerView mRecyclerView;
    private DocumentsCardAdapter mAdapter;
    private static final int SELECT_FILE=101;

    private LinearLayoutManager mLayoutManager;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    View formPlan;
    JSONObject joDocuments;
    CallDocuments callDocuments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        formPlan=(View)findViewById(R.id.formPlan);
        showProgress(true, formPlan, "Carregando Documentos");
        mRecyclerView = (RecyclerView) findViewById(R.id.cardListDocuments);
        callDocuments = new CallDocuments();
        callDocuments.execute((Void) null);
    }

    public class CallDocuments extends AsyncTask<Void, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject joUser = null;


            JSONObject joResponse = null;
            try {

                String sCheckoutPublicKey=APIParameters.getInstance().getStringParameter("sCheckoutPublicKey");
                String merchant=APIParameters.getInstance().getStringParameter("merchant");
                String sDocumentsUrl="https://api.zoopcheckout.com/v1/documents/"+merchant;

                String cookie= APIParameters.getInstance().getStringParameter("cookie");

              /*  try {


                    joDocuments= ZoopSession.getInstance().getSynchronousRESTRequest(sDocumentsUrl, sCheckoutPublicKey,"laravel_session=" + cookie);


                }catch (Exception e){

                    Extras.getInstance().signin(DocumentsActivity.this);
                    cookie= APIParameters.getInstance().getStringParameter("cookie");

                    joDocuments= ZoopSession.getInstance().getSynchronousRESTRequest(sDocumentsUrl, sCheckoutPublicKey, "laravel_session=" + cookie);




                }*/

                joDocuments= ZoopSessionsCheckout.getInstance().syncGetWithCookie(sDocumentsUrl, sCheckoutPublicKey, DocumentsActivity.this);

             /*   String suserUrl="https://api.zoopcheckout.com/v1/marketplace/all";



                JSONObject joDocuments2= ZoopSession.getInstance().getSynchronousRESTRequest(suserUrl, sCheckoutPublicKey,"laravel_session=" + cookie);
                ZLog.t(joDocuments2.toString());
*/

                return true;




            }catch (ZoopSessionHTTPJSONResponseException zhe) {
                ZLog.exception(300009, zhe);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return false;

        }



        protected void  onPostExecute(final Boolean result) {

            showProgress(false, formPlan, "Carregando Documentos");






            if(result) {



                mRecyclerView.setHasFixedSize(true);


                // use a linear layout manager


                mLayoutManager = new LinearLayoutManager(DocumentsActivity.this);
                mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

                //   mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, mRecyclerView, this));


                mRecyclerView.setLayoutManager(mLayoutManager);
                JSONObject jaDocuments=null;

                ;
                JSONArray jsonArray = new JSONArray();

                JSONObject joSeller=APIParametersCheckout.getInstance().getSeller();
                String type="";
                try {
                    type=joSeller.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                try {

                    JSONObject Documents= joDocuments.getJSONObject("content");
                    Iterator x = Documents.keys();
                    while (x.hasNext()){
                        String key = (String) x.next();

                        if (type.equals("individual")) {
                            if (!key.equals("cnpj")) {


                                JSONObject json = new JSONObject();
                                json.put("documents_type", key);

                                json.put("documents", Documents.get(key));


                                jsonArray.put(json);
                            }
                        }else{

                            JSONObject json = new JSONObject();
                            json.put("documents_type", key);

                            json.put("documents", Documents.get(key));


                            jsonArray.put(json);


                        }





                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // specify an adapter (see also next example)
                mAdapter = new DocumentsCardAdapter(DocumentsActivity.this,jsonArray , DocumentsActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        if (requestCode ==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode==SELECT_FILE) {

            mAdapter.onActivityResult(requestCode,resultCode,intent);



        }






    }


}
