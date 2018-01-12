package com.algonquincollege.trop0008.doorsopenottawa;

import android.app.Activity;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquincollege.trop0008.doorsopenottawa.model.BuildingPOJO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Locale;
/**
 * Open Doors Ottawa App
 *
 * @author Marjan Tropper (trop0008@algonquinlive.com)
 */

public class DetailBuildingActivity extends Activity implements OnMapReadyCallback {

    private TextView  tvName;
    private TextView  tvCategory;
    private TextView  tvHours;
    private TextView  tvDescription;
    private ImageView buildingImage;
    private LatLng mLatlng;
    private String mTitle ;
    private String mHours;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_detail);
        Geocoder mGeocoder = new Geocoder(this, Locale.CANADA);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        
        BuildingPOJO selectedBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (selectedBuilding == null) {
            throw new AssertionError("Null data item received!");
        }

        tvName = (TextView) findViewById(R.id.tvName);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        buildingImage = (ImageView) findViewById(R.id.buildingImage);
        tvHours= (TextView) findViewById(R.id.tvHours) ;
        mHours="Hours:\n";
        if (selectedBuilding.getSaturdayStart()!= null){
            mHours= mHours +selectedBuilding.getSaturdayStart() + " to " + selectedBuilding.getSaturdayClose().replaceAll("2017-06-03 ","");
        }
        if (selectedBuilding.getSundayStart()!= null){
            mHours= mHours + "\n" +selectedBuilding.getSundayStart() + " to " + selectedBuilding.getSundayClose().replaceAll("2017-06-04 ","");
        }

        tvHours.setText(mHours);
        tvName.setText(selectedBuilding.getNameEN());
        tvCategory.setText(selectedBuilding.getCategoryEN());
        tvDescription.setText(selectedBuilding.getDescriptionEN());
        mTitle = selectedBuilding.getAddressEN();
        //FIXME :: LOCALHOST
        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + selectedBuilding.getBuildingId() + "/image";
        //url = "http://10.0.2.2:3000/buildings/" + selectedBuilding.getBuildingId() + "/image";
        Picasso.with(this)
                .load(Uri.parse(url))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimagefound)
                .fit()
                .into(buildingImage);
        mLatlng = new LatLng(selectedBuilding.getLatitude(), selectedBuilding.getLongitude());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        Marker mMarker = mMap.addMarker(new MarkerOptions().position(mLatlng).title(mTitle));
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(mLatlng, 15.F) );
        mMarker.showInfoWindow();
    }
}