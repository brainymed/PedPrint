package com.pedprint;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class TrackMeActivity extends Activity {

	

	protected CheckBox checkbox;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_me);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_track_me, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_view_update:
			updateMapType();
			return true;
		default:
			return false;
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		locationSettingsEnabled();
	}
	
	private boolean locationSettingsEnabled(){
		try{
			LocationManager locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			final boolean gpsEnabled = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
			System.out.println("GPS ENABLED? : " + gpsEnabled);
			if(!gpsEnabled){
				Dialog dialog = createLocationSettingsDialog();
				dialog.show();
			}
			return true;
		} catch (Exception ex){
			String errorMsg = ex.getMessage();
			System.out.println(errorMsg);
			Log.e("TrackMeActivity", errorMsg);
		}
		return false;
	}
	
    public Dialog createLocationSettingsDialog() {
        View neverShowCheckbox = View.inflate(this, R.layout.never_show_checkbox, null);
        checkbox = (CheckBox) neverShowCheckbox.findViewById(R.id.checkbox);
 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(neverShowCheckbox)
               .setTitle(R.string.location_settings_title)
               .setMessage(R.string.location_instructions)
               .setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       if (checkbox.isChecked()) {
                           doNotShowAgain();
                       }
                       Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                       startActivity(myIntent);
                   }
               })
               .setNegativeButton(R.string.location_skip, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       if (checkbox.isChecked()) {
                           doNotShowAgain();
                       }
                   }
               });
        return builder.create();
    }
    private void doNotShowAgain() {
        // persist shared preference to prevent dialog from showing again.
        Log.d("MainActivity", "TODO: Persist shared preferences.");
    }
    
    @SuppressLint("NewApi")
	public void updateLocation(View view){
    	if(locationSettingsEnabled()){
			GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mainMapFragment)).getMap();
			LocationManager locMgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			
			LocationProvider locProv = locMgr.getProvider(LocationManager.GPS_PROVIDER);
			Criteria locCriteria = new Criteria();
			locCriteria.setAccuracy(Criteria.ACCURACY_FINE);
			locCriteria.setCostAllowed(false);
			
			String providerName = locMgr.getBestProvider(locCriteria, true);
			
			if(providerName != null){
				Location loc = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				LatLng latLong = new LatLng(loc.getLatitude(), loc.getLongitude());
				map.addMarker(new MarkerOptions().position(latLong).title(System.currentTimeMillis()/1000 + " "));
				GoogleMap.CancelableCallback callback = new CancelableCallback() {
					
					@Override
					public void onFinish() {
						GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mainMapFragment)).getMap();
						map.animateCamera(CameraUpdateFactory.zoomTo(17), 3000, null);
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
					}
				};
				map.animateCamera(CameraUpdateFactory.newLatLng(latLong), 3000, callback);
			}
    	} else {
    		Log.e("TrackMeActivity", "TODO: Throw dialog saying you can't update location without GPS enabled");
    	}
	}
	
	@SuppressLint("NewApi")
	public void updateMapType(){
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mainMapFragment)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	}
	

}
