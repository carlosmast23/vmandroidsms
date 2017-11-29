package com.sms.codesoft.servidorsmscodesoft;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Carlos on 05/10/2017.
 */

public class MiServicio extends IntentService {

    public static final String NOMBRE_MENSAJE =
            "vmandroidsms.smsbroadcast";

    ProgressDialog progressDialog;

    private void enviarMensaje (String Numero, String Mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();

            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(this, 0,new Intent(SENT), 0);

            sms.sendTextMessage(Numero,null,Mensaje,sentPI,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public MiServicio () {
        super("MiServicio");
    }
    @Override
    protected void onHandleIntent(Intent intencion) {
        Log.i("Intente Service: ","---->");
        try {
            String address=intencion.getStringExtra("address");
            int serverPort=intencion.getIntExtra("puerto",0);
            //Se conecta al servidor
            InetAddress serverAddr = InetAddress.getByName(address);
            Log.i("I/TCP Client", "Conectando con "+address+":"+serverPort+" ...");
            mensajeBroadcast("Conectando con "+address+":"+serverPort+" ...");
            Socket socket = new Socket(serverAddr,serverPort);
            Log.i("I/TCP Client", "Conectado con el Servidor");
            mensajeBroadcast("Conectado exitosamente ...");

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
                mensajeBroadcast("enviando mensaje a "+numeroStr+ ": "+mensajeTxt);
                MainActivity.actividadPrincipal.enviarMensaje(numeroStr,mensajeTxt);
                mensajeBroadcast("mensaje enviado ...");
                //publishProgress(numeroStr,mensajeTxt);

            }
            //cierra conexion
            socket.close();
        }catch (UnknownHostException ex) {
            Log.e("E/TCP Client", "" + ex.getMessage());
            mensajeBroadcast(ex.getMessage());

        } catch (IOException ex) {
            Log.e("E/TCP Client", "" + ex.getMessage());
            mensajeBroadcast(ex.getMessage());
        }
    }

    /**
     * Metodo que me permite enviar un mensaje al hilo principal
     */
    private void mensajeBroadcast (String log)
    {
        //Agregando la fecha de la aplicacion ///
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR, 17);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 2);
        System.out.println(simpleDateFormat.format(calendar.getTime()));
        log="I:["+simpleDateFormat.format(calendar.getTime())+" "+log+"]";


        Intent bcIntent = new Intent();
        bcIntent.setAction(NOMBRE_MENSAJE);
        bcIntent.putExtra("log",log);
        sendBroadcast(bcIntent);



    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        //progressDialog = new ProgressDialog(MainActivity.actividadPrincipal);
        //progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.setTitle("Conexion establecida con el servidor");
        //progressDialog.setMessage("Procesando...");
        //progressDialog.show();
        mensajeBroadcast("intentado conectar con el servidor ...");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mensajeBroadcast("proceso conexion terminado ");
        //progressDialog.dismiss();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}