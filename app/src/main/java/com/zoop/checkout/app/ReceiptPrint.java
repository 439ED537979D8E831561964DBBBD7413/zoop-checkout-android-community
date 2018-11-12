package com.zoop.checkout.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.api.ZoopSignatureView;
import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.printer.ZoopPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;


public class ReceiptPrint extends ZCLMenuWithHomeButtonActivity {

    String sbprinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_print);
        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        String receipt=params.getString("buffer");
        sbprinter=params.getString("bufferprinter");
        AutoResizeTextView textreceipt=(AutoResizeTextView)findViewById(R.id.print);

        textreceipt.setText(receipt);
      //  final LinearLayout layoutCustomerSignature = (LinearLayout) findViewById(R.id.viewCustomerSignature);
        final ZoopSignatureView signatureViewCustomerSignature = (ZoopSignatureView) findViewById(R.id.SignatureViewCustomerSignature);
        signatureViewCustomerSignature.setEnabled(false);

        if (null != params.getString("Signatureparam")) {

            String sSignatureData = params.getString("Signatureparam");

            signatureViewCustomerSignature.setViewSignatureData(sSignatureData);
            signatureViewCustomerSignature.setVisibility(View.VISIBLE);

            //((TextView) findViewById(R.id.Txtclient)).setVisibility(View.VISIBLE);
        }

        /*
        if (!params.getBoolean("withpassword")) {
            ((TextView) findViewById(R.id.txtWithPassword)).setVisibility(View.GONE);
        }
        */

        ((Button) findViewById(R.id.buttonCloseReceipt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  finish();
            }
        });

        ((Button) findViewById(R.id.buttonShareReceipt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, sbprinter);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        });

        ((Button) findViewById(R.id.buttonPrintReceipt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( (true == APIParameters.getInstance().getBooleanParameter("impressora_config", false))) {

                    Thread t = new Thread(new Runnable() {
                        //@TargetApi(Build.VERSION_CODES.GINGERBREAD)
                        @Override
                        public void run() {
                            try {
                       /*         String sBTPrinterAddress = APIParameters.getInstance().getStringParameter("BTPrinterMACAddress");
                                OutputStream printerOutputStream =  Extras.getSingleOutputStreamForBTSPPDeviceMACAddress(sBTPrinterAddress);

                                byte[] nPage={0x1b,0x0a,0x0a,0x0a,0x0a,0x0a,0x0a};
                                printerOutputStream.write(nPage);
                                Extras.closeSingleBTSPPConnection();*/
                                ZoopPrinter zoopPrinter=new ZoopPrinter();
                                zoopPrinter.openPrinter();
                                String Receipt = Normalizer.normalize(sbprinter.toString(), Normalizer.Form.NFD);
                                Receipt = Receipt.replaceAll("[^\\p{ASCII}]", "");
                                zoopPrinter.sendTextPrinter(Receipt.getBytes("Windows-1252"));

                                byte[] nPage={0x0c};
                                zoopPrinter.sendTextPrinter(nPage);

                                int defaultBreakLine=APIParameters.getInstance().getIntParameter("defaultbreakline",0);
                                for (int i=1;i<=defaultBreakLine;i++) {
                                    zoopPrinter.sendTextPrinter("\n".getBytes());
                                }
                                zoopPrinter.closeSerialPort();


                            } catch (Exception e) {
                                Log.e("", "IOException");
                                e.printStackTrace();
                                Toast.makeText(ReceiptPrint.this, "Erro ao imprimir recibo.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });

                    t.start();
                }
                else {
                    Toast.makeText(ReceiptPrint.this,"Nenhuma impressora configurada",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_receipt_print, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
