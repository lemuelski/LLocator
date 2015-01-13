package com.lemuel.llocator;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import volleyutil.RequestListener;
import volleyutil.UtilityClass;
import volleyutil.VolleyUtil;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

@SuppressLint("NewApi") 
public class MainActivity extends FragmentActivity implements OnMapReadyCallback, RequestListener, OnMyLocationChangeListener{
    
    private static final String url = "http://maps.googleapis.com/maps/api/directions/json?origin=Market%20Market%20Mall,%20Taguig,%20Metro%20Manila,%20Philippines,&destination=St.%20Luke%27s%20Medical%20Center,%2032nd%20Street,%20Taguig,%20Metro%20Manila,%20Philippines,&sensor=false&mode=transit&alternatives=true";
    private MapFragment mapFragment;
    private JSONObject response;
    private GoogleMap mMap;
    private int color[] = {Color.RED, Color.YELLOW, Color.GREEN, Color.WHITE};
    private Location myLocation;
    private boolean firstTime = true;
    private android.app.Fragment sample;
    private LinearLayout ll;
    
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleyUtil.init(this);
        setContentView(R.layout.activity_main);
        sample = new Sample(this);
        
        mapFragment = MapFragment.newInstance();
       
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        mapFragment.getMapAsync(this);
        LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        
        String provider = service.getBestProvider(criteria, false);
        myLocation = service.getLastKnownLocation(provider);
        
        ll = (LinearLayout)findViewById(R.id.layout2);
        fragmentTransaction.add(ll.getId(), sample);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(this);
        VolleyUtil.get().createRequest(url, this);
        googleMap.setMyLocationEnabled(true);
        if (myLocation!=null){
            Log.i("lem", "has last known");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));
        }
        googleMap.getMyLocation();
    }

    @Override
    public void onRequestDone() {
      response = VolleyUtil.get().getResponse();
      drawPath(response);
    }
    
    public void drawPath(JSONObject  result) {
        try {
               final JSONObject json = result;
               JSONArray routeArray = json.getJSONArray("routes");
              
        
                  JSONObject routes = routeArray.getJSONObject(0);
                  JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                  String encodedString = overviewPolylines.getString("points");
                 
                  
                  List<LatLng> list = UtilityClass.decodePoly(encodedString);

                  for(int z = 0; z<list.size()-1;z++){
                       LatLng src= list.get(z);
                       LatLng dest= list.get(z+1);
                       Polyline line = mMap.addPolyline(new PolylineOptions()
                       .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,dest.longitude))
                       .width(10)
                       .color(color[0]).geodesic(false));
                   }

                  JSONObject route = routeArray.getJSONObject(0);
                  JSONArray legs = route.getJSONArray("legs");
                  JSONObject leg = legs.getJSONObject(0);
                  JSONArray steps = leg.getJSONArray("steps");
                
                  for (int j=0; j <steps.length(); j++){
                      Log.i("lem", steps.length()+""+j);
                      JSONObject locArray = steps.getJSONObject(j).getJSONObject("start_location");
                      String desc = steps.getJSONObject(j).getString("html_instructions");
                      String formatted = Html.fromHtml(desc).toString();
                      Log.i("lem", formatted);
                      mMap.addMarker(new MarkerOptions().title("lol").snippet(formatted).position(new LatLng(locArray.getDouble("lat"), locArray.getDouble("lng"))));
                  }
         
        } 
        catch (JSONException e) {
            Toast.makeText(this, "No Route Found for Chosen Mode", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.i("lem", "location Changed");
        myLocation = location;
        if (firstTime){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));
            firstTime = false;
        }
    } 
    
    @Override
    public void onPress() {
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)ll.getLayoutParams();
//        params.weight = 90;
//        ll.setLayoutParams(params);
        android.app.FragmentManager fm = getFragmentManager();
        
        fm.beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .hide(sample)
        .commit();
    }

}
