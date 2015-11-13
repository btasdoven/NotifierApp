package com.btasdoven.notifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends Activity {

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "28024609959";

    public final static String SERVER_URL = "http://40.124.44.113:8080"; //http://btasdoven.cloudapp.net:8080";
    public final static String USER_ID = "100002";

    CardAdapter mAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Button mBtnListNotif;

    @Override
    protected void onResume() {
        super.onResume();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                retrieveNotifications();
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnListNotif = (Button) findViewById(R.id.btn_list_notif);
        mBtnListNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, MyNotificationsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("GCM", "refreshing...");
                retrieveNotifications();
            }
        });

        mAdapter = new CardAdapter(this);
        ListView view = (ListView) findViewById(R.id.listView);
        view.setAdapter(mAdapter);

        getRegId();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                retrieveNotifications();
            }
        });
    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered";
                    send2server(regid);
                    Log.i("GCM",  msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    public String send2server(String regid) {

        String URL = SERVER_URL + "/register/" + USER_ID + "/" + regid;

        try {
            HttpParams my_httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(my_httpParams, 5000);
            HttpConnectionParams.setSoTimeout(my_httpParams, 3000);
            HttpClient httpclient = new DefaultHttpClient(my_httpParams);
            HttpResponse response = httpclient.execute(new HttpGet(URL));
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                return "Registeration is successfull";
            } else{
                response.getEntity().getContent().close();
                return "Registeration is unsuccessfull: " + statusLine.getStatusCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Registeration is unsuccessfull: SocketTimeOut";
        }
    }

    public void retrieveNotifications(){
        new AsyncTask<Void, Void, List<CardAdapter.ListData>>() {
            @Override
            protected List<CardAdapter.ListData> doInBackground(Void... params) {
                String URL = SERVER_URL + "/cards/" + USER_ID;
                List<CardAdapter.ListData> datas = new ArrayList<CardAdapter.ListData>();
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

                        try {
                            JSONArray notifs= new JSONArray(responseString);
                            if (notifs!= null) {
                                for (int i = 0; i < notifs.length(); ++i) {
                                    JSONObject notif = notifs.getJSONObject(i);
                                    String id = notif.getString("id");
                                    String name = notif.getString("name");
                                    String notifId = notif.getString("notif_id");
                                    int timestamp = notif.getInt("timestamp");
                                    boolean completed = notif.getBoolean("completed");
                                    datas.add(new CardAdapter.ListData(id, name, notifId, timestamp, completed));
                                }
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
            protected void onPostExecute(List<CardAdapter.ListData> msg) {
                mAdapter.clear();
                for(int i = msg.size()-1; i >= 0; --i) {
                    mAdapter.addItem(msg.get(i));
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute(null, null, null);
    }
}