package com.sms.codesoft.servidorsmscodesoft;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Carlos on 10/11/2017.
 */

public class EnviarAlertaServidor extends AsyncTask<String,String,String>
{
    private String mensaje;
    private String url_servidor="";

    public EnviarAlertaServidor(String mensaje,String url_servidor) {
        this.mensaje=mensaje;
        this.url_servidor = url_servidor;
    }

    @Override
    protected String doInBackground(String... params) {
        String parametros="numero="+mensaje;
        try {
            Log.i("Enviando alerta web", "Conectando con el servidor...");
            //S0tring urlText="http://192.168.1.2/servidor/recibirdatos.php?"+parametros;
            String data = "numero=" + URLEncoder.encode(mensaje,"UTF-8");
            URL url=new URL(url_servidor+data);
            //URL url=new URL(pagina+data);
            Log.i("Enviando sitio web", "open stream php...");
            url.openStream();
            Log.i("Enviando sitio web", "fin enviado al servidor php...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
