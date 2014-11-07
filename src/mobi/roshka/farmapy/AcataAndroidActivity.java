package mobi.roshka.farmapy;

import java.util.HashMap;
import java.util.List;

import mobi.roshka.farmapy.activity.AboutActivity;
import mobi.roshka.farmapy.activity.EmergencyActivity;
import mobi.roshka.farmapy.activity.PillActivity;
import mobi.roshka.farmapy.activity.PlaceActivity;
import mobi.roshka.farmapy.activity.SanitaryActivity;
import mobi.roshka.farmapy.bean.Category;
import mobi.roshka.farmapy.bean.GeoPointParceable;
import mobi.roshka.farmapy.bean.Place;
import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.componet.FarmaPyMapView;
import mobi.roshka.farmapy.exception.AcataAppException;
import mobi.roshka.farmapy.overlays.AcataItemizedOverlay;
import mobi.roshka.farmapy.overlays.CustomOverlayItem;
import mobi.roshka.farmapy.util.ImageManager;
import mobi.roshka.farmapy.utils.CategoryHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class AcataAndroidActivity extends MapActivity {
	
	public static final String TAG = "AcataAndroidActivity";
	
	public static final int RESULT_FROM_NEARBY_LOCATIONS = 108;
	
	private ImageButton btnEmergencias, btnAcordarme, btnLugares, btnAlertas;
//	private TextView globalTextView;
//	private TextView teste;
	LinearLayout linearLayout;
	FarmaPyMapView mapView;
	private MapController mapControl;
	List<Overlay> mapOverlays;
	Drawable drawable;
	HashMap<String, AcataItemizedOverlay> itemizedOverlayHash;
	
	
	int requestCode;
	private LocationManager locationManager;
	private GeoUpdateHandler geoUpdateHandler;
	final private Criteria criteria = new Criteria();
	private String provider;
	private MyLocationOverlay myLocationOverlay;
	private GeoPoint currentLocation;
	
	//zoooming
	private Handler handler = new Handler();
	protected int lastZoomLevel;
	protected GeoPoint lastMapCenter;
	public static final int zoomCheckingDelay = 3000; // in ms
	
	private DisplayMetrics metrics = new DisplayMetrics();

	private Runnable zoomChecker = new Runnable()
	{
	    public void run()
	    { 
	    	
		    if(!isNetworkAvailable()){
		    	Toast.makeText(AcataAndroidActivity.this, getString(R.string.networkErrorNoNetwork) , Toast.LENGTH_LONG).show();
		    	return;
		    }

	    	String msg = "";
	    	float f = distanceBetween(lastMapCenter,mapView.getMapCenter());
	    	if (f>100){
	    		msg = "Se movió " + f + " metros";
	    	}
	        if(mapView.getZoomLevel() != lastZoomLevel ) {
	        	lastZoomLevel = mapView.getZoomLevel();
	        	msg += msg +"Nuevo zoom"+ lastZoomLevel;
	        }
	        	
		    if(!msg.equals("")){
	    		lastMapCenter = mapView.getMapCenter();
	        	lastZoomLevel = mapView.getZoomLevel();
	        	//Toast.makeText(AcataAndroidActivity.this, msg, Toast.LENGTH_LONG).show();
	        	Log.d("FB", msg);
        		new GetPlacesTask().execute();
	        }	        
	        
	        handler.removeCallbacks(zoomChecker); // remove the old callback
	        handler.postDelayed(zoomChecker, zoomCheckingDelay); // register a new one
	    }
	};

    public GeoPoint getCurrentLocation() {
		return currentLocation;
	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
          GCMRegistrar.register(this, Globals.GCM_PROJECT_ID);
        } else {
        	SharedPreferences pref = FarmaPYApplication.getPrivatePreferences();
        	if (pref != null) {
    			pref.edit().putString("prefs_googleid", regId).commit();
    		    GCMRegistrar.setRegisteredOnServer(this, true);
    		    Log.v("AcataAndroidActivity", "Already registered");
        	} else {
        		Log.d(TAG, "Warning! Preferences are null!");
        	}
        }
        
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        setContentView(R.layout.main);
        
        btnAcordarme = (ImageButton) this.findViewById(R.id.btnAcordarme);
        btnEmergencias = (ImageButton) this.findViewById(R.id.btnEmergencia);
        btnLugares = (ImageButton) this.findViewById(R.id.btnLugares);
        btnAlertas = (ImageButton) this.findViewById(R.id.btnAlertas);

       
        btnAcordarme.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(AcataAndroidActivity.this, PillActivity.class);
				startActivity(i);
			}
		});
        
        btnEmergencias.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Intent i = new Intent(AcataAndroidActivity.this, EmergencyActivity.class);
				startActivity(i);
			}
		});
                
        btnAlertas.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent i = new Intent(AcataAndroidActivity.this, InfoPlaceActivity.class);
				Intent i = new Intent(AcataAndroidActivity.this, SanitaryActivity.class);
				startActivity(i);
			}
		});
        
        
        btnLugares.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent i = new Intent(AcataAndroidActivity.this, InfoPlaceActivity.class);
				Intent i = new Intent(AcataAndroidActivity.this, PlaceActivity.class);
				GeoPoint lcl = getCurrentLocation();
				if (lcl == null) {
					lcl = mapView.getMapCenter();
				}
				i.putExtra("CURRENT_LOCATION", new GeoPointParceable(lcl));
				startActivityForResult(i, RESULT_FROM_NEARBY_LOCATIONS);
			}
		});        
        
        
        
        
        //Map initializations
        mapView = (FarmaPyMapView) findViewById(R.id.mapview);
    	
        // TODO: si hay tiempo, mover esto a otro lado
        mapView.setBuiltInZoomControls(false);
    	
    	int maxZoom = mapView.getMaxZoomLevel();
        int initZoom = maxZoom-2;
    	//int initZoom = 19;
        //int initZoom = calculateZoomLevel(metrics.widthPixels);
        Log.i("AcataAndroidActivity", "Estableciendo el nivel de zoom inicial a " + initZoom);
        mapControl = mapView.getController();
        mapControl.setZoom(initZoom);
        
        lastZoomLevel = initZoom;
        
        mapOverlays = mapView.getOverlays();
        
        itemizedOverlayHash = new HashMap<String, AcataItemizedOverlay>();
        
    	drawable = this.getResources().getDrawable(R.drawable.androidmarker);
    	
    	//Mark our current location
    	myLocationOverlay = new MyLocationOverlay(this, mapView);
    	mapOverlays.add(myLocationOverlay);

    	myLocationOverlay.runOnFirstFix(new Runnable() {
    		public void run() {
    			mapControl.animateTo(
    			  myLocationOverlay.getMyLocation());
    		 	}
    	});
    	
		//Location initializations
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		criteria.setAccuracy(Criteria.ACCURACY_FINE);               
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			currentLocation = new GeoPoint(lat, lng);
			((FarmaPYApplication)getApplication()).setCurrentLocation(currentLocation);
			mapControl.animateTo(currentLocation);
		} else {
			provider = LocationManager.GPS_PROVIDER;
	    	((FarmaPYApplication)getApplication()).setCurrentLocation(mapView.getMapCenter());
		}
    	
    	geoUpdateHandler = new GeoUpdateHandler();
    	
    	if(!isGpsAvailable()){
    		Toast.makeText(AcataAndroidActivity.this, getString(R.string.gpsErrorNoGps) , Toast.LENGTH_LONG).show();
    		mapControl.animateTo(new GeoPoint(-25282187,-57635034));
    	}
    	
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.acata_android, menu);
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CheckStartActivity","onActivityResult and resultCode = "+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
            case RESULT_FROM_NEARBY_LOCATIONS:
            	GeoPointParceable gpp = data.getParcelableExtra("RETURNED_LOCATION");
            	mapControl.animateTo(gpp.getPoint());
            	break;
            }
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.cmdAbout:
			Intent i = new Intent(AcataAndroidActivity.this, AboutActivity.class);
			startActivity(i);
    		break;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	return true;
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	// This sets the s key on the phone to toggle between satellite and map view
    // and the t key to toggle between traffic and no traffic view (traffic view
    // relevant only in urban areas where it is reported).
    
    public boolean onKeyDown(int keyCode, KeyEvent e){
        if(keyCode == KeyEvent.KEYCODE_S){
            mapView.setSatellite(!mapView.isSatellite());
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_T){
            mapView.setTraffic(!mapView.isTraffic());
//            mapControl.animateTo(gp);  // To ensure change displays immediately
        }
            return(super.onKeyDown(keyCode, e));
    }
    
	// Define a listener that responds to location updates
    public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			((FarmaPYApplication)getApplication()).setCurrentLocation(point);
			mapControl.animateTo(point);
	    	locationManager.removeUpdates(geoUpdateHandler);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
    
	@Override
	protected void onPause() {
		super.onPause();
    	locationManager.removeUpdates(geoUpdateHandler);
    	myLocationOverlay.disableCompass(); 
    	myLocationOverlay.disableMyLocation();
    	handler.removeCallbacks(zoomChecker); // stop the map from updating
	}

	protected int zoomLevelToDistance(int zl){
		
		Projection proj = mapView.getProjection();
		GeoPoint topLeft = proj.fromPixels(0, 0);

		GeoPoint bottomRight = proj.fromPixels(mapView.getWidth()-1, mapView.getHeight()-1);
		return (int) distanceBetween(topLeft,mapView.getMapCenter());
	}
	protected float distanceBetween(GeoPoint a, GeoPoint b){
		
		float[] results = new float[3];
		if(a==null || b==null ){
			return 100000000;
		}else
		Location.distanceBetween(a.getLatitudeE6()/1E6, a.getLongitudeE6()/1E6, b.getLatitudeE6()/1E6, b.getLongitudeE6()/1E6, results);
		return results[0];
	}

	private class GetPlacesTask extends AsyncTask<Void, Integer, List<Place>> {

		private AcataAppException acataEx;
		
		
		@Override
		//trying synchronized to avoid null pointers on categories
		synchronized protected List<Place> doInBackground(Void... params) {
			List<Place> plResults = null;
			try {
				
				int qty;
				if(lastZoomLevel >=18){
					qty = 0;
				}else{
					qty = 50;
				}	
				int distance = zoomLevelToDistance(lastZoomLevel);
				plResults = CommManager.getPlaces(AcataAndroidActivity.this.mapView.getMapCenter(),qty,distance);
				Log.d("FB","doinbackg - qty="+qty+",radio="+distance);
				AcataItemizedOverlay currentItemizedOverlay = null;
				CategoryHelper ch = ((FarmaPYApplication)getApplication()).getCategoryHelper();
				for(Place p : plResults){
					if(distanceBetween(AcataAndroidActivity.this.mapView.getMapCenter(), p.getGeoPoint())<=distance){
					Category cat = ch.findByCode(p.getCategoryId()); 
					p.setProfilePicture(cat.getCode());
					currentItemizedOverlay = itemizedOverlayHash.get(p.getCategoryId());
					if (currentItemizedOverlay == null){
						//cache images in background
						ImageManager.getDrawable(p.getCategoryId(),cat.getIcon()); 
					}						
				}				
				}				
			} catch (AcataAppException e) {
				Log.e("GetPlacesTask", "Error en GetPlacesTask", e);
				acataEx = e;
				
			}
			return plResults;
	    }	
		
		@Override
		synchronized protected void onPostExecute(List<Place> plResults){

			if (acataEx != null) {
				Toast.makeText(AcataAndroidActivity.this, "Error:" + acataEx.getMessage(), Toast.LENGTH_LONG).show();
				// goto is evil
				return;
			} 

			//in this event we can change interface elements
			AcataItemizedOverlay currentItemizedOverlay = null;
			//if there's no server, maybe there are no results
			int i = 0;
			if(plResults != null){
				//we'll clear every overlay 
				for(AcataItemizedOverlay aio : itemizedOverlayHash.values()){
					aio.clear();
				}
				//and remove it from the mapview
				mapOverlays.clear();
				//the only remaining overlay is MyLocationOverlay
				mapOverlays.add(myLocationOverlay);
				
				int maxDistance = zoomLevelToDistance(lastZoomLevel);
				//load each place to its own category overlay.
				for(Place p : plResults){
					float d = distanceBetween(AcataAndroidActivity.this.mapView.getMapCenter(), p.getGeoPoint());
					if(d<=maxDistance){

						currentItemizedOverlay = itemizedOverlayHash.get(p.getCategoryId());
						if (currentItemizedOverlay == null){
							// we've already cached the images in doInBackground
							drawable =  ImageManager.getDrawable(p.getCategoryId(),"");							
							if(drawable != null){
								Drawable drw = resize(drawable);
								currentItemizedOverlay = new AcataItemizedOverlay(drw, AcataAndroidActivity.this.mapView);
								itemizedOverlayHash.put(p.getCategoryId(), currentItemizedOverlay );
							}else{
								Log.d("FB","No encontró la imagen de "+ p.getName());
								drawable =  ImageManager.getDrawable(p.getCategoryId(),"");
							}


						}
						//load new place to the layer
						currentItemizedOverlay.addOverlay(
								new CustomOverlayItem(
										p.getGeoPoint(), 
										p.getName(), 
										p.getAddress(), 
										p.getProfilePicture()
								)
						);

					}
					i++;
				}
				//all layers are loaded, process them and load them into mapOverlay
				for(AcataItemizedOverlay aio : itemizedOverlayHash.values()){
					aio.doPopulate();
					mapOverlays.add(aio);
				}
				mapView.invalidate();//force map redrawing
				
				String msg = "Se vienen :" + plResults.size() + "pero aceptó" + i + " radio="+maxDistance+"metros";
				//Toast.makeText(AcataAndroidActivity.this, msg, Toast.LENGTH_LONG).show();
				Log.d("FB", msg);
			}				
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 0, geoUpdateHandler);
		myLocationOverlay.enableCompass(); 
		myLocationOverlay.enableMyLocation(); 
		boolean x = true;
		if(x)
			new GetPlacesTask().execute();
		else{//geo fix -57.653 -25.289405 4000
			GeoPoint point2 = new GeoPoint((int)(-25.289405*1E6),(int)(-57.653503 *1E6));
			CustomOverlayItem overlayItem2 = new CustomOverlayItem(point2, "GoldenEye (1995)", 
					"(Interiors Russian defence ministry council chambers in St Petersburg)", 
					"http://ia.media-imdb.com/images/M/MV5BMzk2OTg4MTk1NF5BMl5BanBnXkFtZTcwNjExNTgzNA@@._V1._SX40_CR0,0,40,54_.jpg");
			drawable = getResources().getDrawable(R.drawable.androidmarker);
			AcataItemizedOverlay itemizedOverlay2 = new AcataItemizedOverlay(drawable, mapView);
			itemizedOverlay2.addOverlay(overlayItem2);
			point2 = new GeoPoint((int)(-25.28*1E6),(int)(-57.653 *1E6));
			overlayItem2 = new CustomOverlayItem(point2, "GoldenEye (1995)", 
					"(Interiors Russian defence ministry council chambers in St Petersburg)", 
					"http://ia.media-imdb.com/images/M/MV5BMzk2OTg4MTk1NF5BMl5BanBnXkFtZTcwNjExNTgzNA@@._V1._SX40_CR0,0,40,54_.jpg");
			itemizedOverlay2.addOverlay(overlayItem2);
			mapOverlays.add(itemizedOverlay2);

		}
		handler.postDelayed(zoomChecker, zoomCheckingDelay);
	}
    
	private Drawable resize(Drawable image) {
		int dpi = metrics.densityDpi;
		
		int scale = 100;
		if(dpi<160){
			scale = 15;
		}else if(dpi<160){
			scale = 30;
		}else if(dpi<200){
			scale = 50;
		}
		
//		if(lastZoomLevel < 17){
//			scale = (int)(scale * (0.6));
//		}
			
		
	    Bitmap d = ((BitmapDrawable)image).getBitmap();
	    Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, scale, scale, false);
	    return new BitmapDrawable(bitmapOrig);
	}

	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	private boolean isGpsAvailable() {
		LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	    return manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER);
	}

	private int calculateZoomLevel(int screenWidth) {
	    double equatorLength = 40075004; // in meters
	    double widthInPixels = screenWidth;
	    double metersPerPixel = equatorLength / 256;
	    int zoomLevel = 1;
	    while ((metersPerPixel * widthInPixels) > 2000) {
	        metersPerPixel /= 2;
	        ++zoomLevel;
	    }
	    Log.i("ADNAN", "zoom level = "+zoomLevel);
	    return zoomLevel;
	    

	}
	
}
