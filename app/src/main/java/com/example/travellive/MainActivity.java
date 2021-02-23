package com.example.travellive;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static ArrayList<String> places = new ArrayList<String>();
    static  ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.travellive", Context.MODE_PRIVATE);


        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        places.clear();
        latitudes.clear();
        longitudes.clear();
        locations.clear();

        try {

            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<String>())));


        }catch (Exception e) {
            e.printStackTrace();
        }

        if (places.size() >0 && latitudes.size() > 0 && longitudes.size() > 0 ) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for (int i = 0; i < latitudes.size(); i++) {
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));

                }
            } else {
                places.add("Add a new place...");
                locations.add(new LatLng(0,0));
            }


        }


        if (!sharedPreferences.getBoolean("firstTime", false)) {

            places.add("Add a new place...");
            locations.add(new LatLng(0, 0));
            places.add("Galveston Texas");
            locations.add(new LatLng(29.2652, -94.8280));
            places.add("South Beach");
            locations.add(new LatLng(25.7826, -80.1341));
            places.add("Free College");
            locations.add(new LatLng(37.548285, -122.059662));
            places.add("Where I Grew Up ");
            locations.add(new LatLng(42.999304, -85.133894));
            places.add("Marco, Polo");
            locations.add(new LatLng(30.4515, -91.1871));
            places.add("Birthplace");
            locations.add(new LatLng(15.9117, -85.9534));


            // mark first time has ran.
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }



        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber",position);

                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                places.remove(i);
                locations.remove(i);
                arrayAdapter.notifyDataSetChanged();
                ArrayList<String> latitudes= new ArrayList<>();
                ArrayList<String> longitudes = new ArrayList<>();

                for(LatLng coord:MainActivity.locations){
                    latitudes.add(Double.toString(coord.latitude));
                    longitudes.add(Double.toString(coord.longitude));
                }
                try {
                    sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).commit();
                    sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitudes)).apply();
                    sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longitudes)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return false;
            }
        });

    }
}

