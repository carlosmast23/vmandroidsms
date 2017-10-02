package com.sms.codesoft.servidorsmscodesoft;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by Carlos on 13/09/2017.
 */

public class PeticionGet extends AsyncTask<String,String,String>
{
    //final String pagina="http://192.168.1.3/servidor/recibirdatos.php?";
    final String pagina="http://www.vm.codesoft-ec.com/";

    String numero;
    String mensaje;
    public PeticionGet(String numero,String mensaje){
        this.numero=numero;
        this.mensaje=mensaje;
    }

    @Override
    protected String doInBackground(String... params) {
        String parametros="numero="+numero+"&mensaje="+mensaje;
        try {
            Log.i("Enviando sitio web", "Conectando con la pagina php...");
            //S0tring urlText="http://192.168.1.2/servidor/recibirdatos.php?"+parametros;
            String data = "numero=" + URLEncoder.encode(numero,"UTF-8")+"&mensaje="+URLEncoder.encode(mensaje,"UTF-8");
            URL url=new URL(pagina+data);
            Log.i("Enviando sitio web", "open stream php...");
            url.openStream();
            //String data = "numero=" + URLEncoder.encode(numero,"UTF-8")+"&mensaje="+URLEncoder.encode(mensaje,"UTF-8");
            //new URL(pagina+data).openStream();
            //HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //connection.setReadTimeout(10000 /* milisegundos */);
            //connection.setConnectTimeout(15000 /* milisegundos */);
            //connection.setRequestMethod("GET");
            //connection.setDoInput(true);
            //connection.connect();

            //connection.getResponseCode();

            //HttpClient httpclient = new DefaultHttpClient();
            Log.i("Enviando sitio web", "fin enviado al servidor php...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


}

