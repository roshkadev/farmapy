package mobi.roshka.farmapy.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.roshka.farmapy.FarmaPYApplication;
import mobi.roshka.farmapy.R;
import mobi.roshka.farmapy.bean.Category;
import mobi.roshka.farmapy.bean.GeoPointParceable;
import mobi.roshka.farmapy.bean.Place;
import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.exception.AcataAppException;
import mobi.roshka.farmapy.utils.CategoryHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

public class PlaceActivity extends Activity {
	
	
	
	private ListView placeListView;
	private List<Place> places;
	private GeoPoint currentLocation;
	protected ProgressBar activityIndicator;

	private String categorySelected = CategoryHelper.CATEGORIES_ALL;
	private Map<Integer, Category> categoryMap = new HashMap<Integer, Category>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.places);
        //setupCommonComponents();
        
        this.activityIndicator = (ProgressBar) this.findViewById(R.id.commonActivityIndicator);
        
        placeListView = (ListView) this.findViewById(R.id.placesList);
        
        placeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent data = new Intent();
				data.putExtra("RETURNED_LOCATION", new GeoPointParceable(places.get(position).getGeoPoint()));
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
        
        GeoPointParceable gpp = getIntent().getParcelableExtra("CURRENT_LOCATION");
        currentLocation = gpp.getPoint();
        
        Log.d("PlacesActivity", "Launching GetPlacesTask task.");
		
		reload();
	}
	
	private void reload(){
		activityIndicator.setVisibility(View.VISIBLE);
		new  GetPlacesTask().execute();
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
		placeListView.setAdapter(new PlacesListAdapter());
	}
	
	
	public GeoPoint getCurrentLocation() {
		return currentLocation;
	}
	
	//========================  TAREA  =======================================

	private class GetPlacesTask extends AsyncTask<Void, Void, List<Place>> {
		private AcataAppException farmaPyEx;
		@Override
		protected List<Place> doInBackground(Void... params) {
			try {
//				if(categorySelected == CategoryHelper.CATEGORIES_ALL)
//					return CommManager.getPlaces(getCurrentLocation(),10,3000);
				
				return CommManager.getPlaces(getCurrentLocation(),10,-1, categorySelected);
				
			} catch (AcataAppException e) {
				Log.e("AccountActivity", "Error while retrieving places", e);
				//checkSessionStatus(e);
				farmaPyEx = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<Place> places) {
			Log.d("PlaceActivity", "Finished executing getPlaces()");
			activityIndicator.setVisibility(View.GONE);
			if(farmaPyEx!=null){
				Log.e("Places", "estamos en la B " + farmaPyEx.getMessage());
				return;
			}

			//this.places = places;
			setPlaces(places);
		}
		
	}
	
	//======================= LIST ADAPTER ====================================
	
	private class PlacesListAdapter extends BaseAdapter
	{
		private LayoutInflater layoutInflater;
		
		PlacesListAdapter()
		{
			layoutInflater = LayoutInflater.from(PlaceActivity.this);
		}

		@Override
		public int getCount() {
			if (getPlaces() != null)
				return getPlaces().size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (getPlaces() != null) {
				Place place = getPlaces().get(position);
				return place;
			}
			return null;
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.place_entry, parent, false);
			}
			TextView name 		= (TextView) convertView.findViewById(R.id.placeName);
			TextView distance	= (TextView) convertView.findViewById(R.id.distance);
			TextView address 	= (TextView) convertView.findViewById(R.id.placeAddress);
			TextView phone 		= (TextView) convertView.findViewById(R.id.placePhoneNumber);
			TextView description= (TextView) convertView.findViewById(R.id.placeDescription);

			distance.setVisibility(View.GONE);
			address.setVisibility(View.GONE);
			phone.setVisibility(View.GONE);
			description.setVisibility(View.GONE);
			
			final Place place = getPlaces().get(position);

			name.setText(place.getName());
			
			if(place.getDistance() != Place.NO_DISTANCE){
				distance.setVisibility(View.VISIBLE);
				int metros = place.getDistance().intValue();
				
				if(metros < 1000){
					distance.setText("(" + metros + " mts.)");
				}else{
					double km = ((double)metros)/1000;
					metros = (int)(km * 100);
					km = ((double)metros)/ 100;
					distance.setText("(" + km + " km.)");
				}
			}
			
			if(place.getAddress()!=null){
				address.setVisibility(View.VISIBLE);
				address.setText(place.getAddress());
			}
			
			if(place.getPhone()!=null){
				phone.setVisibility(View.VISIBLE);
				phone.setText(place.getPhone());
				
				phone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
						builder.setMessage("¿Está seguro que desea llamar a " + place.getDescription() + " ( "+ place.getPhone()+") ?")
							   .setTitle("FarmaPY")
						       .setCancelable(false)
						       .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
									    String url = "tel:"+ place.getPhone();// "tel:+595216171000";
									    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
									    startActivity(intent);
						           }
						       })
						       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                dialog.cancel();
						           }
						       });
						AlertDialog alert = builder.create();
						alert.show();
					}
				});
			}
			
			if(place.getDescription()!=null){
				description.setVisibility(View.VISIBLE);
				description.setText(place.getDescription());
			}
			
			return convertView;
		}
		
	}
	
//===================== MENU ===============================
	
	
	private final static int CATEGORY_MENU = 1;
	private void makeMenu(Menu menu){

		CategoryHelper categoryHelper = ((FarmaPYApplication)getApplication()).getCategoryHelper();
	    SubMenu smnu = menu.addSubMenu(Menu.NONE, CATEGORY_MENU, Menu.NONE, getString(R.string.categoriesTitleMenu));
		
	    categoryMap.clear();
	    int i = 0;
		for(Category c : categoryHelper.getCategories()){
			categoryMap.put(i, c);
			smnu.add(CATEGORY_MENU,i, Menu.NONE,c.getName());
			i++;
		}
		
		//seleccion exlusica -- radio buttons
		smnu.setGroupCheckable(CATEGORY_MENU, true, true);
	}
	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	     makeMenu(menu);
	     return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		item.setChecked(true);
		if(item.getGroupId() == CATEGORY_MENU){
			categorySelected = categoryMap.get(item.getItemId()).getCode();
			reload();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
}
