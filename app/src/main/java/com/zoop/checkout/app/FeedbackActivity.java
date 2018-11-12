package com.zoop.checkout.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.ZLog;
import com.zoop.zoopandroidsdk.commons.ZoopSessionHTTPJSONResponseException;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FeedbackActivity extends ZCLMenuWithHomeButtonActivity{
	private Spinner tipo_feedback;
	private EditText data_feedback;
	private EditText nome;
	private EditText mensagem;
	private SendFeedback Feedback;
	private Context context;
	private FeedbackActivity FeedbackActivity ;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.feedback_opcao,
		android.R.layout.simple_spinner_item);
		tipo_feedback = (Spinner) findViewById(R.id.tipo_feedback);
		tipo_feedback.setAdapter(adapter);
		Calendar c = Calendar.getInstance();		
		Date data=c.getTime();
		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm",new Locale("pt","br")); 
//		data_feedback=(EditText) findViewById(R.id.data);
		data_feedback.setText(f.format(data));
//		nome=(EditText) findViewById(R.id.nome);
//		mensagem=(EditText) findViewById(R.id.mensagem);
		FeedbackActivity=this;
		context=FeedbackActivity;

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void Send (View v){
	
			Feedback=new SendFeedback();
		Feedback.execute();
	}


	public class SendFeedback extends AsyncTask<Void, Void, Boolean> {
		private String ticket;
		@Override
		protected Boolean doInBackground(Void... params) {
			JSONObject joUser = null;


//			String sNome = ((EditText) findViewById(R.id.nome)).getText().toString();
//			String sMensagem = ((EditText) findViewById(R.id.mensagem)).getText().toString();
			//xx
			//String sData = ((EditText) findViewById(R.id.data)).getText().toString();
			//String sTipo= tipo_feedback.getSelectedItem().toString();

			Map<String, String> requestParams  = new HashMap<>();
//			requestParams.put("name", sNome);
			//requestParams.put("date", sData);
			//requestParams.put("message_type", sTipo);
//			requestParams.put("message_body", sMensagem);

			
			
			Preferences demoPreferences = Preferences.getInstance();
			
			JSONObject joResponse = null;
			try {
				//xx
				String sURL = ""; //Extras.getInstance().getAppURL("http://64.62.167.81/h2");
				//joResponse = ZoopSession.getInstance().postSynchronousRequest(sURL, null, requestParams);
				ticket=joResponse.getString("ticket_number");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				return false;
			}

	
			
			protected void onPostExecute(Void result) {

				Toast.makeText(context, "Sua mensagem foi recebida. Seu ticket é "+ticket,
						Toast.LENGTH_SHORT).show();
				 super.execute(result);
			}
	}	
}
