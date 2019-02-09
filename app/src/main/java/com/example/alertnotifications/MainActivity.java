package com.example.alertnotifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String CATEGORY_URL1="http://traclytics-migration.herokuapp.com/logs/category1/";
    private String CATEGORY_URL2="http://traclytics-migration.herokuapp.com/logs/category2/";
    private String CATEGORY_URL3="http://traclytics-migration.herokuapp.com/logs/category3/";

    RecyclerView firstCategoryRecyclerView;
    RecyclerView secondCategoryRecyclerView;
    RecyclerView thirdCategoryRecyclerView;

    private NotificationListAdapter mAdapter1;
    private NotificationListAdapter mAdapter2;
    private NotificationListAdapter mAdapter3;

    CardView cardView1;
    CardView cardView2;
    CardView cardView3;

    private boolean firstCard=false;
    private boolean secondCard=false;
    private boolean thirdCard=false;

    private ImageButton firstCategoryDeleteButton;
    private ImageButton secondCategoryDeleteButton;
    private ImageButton thirdCategoryDeleteButton;

    List<NotificationList> notificationListList1;
    List<NotificationList> notificationListList2;
    List<NotificationList> notificationListList3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set local attributes to corresponding views
        firstCategoryRecyclerView = (RecyclerView)findViewById(R.id.first_category_list);
        secondCategoryRecyclerView = (RecyclerView)findViewById(R.id.second_category_list);
        thirdCategoryRecyclerView = (RecyclerView)findViewById(R.id.third_category_list);

        firstCategoryDeleteButton = (ImageButton) findViewById(R.id.first_category_delete_all);
        secondCategoryDeleteButton = (ImageButton) findViewById(R.id.second_category_delete_all);
        thirdCategoryDeleteButton = (ImageButton)findViewById(R.id.third_category_delete_all);

        // Set layout for the RecyclerView, because it's a list i'm using the linear layout
        LinearLayoutManager manager1 = new LinearLayoutManager(this);
        LinearLayoutManager manager2 = new LinearLayoutManager(this);
        LinearLayoutManager manager3 = new LinearLayoutManager(this);
        firstCategoryRecyclerView.setLayoutManager(manager1);
        secondCategoryRecyclerView.setLayoutManager(manager2);
        thirdCategoryRecyclerView.setLayoutManager(manager3);

        firstCategoryRecyclerView.setHasFixedSize(false);
        secondCategoryRecyclerView.setHasFixedSize(true);
        thirdCategoryRecyclerView.setHasFixedSize(false);

        notificationListList1 = new ArrayList<>();
        notificationListList2 = new ArrayList<>();
        notificationListList3 = new ArrayList<>();

        mAdapter1 = new NotificationListAdapter(notificationListList1,this);
        mAdapter2 = new NotificationListAdapter(notificationListList2,this);
        mAdapter3 = new NotificationListAdapter(notificationListList3,this);

        cardView1= (CardView)findViewById(R.id.first_cardview);
        cardView2= (CardView)findViewById(R.id.second_cardview);
        cardView3= (CardView)findViewById(R.id.third_cardview);


        //checking the deleted categories using sharedpreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("SHARED_CATEGORY",Context.MODE_PRIVATE);
        String removedCategory= sharedPreferences.getString("DELETED_CATEGORY","");
        Log.d("Category removed",removedCategory);

        if(removedCategory.length()!=0) {
            String[] removed = removedCategory.split(",");
            for (String i : removed) {
                if (i.equals("category1")) {
                    cardView1.setVisibility(View.GONE);
                } else if (i.equals("category2")) {
                    cardView2.setVisibility(View.GONE);
                } else if (i.equals("category3")) {
                    cardView3.setVisibility(View.GONE);
                }
            }
        }

        //making api call using volley for the all urls
        getNotificationDataForFirstCategory();
        firstCategoryRecyclerView.setAdapter(mAdapter1);

        getNotificationDataForSecondCategory();
        secondCategoryRecyclerView.setAdapter(mAdapter2);

        getNotificationDataForThirdCategory();
        thirdCategoryRecyclerView.setAdapter(mAdapter3);

        //implementing the visibility of the categories according to user's click
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!firstCard) {
                    firstCategoryRecyclerView.setVisibility(View.VISIBLE);
                    firstCard=true;
                }else{
                    firstCategoryRecyclerView.setVisibility(View.GONE);
                    firstCard=false;
                }
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!secondCard) {
                    secondCategoryRecyclerView.setVisibility(View.VISIBLE);
                    secondCard=true;
                }else{
                    secondCategoryRecyclerView.setVisibility(View.GONE);
                    secondCard=false;
                }

            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!thirdCard) {
                    thirdCategoryRecyclerView.setVisibility(View.VISIBLE);
                    thirdCard=true;
                }else{
                    thirdCategoryRecyclerView.setVisibility(View.GONE);
                    thirdCard=false;
                }
            }
        });

        //storing deleted text through Sharedpreferences
        final String[] deletedCategory = {""};
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SHARED_CATEGORY",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        firstCategoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView1.setVisibility(View.GONE);
                notificationListList1.clear();
                deletedCategory[0] +="category1";
                editor.putString("DELETED_CATEGORY", deletedCategory[0]);
                editor.commit();


            }
        });

        secondCategoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView2.setVisibility(View.GONE);
                notificationListList2.clear();
                deletedCategory[0] +=",category2";
                editor.putString("DELETED_CATEGORY", deletedCategory[0]);
                editor.commit();
            }
        });

        thirdCategoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView3.setVisibility(View.GONE);
                notificationListList3.clear();
                deletedCategory[0] +=",category3";
                editor.putString("DELETED_CATEGORY", deletedCategory[0]);
                editor.commit();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //for refresh button
        if(item.getItemId() == R.id.refresh_button){

            notificationListList1.clear();
            notificationListList2.clear();
            notificationListList3.clear();

            getNotificationDataForFirstCategory();
            getNotificationDataForSecondCategory();
            getNotificationDataForThirdCategory();

        }
        return super.onOptionsItemSelected(item);
    }

    public void getNotificationDataForFirstCategory() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CATEGORY_URL1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("inside onResponse","reached");

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            notificationListList1.clear();
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                NotificationList item = new NotificationList(object.getString("category"),object.getString("title")
                                        ,object.getString("time_stamp"),true);
                                notificationListList1.add(item);
                                mAdapter1.notifyDataSetChanged();
                            }


                        }catch(JSONException e){
                            //progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();

            }
        })

                //caching the data once loaded
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }

            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

        };

        //Requesting using volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void getNotificationDataForSecondCategory() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CATEGORY_URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("inside onResponse","reached");

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            notificationListList2.clear();
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                NotificationList item = new NotificationList(object.getString("category"),object.getString("title")
                                        ,object.getString("time_stamp"),true);
                                notificationListList2.add(item);
                                mAdapter2.notifyDataSetChanged();
                            }

                        }catch(JSONException e){
                            //progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();

            }
        })

                //caching news data once loaded
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }

            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

        };

        //Requesting using volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void getNotificationDataForThirdCategory() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, CATEGORY_URL3,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("inside onResponse","reached");

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            notificationListList3.clear();
                            for(int i=0; i<jsonArray.length();i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                NotificationList item = new NotificationList(object.getString("category"),object.getString("title")
                                        ,object.getString("time_stamp"),true);
                                notificationListList3.add(item);
                                mAdapter3.notifyDataSetChanged();
                            }

                        }catch(JSONException e){
                            //progressDialog.dismiss();
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "try again", Toast.LENGTH_SHORT).show();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Check your Internet", Toast.LENGTH_SHORT).show();

            }
        })

                //caching news data once loaded
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(jsonString, cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }

            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

        };

        //Requesting using volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}
