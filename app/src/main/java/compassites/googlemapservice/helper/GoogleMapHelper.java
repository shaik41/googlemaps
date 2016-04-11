package compassites.googlemapservice.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import compassites.googlemapservice.R;

/**
 * Created by shaikatif on 16/3/16.
 */
public class GoogleMapHelper {
    /**
     *
     * you can set marker icon here
     */
    private int markerIcon=R.drawable.map_marker;
    /**
     *
     * you can zoomLevel here
     */
    float zoomLevel = 15;

    public Marker addMarker(GoogleMap mGoogleMap,LatLng latLng){
      return mGoogleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(markerIcon)));
    }

    public String getAddressFromLatLng(LatLng latLng,Context context){

        List<Address> addresses;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            StringBuilder sb=new StringBuilder();
            int i=0;
            while (null!=addresses.get(0).getAddressLine(i)){
                sb.append(addresses.get(0).getAddressLine(i));
                sb.append(" ");
                i++;
            }
            return sb.toString();


        } catch (Exception e) {
            Log.d("GoogleMapHelper","Geocoder failed");
            return "";
        }


    }

    public void setMapToLocation(GoogleMap mGoogleMap,LatLng mLatLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mLatLng, zoomLevel);
        mGoogleMap.animateCamera(cameraUpdate);
    }
}
