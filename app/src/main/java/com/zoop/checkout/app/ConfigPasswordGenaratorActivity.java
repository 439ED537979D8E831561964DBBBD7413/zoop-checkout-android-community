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
public class ConfigPasswordGenaratorActivity extends ZCLMenuActivity{
    private EditText senha;
    private EditText confirmsenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_config_generator);
        senha= (EditText) findViewById(R.id.senha);
        confirmsenha=(EditText) findViewById(R.id.senhaconfirm);
    }
    public void GerarSenha (View v){

        if (!((senha.getText().toString().equals("")) && (!(confirmsenha.getText().toString().equals(""))))) {
            if (senha.getText().toString().equals(confirmsenha.getText().toString())){
                APIParameters.getInstance().putParameter("senha",senha.getText().toString());
                Toast.makeText(this, "Senha criada com sucesso ",
                        Toast.LENGTH_SHORT).show();
                Intent ConfigIntent = new Intent(ConfigPasswordGenaratorActivity.this, PaymentMethodsActivity.class);
                startActivity(ConfigIntent);
            }else{
                Toast.makeText(this, "Senhas não conferem senha1 ",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Campo senha não foi preenchido " ,
                    Toast.LENGTH_SHORT).show();
        }

    }


}
