package com.example.tareagooglemarcador;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button mapTypeButton, satelliteTypeButton, terrainTypeButton;
    private RequestQueue requestQueue;
    private HttpUtil httpUtil;
    private Marker selectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        requestQueue = Volley.newRequestQueue(this);
        httpUtil = new HttpUtil(requestQueue);
        mapTypeButton = findViewById(R.id.mapTypeButton);
        satelliteTypeButton = findViewById(R.id.satelliteTypeButton);
        terrainTypeButton = findViewById(R.id.terrainTypeButton);

        mapTypeButton.setOnClickListener(view -> changeMapType(GoogleMap.MAP_TYPE_NORMAL));
        satelliteTypeButton.setOnClickListener(view -> changeMapType(GoogleMap.MAP_TYPE_SATELLITE));
        terrainTypeButton.setOnClickListener(view -> changeMapType(GoogleMap.MAP_TYPE_TERRAIN));

    }

    private void changeMapType(int mapType) {
        if (mMap != null) {
            mMap.setMapType(mapType);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Nuevo Marcador"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));

            getPlaceDetails(latLng);
        });
    }

    private void getPlaceDetails(LatLng latLng) {
        String apiKey = "AIzaSyD0ONVovLBMhzWI2nU0XEkJguQO-y_cJrI";
        String nearbySearchUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latLng.latitude + "," + latLng.longitude +
                "&radius=1500&type=bar" +
                "&key=" + apiKey;

        httpUtil.sendJsonObjectRequest(nearbySearchUrl,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject firstResult = results.getJSONObject(0);
                            String placeId = firstResult.getString("place_id");
                            getPlaceDetailsFromId(placeId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.i("Error","Hubo un error");
                }
        );
    }

    private void getPlaceDetailsFromId(String placeId) {
        String apiKey = "AIzaSyD0ONVovLBMhzWI2nU0XEkJguQO-y_cJrI";
        String detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?" +
                "fields=name,rating,formatted_phone_number" +
                "&place_id=" + placeId +
                "&key=" + apiKey;

        httpUtil.sendJsonObjectRequest(detailsUrl,
                response -> {
                    try {
                        JSONObject result = response.getJSONObject("result");
                        String placeName = result.getString("name");
                        double rating = result.optDouble("rating", 0.0);
                        String phoneNumber = result.optString("formatted_phone_number", "N/A");
                        String photoReference = result.optString("photo_reference", "");

                        showPlaceInfoDialog(placeName, rating, phoneNumber, photoReference);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.i("Error","Hubo un error");
                }
        );
    }



    private void showPlaceInfoDialog(String placeName, double rating, String phoneNumber, String photoReference) {
        Context context = this;

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.adaptadorinfo, null);

        ImageView placeImageView = dialogView.findViewById(R.id.placeImageView);
        TextView placeNameTextView = dialogView.findViewById(R.id.placeNameTextView);
        TextView ratingTextView = dialogView.findViewById(R.id.ratingTextView);
        TextView phoneNumberTextView = dialogView.findViewById(R.id.phoneNumberTextView);

        placeNameTextView.setText(placeName);
        ratingTextView.setText("Rating: " + rating);
        phoneNumberTextView.setText("Teléfono: " + phoneNumber);

        if (!photoReference.isEmpty()) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=400" +
                    "&photo_reference=" + photoReference +
                    "&key=" + "AIzaSyD0ONVovLBMhzWI2nU0XEkJguQO-y_cJrI";

            Glide.with(context)
                    .load(photoUrl)
                    .into(placeImageView);
        } else {
            placeImageView.setVisibility(View.GONE);
            TextView noImageTextView = dialogView.findViewById(R.id.noImageTextView);
            noImageTextView.setVisibility(View.VISIBLE);
        }

        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        dialog.setCancelable(true);

        dialog.show();
    }
}
