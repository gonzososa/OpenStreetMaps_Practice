package osmdroidemo.examples.gonzaloantonio.gmail.com.osmdroiddemo;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements LocationListener {
    LocationManager locationManager =  null;
    ResourceProxy resourceProxy;
    MapView mapView;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resourceProxy = new DefaultResourceProxyImpl (getApplicationContext ());
        setContentView (R.layout.activity_main);

        mapView = (MapView) findViewById (R.id.mapView);
        mapView.setTileSource (TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls (true);
        mapView.setMultiTouchControls (true);
        mapView.getController().setZoom (16);
        mapView.getController().setCenter (new GeoPoint (19.4326, -99.1332));

        MinimapOverlay minimapOverlay = new MinimapOverlay (this, mapView.getTileRequestCompleteHandler ());
        minimapOverlay.setZoomDifference (5);
        minimapOverlay.setHeight (150);
        minimapOverlay.setWidth (150);

        mapView.getOverlays().add (minimapOverlay);
        mapView.invalidate ();
    }

    @Override
    public void onPause () {
        if (locationManager != null) {
            locationManager.removeUpdates (this);
        }
        super.onPause();
    }

    @Override
    public void onResume () {
        if (locationManager != null && !provider.equals ("")) {
            locationManager.requestLocationUpdates (
                    provider,
                    5000,
                    10,
                    this);
        }
        super.onResume ();
    }

    @Override
    public void onStop () {
        if (locationManager != null) {
            locationManager.removeUpdates (this);
        }
        super.onStop ();
    }

    public void position_Click (View v) {
        getLocation();
    }

    private void alertBox (String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (message)
                .setCancelable (false)
                .setTitle (title)
                .setPositiveButton ("Location On", new DialogLocationClickHandler (this, true))
                .setNegativeButton ("Cancel", new DialogLocationClickHandler (this, false));

        AlertDialog alertDialog = builder.create ();
        alertDialog.show();
    }

    private void getLocation () {
        locationManager = (LocationManager) getSystemService (LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            alertBox ("Location is disabled", "Please enabled it and try again!");
            return;
        }


        provider = isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;

        Location location = locationManager.getLastKnownLocation (provider);
        if (location != null) {
            settingUpLocation (location);
            return;
        }

        locationManager.requestLocationUpdates (provider, 5000, 10, this);
    }

    @Override
    public void onLocationChanged (Location location) {
        settingUpLocation(location);
        locationManager.removeUpdates (this);
    }

    @Override
    public void onProviderEnabled (String provider) {

    }

    @Override
    public void onProviderDisabled (String provider) {

    }

    @Override
    public void onStatusChanged (String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void settingUpLocation (Location location) {
        Toast.makeText (this, "Location changed: Lat " + location.getLatitude () +
                        " Log " + location.getLongitude (), Toast.LENGTH_LONG).show();

        mapView.getController().setCenter(new GeoPoint(location));
        mapView.getController().setZoom (16);

        if (mapView.getOverlays().size() > 1)
            mapView.getOverlays().remove (1);

        OverlayItem item = new OverlayItem ("Here", "Here", new GeoPoint (location));

        ArrayList<OverlayItem> overlayItemsArray = new ArrayList<>();
        overlayItemsArray.add (item);

        mapView.getOverlays().add (new ItemizedOverlayWithFocus<>(this, overlayItemsArray, null));
    }
}
