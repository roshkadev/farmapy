package mobi.roshka.farmapy;

import mobi.roshka.farmapy.bean.User;
import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.exception.AcataAppException;
import mobi.roshka.farmapy.utils.CategoryHelper;

import com.google.android.maps.GeoPoint;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class FarmaPYApplication extends Application {
	
	private String globalValue;
	private User user = null;
	private GeoPoint currentLocation;
	private CategoryHelper categoryHelper;
	/**
	 * AcataApplication to hold global variables
	 */
	
	private static FarmaPYApplication _instance;
	
	
	public static SharedPreferences getPrivatePreferences()
	{
		if (_instance != null) {
			return _instance.getSharedPreferences("userdetails", Context.MODE_PRIVATE);
		}
		return null;
	}
	
	public FarmaPYApplication()
	{
		_instance = this;
		setGlobalValue("");
		initCategoryHelper();
	}
	
	public static Context getContext()
	{
		return _instance;
	}

	private void initCategoryHelper() {

		new Thread() {
			public void run() {
				try {
					categoryHelper = CommManager.getCategories();
				} catch (AcataAppException e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	public String getGlobalValue() {
		return globalValue;
	}

	public void setGlobalValue(String globalValue) {
		this.globalValue = globalValue;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GeoPoint getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(GeoPoint currentLocation) {
		this.currentLocation = currentLocation;
	}

	public CategoryHelper getCategoryHelper() {
		return categoryHelper;
	}
	
	public boolean isLoggedIn(){
		
		return user!=null;
		
	}
}
