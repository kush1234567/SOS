package com.bawp.womensafety.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.bawp.womensafety.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.net.wifi.WifiConfiguration.Status.strings;

public class NearByPlaces extends AppCompatActivity {
    Button btFind;
    Spinner spType;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat=0;
    private double currentLng=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by_places);
        //initialize variable
        spType=findViewById(R.id.sp_type);
        btFind=findViewById(R.id.bt_find);
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        final String[] placeTypelist={"atm","bank","hospital"};
        String[] placeNameList={"ATM","Bank","Hospital"};
        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,placeNameList));
        //initialize Fused location provider client
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else {
            //requesting permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get seleced spinner position
                int i=spType.getSelectedItemPosition();
                //url
                String url="https://maps.googleapis.com/maps/api/place/nearbysearch/json"+//url
                "?location="+currentLat+","+currentLng+ //latitude and longitude
                "&radius=5000"+//nearby radius
                "&types="+placeTypelist[i]+//place type
                "&sensor=true"+//sensor
                "&key=" + getResources().getString(R.string.google_map_api);

                //execute place task method to download json data
                new PlaceTask().execute(url);

            }
        });

    }

    private void getCurrentLocation() {
        //initialize task location
        Task<Location>task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    currentLat=location.getLatitude();
                    currentLng=location.getLongitude();
                    //sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            //when map is ready
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLng),15));
                        }
                    });

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==44)
        {
          if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
          {
              getCurrentLocation();
          }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        //Initialize data

        protected String doInBackground(String... strings) {
            String data=null;
            try {
               data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }
        @Override
        protected void onPostExecute(String s) {
            new ParserTask().execute(s);

        }
    }

    private String downloadUrl(String string) throws IOException {
        //Initialize data
        URL url=new URL(string);
        //Initialize connection
        HttpURLConnection connection=(HttpURLConnection)url.openConnection();
        //connect connection
        InputStream stream=connection.getInputStream();
        //Initialize buffer reader
        BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
        //initialize string builder
        StringBuilder builder=new StringBuilder();
        //Initialize string value

         String line="";
         //use while loop
        while((line=reader.readLine())!=null)
        {
            //Append line
            builder.append(line);
        }
        //Get append data
        String data=builder.toString();
        //close reader
        reader.close();
        //return data
        return  data;
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class

            JsonParser jsonParser=new JsonParser();
            //initialize hash map List
            List<HashMap<String,String>>mapList= null;
            JSONObject object=null;
            try {
                object=new JSONObject(strings[0]);
                //parse json object
                mapList=jsonParser.parserResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();
            //use for loop
            for(int i=0;i<hashMaps.size();i++)
            {
                //Initialize hashMap
                HashMap<String,String>hashMapList=hashMaps.get(i);
                //get latitute
                double lat=Double.parseDouble(hashMapList.get("lat"));
                //get longitue
                double lng=Double.parseDouble(hashMapList.get("lng"));
                //get name
                String name=hashMapList.get("name");
                //concat lat lng
                LatLng latLng=new LatLng(lat,lng);
                //initialize marker
                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("name");
                map.addMarker(markerOptions);
            }

        }
    }
}
