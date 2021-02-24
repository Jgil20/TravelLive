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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static ArrayList<String> places = new ArrayList<String>();
    static  ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;
    static  ArrayList<String> latitudes = new ArrayList<>();
    static  ArrayList<String> longitudes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);


           // Creates Local Storage
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.travellive", Context.MODE_PRIVATE);

                              // Clears array to get ready for Shared Preference
          places.clear();
          latitudes.clear();
        longitudes.clear();
        locations.clear();

        try {

                                      // Sink the Array List with the Local Storage
            places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(new ArrayList<String>())));


        }catch (Exception e) {
            e.printStackTrace();
        }
                                              // check if not empty and all 3 arrays are synced
        if (places.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0 ) {
            if (places.size() == latitudes.size() && places.size() == longitudes.size()) {
                for (int i = 0; i < latitudes.size(); i++) {
                    // adds to location array because ObjectSerializer converts it to a String
                    locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(longitudes.get(i))));

                }
            }
        }else {
                           // If first time adds these to the array but not the Shared Preferences

            places.add("Add a new place...");
            locations.add(new LatLng(0,0));
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
        }

                       // view and array
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,places);

        listView.setAdapter(arrayAdapter);

                                      // click see map
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("placeNumber",position);

                startActivity(intent);
            }
        });
                                             // Click need to Delete errors are Index out of bounds I think the arrays and Shared Preferences need to sync
        //
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                places.remove(i);
                locations.remove(i);

                arrayAdapter.notifyDataSetChanged();

                                                              // break down to store in shared Preferences
                for(LatLng coord:MainActivity.locations){
                    latitudes.add(Double.toString(coord.latitude));
                    longitudes.add(Double.toString(coord.longitude));
                }

                try {
                                                                             //trying to update shared preferences with out the deleted values

                    places = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(places)));
                    latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("lats",ObjectSerializer.serialize(latitudes)));
                    longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longs",ObjectSerializer.serialize(longitudes)));


            }catch (IOException e) {
                e.printStackTrace();
            }

                Toast.makeText(getApplicationContext(),"Location Deleted!",Toast.LENGTH_SHORT).show();
                return false;
            }

        });

    }
}
