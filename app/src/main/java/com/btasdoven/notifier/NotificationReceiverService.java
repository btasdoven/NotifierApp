package com.btasdoven.notifier;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NotificationReceiverService extends Service {
    public String send2server(String cardid) {

        String URL = MainActivity.SERVER_URL + "/done/" + cardid;

        try {
            HttpParams my_httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(my_httpParams, 5000);
            HttpConnectionParams.setSoTimeout(my_httpParams, 3000);
            HttpClient httpclient = new DefaultHttpClient(my_httpParams);
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_OK){

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                return "Completion is successfull";
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                return "Completion is unsuccessfull: " + statusLine.getStatusCode();
                //throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Completion is unsuccessfull: SocketTimeOut";
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "onStartCommand", Toast.LENGTH_LONG).show();
        final String cardid = intent.getStringExtra("id");
//        Toast.makeText(this, cardid + " id", Toast.LENGTH_LONG).show();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(cardid));

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = send2server(cardid);
                Log.i("GCM", msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //               etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);

        this.stopSelf();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
