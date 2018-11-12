package com.zoop.checkout.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zoop.zoopandroidsdk.commons.APIParameters;

/**
 * Created by mainente on 11/03/15.
 */
public class ConfigSenhaActivity extends ZCLMenuActivity {
    private EditText senha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_config);
        senha= (EditText) findViewById(R.id.senha);
    }
    public void ValidarSenha (View v){
        Preferences demoPreferences = Preferences.getInstance();

        if (!(senha.getText().toString().equals(""))){

         if (senha.getText().toString().equals(APIParameters.getInstance().getGlobalStringParameter("currentLoggedinSecurityToken")
         )){

             Intent ConfigIntent = new Intent(ConfigSenhaActivity.this, PaymentMethodsActivity.class);
             startActivity(ConfigIntent);
         }else{
             Toast.makeText(this, "Senha inválida " ,
                     Toast.LENGTH_SHORT).show();
         }
        }else{
             Toast.makeText(this, "Senha não informada " ,
                     Toast.LENGTH_SHORT).show();
         }



    }
}
