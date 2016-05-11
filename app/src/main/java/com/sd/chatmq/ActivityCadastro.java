package com.sd.chatmq;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * @author Isaías Santana
 * Created on 04/05/2016
 * Representa uma tela de cadastro do número de celular do usuário
 */
public class ActivityCadastro extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_cadastro);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Número não cadastrado.")
                    .setMessage("Você ainda não cadastrou seu número de celular. Deseja realmente finalizar ?")
                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Encerra a atividade
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    public void cadastrarNumero(View view)
    {
        final EditText numeroCelular = (EditText) findViewById(R.id.editNumero);
        String numCel = numeroCelular.getText().toString();

        if(!numCel.matches(""))
        {
            Intent intentResposta = new Intent();
            intentResposta.putExtra("CELULAR",numCel);
            setResult(Activity.RESULT_OK,intentResposta);
            finish();
        }
    }


}
