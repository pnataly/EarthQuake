package com.example.earthquake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquake.adapter.RecyclerViewAdapter;
import com.example.earthquake.model.EarthQuake;
import com.example.earthquake.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuakesListActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private List<EarthQuake> quakeList;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RequestQueue queue;

    private SharedPreferences prefs;
    private double MAG;
    private int NUM_TO_SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        quakeList = new ArrayList<>();
        queue = Volley.newRequestQueue(this);

        getEarthQuakeList();
    }

    //fill the quake list from the internet
    public void getEarthQuakeList(){
        String minMagnitude = prefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String minShow = prefs.getString(
                getString(R.string.settings_min_numbers_key),
                getString(R.string.settings_min_numbers_default));

        final String orderBy = prefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );



        MAG = Double.parseDouble(minMagnitude);
        NUM_TO_SHOW = Integer.parseInt(minShow);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("features");

                    for (int i = 0; i < NUM_TO_SHOW; i++) {
                        JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");

                        JSONObject geometry = jsonArray.getJSONObject(i).getJSONObject("geometry");

                        //get coordinates array
                        JSONArray coordinates = geometry.getJSONArray("coordinates");

                        double lon = coordinates.getDouble(0);
                        double lat = coordinates.getDouble(1);

                        if(properties.getDouble("mag") >= MAG){

                            //Setup EarthQuake Object
                            EarthQuake earthQuake = new EarthQuake(properties.getString("place"),
                                    properties.getDouble("mag"), properties.getLong("time"),
                                    lat, lon);
                            quakeList.add(earthQuake);
                        }
                    }

                    Collections.sort(quakeList, new Comparator<EarthQuake>() {
                        @Override
                        public int compare(EarthQuake e1, EarthQuake e2) {
                            switch (orderBy){
                                case "time":
                                    return Long.compare(e1.getTime(), e2.getTime());

                                case "magnitude":
                                    return Double.compare(e1.getMagnitude(), e2.getMagnitude());
                            }
                            return 0;
                        }
                    });

                    recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(QuakesListActivity.this));

                    recyclerViewAdapter = new RecyclerViewAdapter(QuakesListActivity.this, quakeList);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    recyclerViewAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(jsonObjectRequest);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_min_magnitude_key)) ||
                key.equals(getString(R.string.settings_min_numbers_key)) ||
                key.equals(getString(R.string.settings_order_by_key))){

            getEarthQuakeList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
