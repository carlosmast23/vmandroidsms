package com.sms.codesoft.servidorsmscodesoft;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {


    /**
     * Controles
     * */
    private Button button;
    private TextView textView;
    //private EditText editText2;
    private Context context = this;

    /**
     * Puerto
     * */
    private static final int SERVERPORT = 5000;
    /**
     * HOST
     * */
    private static final String ADDRESS = "192.168.100.21";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        button = ((Button) findViewById(R.id.button));
        textView = ((TextView) findViewById(R.id.textView));
        //editText2 = ((EditText) findViewById(R.id.txtRecibir));

        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        //if(editText.getText().toString().length()>0){
                        MyATaskCliente myATaskYW = new MyATaskCliente();
                        myATaskYW.execute("");
                        //}else{
                        //   Toast.makeText(context, "Escriba \"frase\" o \"libro\" ", Toast.LENGTH_LONG).show();
                        //}

                    }
                });
    }

    private void enviarMensaje (String Numero, String Mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(Numero,null,Mensaje,null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    /**
     * Clase para interactuar con el servidor
     * */
    class MyATaskCliente extends AsyncTask<String,String,String> {

        /**
         * Ventana que bloqueara la pantalla del movil hasta recibir respuesta del servidor
         * */
        ProgressDialog progressDialog;

        /**
         * muestra una ventana emergente
         * */
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Conexion establecida con el servidor");
            progressDialog.setMessage("Procesando...");
            progressDialog.show();
        }

        /**
         * Se conecta al servidor y trata resultado
         * */
        @Override
        protected String doInBackground(String... values){

            try {
                //Se conecta al servidor
                InetAddress serverAddr = InetAddress.getByName(ADDRESS);
                Log.i("I/TCP Client", "Conectando...");
                Socket socket = new Socket(serverAddr, SERVERPORT);
                Log.i("I/TCP Client", "Conectado con el Servidor");

                //Se queda en bucle infinito hasta enviar los mensajes

                boolean salir=true;
                InputStream inputStream = socket.getInputStream();
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader( inputStream ) );
                while(salir) {
                    String numeroStr="";
                    //recibe respuesta del servidor y formatea a String
                    //byte[] lenBytes = new byte[256];
                    //inputStream.read(lenBytes, 0, 256);
                    //numeroStr = new String(lenBytes, "UTF-8").trim();
                    numeroStr=entrada.readLine();
                    Log.i("I/TCP Client", "NumeroRecibido: " + numeroStr);

                    //envia peticion de cliente
                    //Log.i("I/TCP Client", "Send data to server");
                    //PrintStream output = new PrintStream(socket.getOutputStream());
                    //output.println(" ");

                    //recibe respuesta del servidor y formatea a String

                    //byte[] lenBytes2 = new byte[256];
                    //inputStream.read(lenBytes2, 0, 256);
                    //String mensajeTxt = new String(lenBytes, "UTF-8").trim();
                    String mensajeTxt =entrada.readLine();
                    Log.i("I/TCP Client", "MensajeRecibido: " + mensajeTxt);

                    publishProgress(numeroStr,mensajeTxt);

                }
                //cierra conexion
                socket.close();
                return "";
            }catch (UnknownHostException ex) {
                Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            } catch (IOException ex) {
                Log.e("E/TCP Client", "" + ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(String... datos) {
            //TV_mensaje.setText("Progreso descarga: "+porcentajeProgreso[0]+"%. Hilo PRINCIPAL");
            //Log.v(TAG_LOG, "Progreso descarga: "+porcentajeProgreso[0]+"%. Hilo PRINCIPAL");
            Log.i("Numero",datos[0]);
            Log.i("Mensaje",datos[1]);
            enviarMensaje(datos[0],datos[1]);
            //miBarraDeProgreso.setProgress( Math.round(porcentajeProgreso[0]) );
        }

        /**
         * Oculta ventana emergente y muestra resultado en pantalla
         * */
        @Override
        protected void onPostExecute(String value){
            progressDialog.dismiss();
            //textView.setText(value);
        }




    }

}
