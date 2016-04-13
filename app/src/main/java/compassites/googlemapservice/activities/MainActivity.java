package compassites.googlemapservice.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import compassites.googlemapservice.R;
import compassites.googlemapservice.fragments.GoogleMapFragment;
import compassites.googlemapservice.interfaces.LocationSelected;

/**
 * Created by shaikatif on 11/4/16.
 */
public class MainActivity extends AppCompatActivity implements LocationSelected {

    private LocationRequest mLocationRequest;
    private GoogleMapFragment googleMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, getMapFragment()).commit();
    }

    private GoogleMapFragment getMapFragment() {
        /**
         * First specify type of location request you want and
         * add it to the bundle, you can also add many other stuffs like
         * mapmarker icon as per your requirement etc.
         */
        generateLocationRequest();
        Bundle args = new Bundle();
        args.putParcelable("locreq", mLocationRequest);
        googleMapFragment=new GoogleMapFragment();
        googleMapFragment.setArguments(args);
        return googleMapFragment;
    }

    private void generateLocationRequest() {
        mLocationRequest = new LocationRequest();
        /**
         * Interval to fire the location change
         */
        mLocationRequest.setInterval(600000);
        /**
         * MinimumDisplacement to fire the location,Please note it setSmallestDisplacement takes precedence over Time Interval.
         */
        mLocationRequest.setSmallestDisplacement(500);
        /**
         * Battery Usage Management PRIORITY_HIGH_ACCURACY,PRIORITY_BALANCED_POWER_ACCURACY,PRIORITY_LOW_POWER
         */
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        
        /** Mandatory
         * This is used to listen to users response when he is prompted to turn on gps
         * 
         */
        switch (requestCode){
            case GoogleMapFragment.RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        googleMapFragment.startLocationService();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void updateUI(String address) {
        Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
    }
}
