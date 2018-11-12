package com.zoop.checkout.app;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;
import com.zoop.zoopandroidsdk.commons.UFUC;
import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsCheckout;
import com.zoop.zoopandroidsdk.sessions.ZoopSessionsPayments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by mainente on 03/11/15.
 */
public class DocumentsCardAdapter extends RecyclerView.Adapter<DocumentsCardAdapter.DocumentsViewHolder> {
   JSONArray jaDocuments;
    Context c;
    Activity aDocuments;
    private Uri fileUri;
    SendDocuments sendDocuments;
    Bitmap photo;
    static String nameDoc;
     View mLoginStatusView;
     TextView mLoginFormView;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_FILE=101;

    public DocumentsCardAdapter(Context c, JSONArray jaDocuments, Activity aDocuments) {
        this.jaDocuments=jaDocuments;
        this.c=c;
        this.aDocuments=aDocuments;

    }

    @Override
    public int getItemCount() {
        return jaDocuments.length();
    }

    @Override
    public void onBindViewHolder(final DocumentsViewHolder documentsViewHolder, final int position) {


        try {
            JSONArray jaDo=jaDocuments.getJSONObject(position).getJSONArray("documents");
            String sNameDocument="";
            String sNameDocumentInfo="";

            String infoFiles="Com até 3MB no máximo.\n" +
                    "Imagem ou extensão: .doc, .pdf.\n" +
                    "Enviar frente e verso dos documentos." ;

            documentsViewHolder.infoFiles.setText(infoFiles);


            if(jaDocuments.getJSONObject(position).getString("documents_type").equals("identificacao")){


                sNameDocument="Documento de identificação";
                sNameDocumentInfo="CPF e RG ou CNH";

            }else if(jaDocuments.getJSONObject(position).getString("documents_type").equals("residencia")){


                sNameDocument="Comprovante de residência";
                sNameDocumentInfo="em seu nome";

            }else if(jaDocuments.getJSONObject(position).getString("documents_type").equals("atividade")){


                sNameDocument="Comprovante de atividade";
                sNameDocumentInfo="imagem ou documento";

            }else if(jaDocuments.getJSONObject(position).getString("documents_type").equals("cnpj")){


                sNameDocument="CNPJ";
                sNameDocumentInfo="imagem ou documento";

            }

            String statusDoc;
            Integer colorStatus;

            if(jaDo.length()==0){

                statusDoc="Não enviado";
                colorStatus= Color.RED;



            }else{

                statusDoc="Aprovado";
                colorStatus= Color.rgb(35,142,35);
                for(int i=0;i<jaDo.length();i++){

                    if(jaDo.getJSONObject(i).getString("status").equals("reproved")){

                        statusDoc="Reprovado";
                        colorStatus= Color.RED;



                    }






                }

                for(int i=0;i<jaDo.length();i++) {

                    if (jaDo.getJSONObject(i).getString("status").equals("pending")) {

                        statusDoc = "Em análise";
                        colorStatus = Color.rgb(217,217,25);


                    }
                }








            }



                documentsViewHolder.nameDocumentStatus.setText(statusDoc);
                documentsViewHolder.nameDocumentStatus.setTextColor(colorStatus);


            documentsViewHolder.nameDocument.setText(sNameDocument);
            documentsViewHolder.nameDocumentInfo.setText(sNameDocumentInfo);

            documentsViewHolder.documentCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        nameDoc=jaDocuments.getJSONObject(position).getString("documents_type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    aDocuments.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

                    mLoginStatusView=documentsViewHolder.vProgress;
                    mLoginFormView=documentsViewHolder.nameDocumentStatus;





                }
            });

            documentsViewHolder.selectDocument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mLoginStatusView=documentsViewHolder.vProgress;
                    mLoginFormView=documentsViewHolder.nameDocumentStatus;


                    try {
                        nameDoc=jaDocuments.getJSONObject(position).getString("documents_type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

/*
                   Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);*/

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    String[] mimetypes = {"image/jpeg", "image/png","application/msword","application/pdf"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        aDocuments.startActivityForResult(
                                Intent.createChooser(intent, "Selecionar arquivo"),
                                SELECT_FILE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Potentially direct the user to the Market with a Dialog

                        ZLog.exception(1,ex);

                    }




                }
            });





        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = aDocuments.getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginStatusView.setVisibility(show ? View.VISIBLE
                            : View.GONE);
                }
            });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE
                            : View.VISIBLE);
                }
            });
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public DocumentsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_documents, viewGroup, false);

        return new DocumentsViewHolder(itemView);
    }



    public static class DocumentsViewHolder extends RecyclerView.ViewHolder {

        private TextView nameDocument;
        private Button selectDocument;
        private TextView  nameDocumentInfo;
        private TextView    nameDocumentStatus;
        private TextView    infoFiles;

        private ImageButton documentCam;
        private View vProgress;




        public DocumentsViewHolder(View v) {
            super(v);

            nameDocument=(TextView)v.findViewById(R.id.nameDocument);
            nameDocumentInfo=(TextView)v.findViewById(R.id.nameDocumentInfo);
            nameDocumentStatus=(TextView)v.findViewById(R.id.nameDocumentStatus);
            selectDocument=(Button)v.findViewById(R.id.selectDocument);
            documentCam=(ImageButton)v.findViewById(R.id.documentCam);
            vProgress=(View)v.findViewById(R.id.login_status);
            infoFiles=(TextView)v.findViewById(R.id.filesInfo);











        }


    }



    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File mediaFile;



        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                   nameDoc+ "_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode ==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == aDocuments.RESULT_OK) {

                File fDocuments;



                fDocuments=new File(fileUri.getPath());
                photo= BitmapFactory.decodeFile(fDocuments.getAbsolutePath());
                //  fDocuments.createNewFile();


                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(fDocuments);


                photo = Bitmap.createScaledBitmap(photo, 1024, 1024, false);

                photo.compress(Bitmap.CompressFormat.JPEG, 80, fOut);

                fOut.flush();
                fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showProgress(true);
                sendDocuments = new SendDocuments();
                sendDocuments.execute((fDocuments));










            } else if (resultCode == aDocuments.RESULT_CANCELED) {
                Toast.makeText(c, "Cancelou", Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(c, "Saiu", Toast.LENGTH_SHORT);
            }

        }else if(requestCode==SELECT_FILE){



            if (resultCode == aDocuments.RESULT_OK) {

                try {
                    fileUri = intent.getData();


               /*   *//*  fileUri = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = aDocuments.getContentResolver().query(
                            fileUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
*//*
                    String wholeID = DocumentsContract.getDocumentId(fileUri);
                    String[]whole=wholeID.split(":");

// Split at colon, use second item in the array
                    String id;
                    if (whole.length<2){
                        id=wholeID;
                    }else {
                        id = wholeID.split(":")[1];
                    }
                    String[] column = { MediaStore.Images.ImageColumns.DATA };

// where id is equal to
                    String sel = MediaStore.Images.ImageColumns._ID + "=?";
                    Cursor cursor = aDocuments.getContentResolver().
                            query(MediaStore.Files.getContentUri("external"),
                                    column, sel, new String[]{ id }, null);

                    String filePath = "";

                    int columnIndex = cursor.getColumnIndex(column[0]);

                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex);
                    }

                    cursor.close();
                    File fDocuments;





                    fileUri = Uri.parse(filePath);

                    fDocuments = new File(fileUri.getPath());


                    String type = null;
                    String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
                    if (extension != null) {
                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    }

                    ZLog.t(type);*/

                    Uri selectedImageUri = intent.getData();



                    String selectedImagePath = ImageFilePath.getPath(
                            aDocuments, selectedImageUri);

                   File fDocuments=new File(selectedImagePath);



                    showProgress(true);

                    sendDocuments = new SendDocuments();
                    sendDocuments.execute((fDocuments));
                }catch (Exception e){
                    ZLog.exception(1,e);
                }










            } else if (resultCode == aDocuments.RESULT_CANCELED) {
                Toast.makeText(c, "Cancelou", Toast.LENGTH_SHORT);
            } else { //Saiu da Intent
                Toast.makeText(c, "Saiu", Toast.LENGTH_SHORT);
            }




        }






}


    public class SendDocuments extends AsyncTask<File, Void, Boolean> {
        private String ticket;
        @Override
        protected Boolean doInBackground(File... params) {
            JSONObject joUser = null;


            JSONObject joResponse = null;
            String filename = null;







            try {


                if(nameDoc.equals("identificacao")){


                    filename="file1";

                }else if(nameDoc.equals("residencia")){

                    filename="file2";


                }else if(nameDoc.equals("atividade")){

                    filename="file3";


                }else if(nameDoc.equals("cnpj")){

                    filename="file4";


                }








                File fDocuments=params[0];
                Map<String, File> requestParams = new HashMap<>();
                requestParams.put(filename, fDocuments);
             //   requestParams.put("resource","177");
                String publishableKey =  APIParameters.getInstance().getStringParameter("publishableKey");
                String sMarketplaceId = APIParameters.getInstance().getStringParameter("marketplaceId");
                String seller_id = APIParameters.getInstance().getStringParameter("sellerId");
                String sCheckoutPublicKey=APIParameters.getInstance().getStringParameter("sCheckoutPublicKey");
                String sSendDocumentsUrl = UFUC.getUFU("https://api.zoopcheckout.com/v1/documents");



            /*    try {


                    joResponse = com.zoop.checkout.app.ZoopSession.getInstance().postSynchronousRequest(sSendDocumentsUrl, sCheckoutPublicKey, requestParams, "laravel_session=" + cookie);


                }catch (Exception e){

                    Extras.getInstance().signin(c);
                    cookie= APIParameters.getInstance().getStringParameter("cookie");

                    joResponse = com.zoop.checkout.app.ZoopSession.getInstance().postSynchronousRequest(sSendDocumentsUrl, sCheckoutPublicKey, requestParams, "laravel_session=" + cookie);




                }*/


                long fileSizeInBytes = fDocuments.length();



                float fileSizeInKB = fileSizeInBytes / 1024;
                // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                float fileSizeInMB = fileSizeInKB / 1024;

                if(fileSizeInMB<=3) {

                  //  joResponse = ZoopSessionsCheckout.getInstance().syncPostWithCookie( sSendDocumentsUrl, sCheckoutPublicKey, requestParams, c);


                    ZLog.t(joResponse.toString());


                    return true;
                }else {
                    return false;
                }




            }catch (Exception e) {
                ZLog.exception(300009, e);
            }
            return false;

        }



        protected void  onPostExecute(final Boolean result) {



            showProgress(false);


            if(result) {



                Toast.makeText(c,"Documento enviado, aguarde análise.",Toast.LENGTH_LONG).show();

                mLoginFormView.setText("Em análise");
                mLoginFormView.setTextColor(Color.rgb(217,217,25));




            }else{
                Toast.makeText(c,"Erro ao enviar documento, verifique o tamanho do arquivo ou sua extensão.",Toast.LENGTH_LONG).show();


            }



        }
    }






}
