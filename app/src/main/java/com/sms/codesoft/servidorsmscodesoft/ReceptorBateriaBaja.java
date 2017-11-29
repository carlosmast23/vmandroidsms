package com.sms.codesoft.servidorsmscodesoft;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Carlos on 10/11/2017.
 */

public class ReceptorBateriaBaja extends BroadcastReceiver
{
    public static String MENSAJE_BATERIA_BAJA="BATERIA BAJA";
    @Override
    public void onReceive(Context context, Intent intent) {

        new EnviarAlertaServidor(MENSAJE_BATERIA_BAJA,MainActivity.urlInformarBateriaBaja).execute();
    }
}
