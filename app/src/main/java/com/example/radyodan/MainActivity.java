package com.example.radyodan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity<currentIndex> extends AppCompatActivity {
    private TextView mTextViewResult;
        private RequestQueue mQueue;
    MediaPlayer mediaPlayer = new MediaPlayer();
    public String

    currentIndex = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        setContentView(R.layout.activity_main);

        Button buttonRandomStation = findViewById(R.id.random_button);
        Button buttonPlayPause = findViewById(R.id.play_pause_button);


        mQueue = Volley.newRequestQueue(this);


        getQuote();
        getStations();


        buttonRandomStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomStation();
            }
        });
        buttonPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPause();
            }
        });

    }

    private void togglePlayPause() {
        Button button = (Button) findViewById(R.id.play_pause_button);

        if (mediaPlayer.isPlaying() == true) {
            mediaPlayer.pause();

            button.setText(">>");

        } else if (currentIndex != "") {
            button.setText("||");
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(currentIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                mediaPlayer.prepare(); // might take long! (for buffering, etc)
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();




        }
    }

    private void getQuote() {

        String url = "https://apis.zavodx.com/quotes/random";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONObject station = response.getJSONObject("data");


                            String name = station.getString("text");
                            String station_url = station.getString("author");


                            final TextView TextView = (TextView) findViewById(R.id.quote_text_view);
                            TextView.setText(name + " \n " + station_url + "\n\n");
                            


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @SuppressLint("ResourceAsColor")
    private void addButton (int i, final String station_name, final String station_url) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.llayout);
        final Button button;
        button = new Button(this);
        //button.setBackgroundColor(Color.argb(255, 17, 102, 119));

        button.setBackgroundColor(Color.argb(255, 104, 157, 106));
        button.setTextColor(Color.argb(255, 251, 241, 199));
        button.setId(i);
        button.setText(station_name);
        layout.addView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.reset();
                TextView TextView = (TextView) findViewById(R.id.station_name);
                TextView.setText(station_name);
                Button button = (Button) findViewById(R.id.play_pause_button);
                button.setText(">>");


                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(station_url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                currentIndex = station_url;
                mediaPlayer.start();            }
        });
        }


    private void addButtonL (final int i ) {

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rel);

        final Button button;
        button = new Button(this);
        button.setId(i);
        button.setText(String.valueOf(i));
        // rLayout.addView(button);
        Log.d("myTag", "buton cre");



        LinearLayout layout = (LinearLayout) findViewById(R.id.llayout);
        layout.addView(button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tuneToStation(i);
            }
        });


    }




    private void tuneToStation(int i) {

        String url = "https://apis.zavodx.com/radio_stations/" + String.valueOf(i);

        mediaPlayer.reset();



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONObject station = response.getJSONObject("data");


                            String name = station.getString("name");
                            String station_url = station.getString("url");

                            mTextViewResult.setText(name + " - " + station_url + "\n\n");

                            String url = station_url; // your URL here
                            currentIndex = station_url;

                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(url);
                        //    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mediaPlayer.start();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }



    private void getStations() {

        String url = "https://apis.zavodx.com/radio_stations";


        Log.d("myTag", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");



                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject station = jsonArray.getJSONObject(i);


                                String station_name = station.getString("name");
                                String station_url = station.getString("url");

                                Log.d("myTag", station_name);


                            //    final TextView TextView = (TextView) findViewById(R.id.stations_view);
                            //    TextView.append(firstName + "\n\n");
                                addButton(i, station_name, station_url);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }



    private void randomStation() {

        String url = "https://apis.zavodx.com/radio_stations/random";
        mediaPlayer.reset();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            JSONObject station = response.getJSONObject("data");


                            String station_name = station.getString("name");
                            String station_url = station.getString("url");

                            TextView TextView = (TextView) findViewById(R.id.station_name);
                            TextView.setText(station_name);


                            String url = station_url; // your URL here
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mediaPlayer.start();
                            Button button = (Button) findViewById(R.id.play_pause_button);
                            button.setText(">>");



                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}