package com.sms.codesoft.servidorsmscodesoft;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
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

/**
 * Created by Carlos on 05/10/2017.
 */

public class MiServicio extends IntentService {


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
            //Se conecta al servidor
            InetAddress serverAddr = InetAddress.getByName(MainActivity.ADDRESS);
            Log.i("I/TCP Client", "Conectando...");
            Socket socket = new Socket(serverAddr, MainActivity.SERVERPORT);
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
                MainActivity.actividadPrincipal.enviarMensaje(numeroStr,mensajeTxt);
                //publishProgress(numeroStr,mensajeTxt);

            }
            //cierra conexion
            socket.close();
        }catch (UnknownHostException ex) {
            Log.e("E/TCP Client", "" + ex.getMessage());
        } catch (IOException ex) {
            Log.e("E/TCP Client", "" + ex.getMessage());
        }
    }
}