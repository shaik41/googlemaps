package compassites.googlemapservice.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import compassites.googlemapservice.helper.GoogleMapHelper;

public class GoogleMapFragment extends SupportMapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<LocationSettingsResult>
{

    public static final int RESOLUTION_REQUEST = 538;
    private int markerIcon;
    private Handler handler=new Handler();
    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;
    private GoogleMapHelper mGoogleMapHelper;
    private GoogleMap mGoogleMap;
    private Marker mUserMarker;
    private LatLng mCurrentLocationLatLng;

    public GoogleMapFragment(){

    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /**
         * Initialize the map when Fragment is attached to the relevant Activity
         */
        initMap();
    }

    private void initMap() {
        mGoogleMap = getMap();
        /**
         * When getMap() returns null, we wait until we get a map object
         */
        if (mGoogleMap == null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initMap();
                }
            }, 100);
            return;
        }
        /**
         * Google Maps provides many UI elements like zoom in zoom out buttons , you can hide or display them.
         */
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);

        /**
         * Add map listeners ,just added map click listener and map long click.
         */
        mGoogleMap.setOnMapClickListener(onMapClickListener);
        mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
        /**
         * Building Gooogle API cient : Below we are using LocationServices API to build, refer "https://developers.google.com/android/guides/api-client" for more info on this
         */
        mLocationClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mLocationRequest=getArguments().getParcelable("locreq");
        mLocationClient.connect();
        /**
         * Initialize the helper class
         */

        mGoogleMapHelper=new GoogleMapHelper();
    }

    @Override
    public void onConnected(Bundle bundle) {
        /**
         * Once Google API client is Connected,Check for GPS connectivity and generate a request
         */
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mLocationClient,
                        mLocationSettingsRequest
                );

        result.setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        /**
         * Disconnect the client
         */
        mLocationClient.disconnect();
    }

    /**
     * You can implement the location changed in your activity and use this as per your requirement .Below is just a sample code
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        /**
         * CallBack when location is changed, we just move map and add a marker here
         */
        mCurrentLocationLatLng=new LatLng(location.getLatitude(), location.getLongitude());
        mGoogleMapHelper.setMapToLocation(mGoogleMap,mCurrentLocationLatLng);
        Toast.makeText(getActivity(),mGoogleMapHelper.getAddressFromLatLng(mCurrentLocationLatLng,getActivity()),Toast.LENGTH_SHORT).show();
        mUserMarker=mGoogleMapHelper.addMarker(mGoogleMap,mCurrentLocationLatLng);
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                startLocationService();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                /**
                 * Location settings are not satisfied. But could be fixed by showing the user
                    a dialog.
                  */
                try {
                    /**
                     * Show the dialog by calling startResolutionForResult(),
                       and check the result in onActivityResult() in your Activity and call startLocationService
                     */
                    status.startResolutionForResult(getActivity(),RESOLUTION_REQUEST);

                } catch (IntentSender.SendIntentException e) {
                    /**
                     * Very Rare Exception
                     */
                    Log.d("GoogleServices", "SendIntent Failed");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                /**
                 *   Location settings are not satisfied. However, we have no way to fix the
                     settings so we won't show the dialog.
                 */
                Log.d("GoogleServices", "Settings Not Available");
                break;
        }

    }

    public void startLocationService() {
        /**
         * Below we are using Fused Location API , refer "http://www.androidwarriors.com/2015/10/fused-location-provider-in-android.html"
         */
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
    }

   GoogleMap.OnMapClickListener onMapClickListener=new GoogleMap.OnMapClickListener() {
       @Override
       public void onMapClick(LatLng latLng) {
           handleMapEvents(latLng);

       }
   };

    GoogleMap.OnMapLongClickListener onMapLongClickListener=new GoogleMap.OnMapLongClickListener() {


        @Override
        public void onMapLongClick(LatLng latLng) {
            handleMapEvents(latLng);

        }
    };

    GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener=new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            handleMapEvents(mCurrentLocationLatLng);
            return true;
        }
    };

        private void handleMapEvents(LatLng latLng){
        if(mUserMarker!=null){
            mUserMarker.remove();
        }
        mGoogleMapHelper.setMapToLocation(mGoogleMap,latLng);
        mUserMarker=mGoogleMapHelper.addMarker(mGoogleMap,latLng);
        Toast.makeText(getActivity(),mGoogleMapHelper.getAddressFromLatLng(latLng,getActivity()),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
