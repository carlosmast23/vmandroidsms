package com.sms.codesoft.servidorsmscodesoft;

import android.Manifest;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {
    /**
     * Valor por defecto de la url que contiene la pagina que tengo que reenviar los
     * mensajes y el numero del celular
     */
    //public static String urlPeticion="http://www.vm.codesoft-ec.com/busquedas/recibir_sms";
    public static String urlPeticion="http://www.vm.codesoft-ec.com/busquedas/recibir_sms?borrar";
    public static String urlInformarBateriaBaja="http://www.vm.codesoft-ec.com/cronos/recibir_alert";
    public static final int REQUEST_CODE_FOR_SMS=1;
    //public static String urlPeticion="http://192.168.100.11/servidor/recibirdatos.php?";

    /**
     * Controles
     * */
    private Button button;
    private TextView textView;
    private TextView txtPeticionesWeb;
    private TextView  txtConsola;
    //private EditText editText2;
    private Context context = this;
    private Button btnLimpiar;

    /**
     * Puerto
     * */
    public static int SERVERPORT = 5000;
    /**
     * HOST
     * */
    public static String ADDRESS = "192.168.100.8";

    private Intent intent;

    public static MainActivity actividadPrincipal;

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

        initComponent();

        //editText2 = ((EditText) findViewById(R.id.txtRecibir));
        ///Agregar la direccion ip de la red
        txtPeticionesWeb.setText(urlPeticion);
        textView.setText(getIP());
        intent = new Intent(this,MiServicio.class);



        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d("CLick : ","click");
                        urlPeticion=txtPeticionesWeb.getText().toString();
                        ADDRESS=textView.getText().toString();
                        intent.putExtra("address", ADDRESS);
                        intent.putExtra("puerto", SERVERPORT);

                        //MyATaskCliente myATaskYW = new MyATaskCliente();
                        //myATaskYW.execute("");
                        startService(intent);
                    }
                });

        btnLimpiar.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d("Accion : ","limpiar");
                        txtConsola.setText("");
                    }
                });

        PermissionManager.check(this, Manifest.permission.RECEIVE_SMS, REQUEST_CODE_FOR_SMS);
        actividadPrincipal=this;
        receptorMensajeIntent();
    }

    private void receptorMensajeIntent()
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MiServicio.NOMBRE_MENSAJE);
        //filter.addAction(MiIntentService.ACTION_FIN);
        ProgressReceiver rcv = new ProgressReceiver();
        registerReceiver(rcv, filter);
    }

    /**
     * Inicializar los componentes de la vista
     */
    private void initComponent()
    {
        txtConsola=(TextView)findViewById(R.id.multilineConsole);
        button = ((Button) findViewById(R.id.button));
        textView = ((TextView) findViewById(R.id.txtDireccionIp));
        txtPeticionesWeb=((TextView)findViewById(R.id.txtPeticionWeb));
        btnLimpiar=((Button) findViewById(R.id.btnLimpiar));
    }

    public String getIP() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if ((intf.getName().contains("wlan")) || (intf.getName().contains("ap"))) {
                    for (Enumeration <InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() &&
                                (inetAddress.getAddress().length == 4)) {
                            //Log.d(TAG, inetAddress.getHostAddress());
                            Log.i("Direccion IP: ",inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.i("Excepcion : ",ex.toString());
        }
        return null;
    }

    public void enviarMensaje (String Numero, String Mensaje){
        try {
            Thread.sleep(500);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(Numero,null,Mensaje,null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }


    public TextView getTxtConsola() {
        return txtConsola;
    }

    public void setTxtConsola(TextView txtConsola) {
        this.txtConsola = txtConsola;
    }

    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MiServicio.NOMBRE_MENSAJE)) {
                String log = intent.getStringExtra("log");

                txtConsola.setText(log+"\n"+txtConsola.getText());

                /**
                 * Eliminar los logs cuando el tamaño es demasiado grande para no tener problemas
                 * de rendimiento con el celular
                 */
                if(txtConsola.getText().length()>=10000)
                {
                    txtConsola.setText(txtConsola.getText().toString().substring(0,10000));
                }


                //txtConsola.append(System.getProperty("line.separator"));
                ///txtConsola.append(log);
            }

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
