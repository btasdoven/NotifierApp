package com.btasdoven.notifier;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyNotificationsActivity extends Activity {

    NotificationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notifications);
        mAdapter = new NotificationAdapter(this);
        ListView view = (ListView) findViewById(R.id.listView);
        view.setAdapter(mAdapter);
        retrieveNotifications();
    }

    public void retrieveNotifications(){
        new AsyncTask<Void, Void, List<NotificationAdapter.ListData>>() {
            @Override
            protected List<NotificationAdapter.ListData> doInBackground(Void... params) {
                String URL = MainActivity.SERVER_URL + "/notifs/" + MainActivity.USER_ID;
                List<NotificationAdapter.ListData> datas = new ArrayList<NotificationAdapter.ListData>();
                try {
                    HttpParams my_httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(my_httpParams, 5000);
                    HttpConnectionParams.setSoTimeout(my_httpParams, 3000);
                    HttpClient httpclient = new DefaultHttpClient(my_httpParams);
                    HttpResponse response = httpclient.execute(new HttpGet(URL));
                    StatusLine statusLine = response.getStatusLine();

                    if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();
                        Log.i("GCM", responseString);

                        try {JSONArray notifs= new JSONArray(responseString);

                            for(int i = 0; i < notifs.length(); ++i) {
                                JSONObject notif = notifs.getJSONObject(i);
                                String id = notif.getString("id");
                                String name = notif.getString("name");
                                int period = notif.getInt("period");
                                JSONArray tses = notif.getJSONArray("start_ts");
                                int timestamps[] = new int[tses.length()];
                                for (int j = 0; j < tses.length(); ++j) {
                                    timestamps[j] = tses.getInt(j);
                                }
                                datas.add(new NotificationAdapter.ListData(id, name, timestamps, period));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        out.close();
                    } else{
                        response.getEntity().getContent().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return datas;
            }

            @Override
            protected void onPostExecute(List<NotificationAdapter.ListData> msg) {
                mAdapter.clear();
                for(int i = msg.size()-1; i >= 0; --i) {
                    mAdapter.addItem(msg.get(i));
                }
            }
        }.execute(null, null, null);
    }
}
