package mobi.roshka.farmapy.comm;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mobi.roshka.farmapy.bean.EmergencyNumber;
import mobi.roshka.farmapy.bean.Place;
import mobi.roshka.farmapy.bean.SanitaryAlert;
import mobi.roshka.farmapy.bean.User;
import mobi.roshka.farmapy.exception.AcataAppException;
import mobi.roshka.farmapy.search.Filter;
import mobi.roshka.farmapy.utils.CategoryHelper;

import org.json.JSONException;

import android.os.Build;
import android.util.Log;

import com.google.android.maps.GeoPoint;



/**
 * Esta clase va a manejar todo lo relacionado con la comunicación entre el
 * dispositivo y el GateWay. Es una abtracción de la comunicación HTTP y del
 * procesamiento de la respuesta (JSON)
 * 
 * @author Pablo Welti
 * 
 * 
 */
public class CommManager {
	
	private static final String URL_BASE = "http://www.roshka.mobi/AcataAPI/api/";
	
	public static String gcmRegister(String regId)
		throws AcataAppException
	{
		String ret = HttpConnManager.get(URL_BASE + "gcm/register?regId=" + regId);
		Log.d("CommManager", "When registered " + regId + " got response: " + ret);
		return ret;
	}
	
	public static String gcmUnregister(String regId)
		throws AcataAppException
	{
		String ret = HttpConnManager.get(URL_BASE + "gcm/unregister?regId=" + regId);
		Log.d("CommManager", "When unregistered " + regId + " got response: " + ret);
		return ret;
	}
	
	public static Place getPlace(String placeId) 
		throws AcataAppException
	{
		//Map<String,String> params = new HashMap<String,String> 
		String string = HttpConnManager.get(URL_BASE + "place/" + placeId);
		try {
			return JSONProcessor.getPlace(string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new AcataAppException();
		}
	}
	
	
	public static CategoryHelper getCategories() throws AcataAppException {
		
		String string = HttpConnManager.get(URL_BASE + "categories/");		
		
		try {
			return JSONProcessor.loadCategories(string);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new AcataAppException("Could not load categories", e);
		}
		
	}
	
	public static List<EmergencyNumber> getEmergencyNumbers()throws AcataAppException{
		String url = URL_BASE + "emergencies";

		String string = HttpConnManager.get(url);		
		
		try {
			return JSONProcessor.getEmergencyNumbers(string);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new AcataAppException("Could not get Emergency numbers", e);
		}
		
	}
	
	public static List<SanitaryAlert> getSanitaryAlert()throws AcataAppException{
		String url = URL_BASE + "sanitary_alerts";

		String string = HttpConnManager.get(url);		
		
		try {
			return JSONProcessor.getSanitaryAlert(string);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new AcataAppException("Could not get Emergency numbers", e);
		}
		
	}

	
	@SuppressWarnings("unchecked")
	public static List<Place> getPlaces(Filter filters) throws AcataAppException{
		String url = URL_BASE + "search";
		String result;
		
		if(!filters.isEmpty()){
			Hashtable<String, String> params = new Hashtable();
			if(filters.getName()!=null && filters.getName()!=""){
				params.put("q", filters.getName());
			}
			if(filters.getDistance()!=0){
				params.put("r", ""+filters.getDistance());
			}
			if(filters.getCategory()!=null && filters.getCategory() != ""){
				params.put("c", filters.getCategory());
			}
			result = HttpConnManager.get(url, params);
		}else{
			result = HttpConnManager.get(url);
		}
		
		try {
			return JSONProcessor.getPlaces(result);
		} catch (JSONException e) {
			throw new AcataAppException();
		}
	}
	
	public static List<Place> getPlaces(GeoPoint point, int quantity, int distance, String categoryCode) throws AcataAppException{
		
		Map<String, String> params = new HashMap<String, String>();
		String string; 
		if(point != null){
			params.put("lat", "" + point.getLatitudeE6());			
			params.put("lon", "" +point.getLongitudeE6());			
		}
		if(quantity != -1);
			params.put("qty", "" +quantity);
		
		if(distance != -1)
			params.put("dist", "" +distance);
		
		if(categoryCode!=null && !categoryCode.equals(CategoryHelper.CATEGORIES_ALL))
			params.put("category_id", categoryCode);
		
		string = HttpConnManager.get(URL_BASE + "search",params);
		try {
			return JSONProcessor.getPlaces(string);
		} catch (JSONException e) {
			throw new AcataAppException("getPlaces:El servidor respondió  con información no válida.");
		}
		
	}
	
	public static List<Place> getPlaces(GeoPoint point, int quantity, int distance) throws AcataAppException{
		return getPlaces(point, quantity, distance, CategoryHelper.CATEGORIES_ALL);
//		Map<String, String> params = new HashMap<String, String>();
//		String string; 
//		if(point != null){
//			params.put("lat", "" + point.getLatitudeE6());			
//			params.put("lon", "" +point.getLongitudeE6());			
//		}
//		params.put("qty", "" +quantity);
//		params.put("dist", "" +distance);
//		string = HttpConnManager.get(URL_BASE + "search",params);
//		try {
//			return JSONProcessor.getPlaces(string);
//		} catch (JSONException e) {
//			throw new AcataAppException("getPlaces:El servidor respondió  con información no válida.");
//		}
	}
	
	public static String registerDevice(String androidId, String udid, String timestamp, String ssign) throws AcataAppException{
				
		Map<String, String> params = new HashMap<String, String>();
		//params.put("client_version", FBConnectActivity.CLIENT_VERSION);
		//params.put("platform",FBConnectActivity.PLATFORM_NAME);
		params.put("brand", Build.BRAND);
		params.put("model", Build.DEVICE);
		params.put("name", Build.MANUFACTURER);
		params.put("os_version", android.os.Build.VERSION.SDK_INT+"");
		params.put("udid", udid);
		params.put("time_stamp", timestamp);
		params.put("ssign", ssign);		
		
		String res = HttpConnManager.post(URL_BASE+"devices", params);
		
		String id = null;
		
		try {
			id = JSONProcessor.getLoginId(res);
		} catch (JSONException e) {
			throw new AcataAppException("No se pudo registrar el dispositivo. ",e);
		}
		
		System.out.println("Respuesta Servidor: "+res);
			
		return id;

   }
	public static User login(String deviceId, String accessToken, String udid, String ssign, String timestamp)  throws AcataAppException{
				
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", deviceId);
		//params.put("client_version",FBConnectActivity.CLIENT_VERSION);
		//params.put("platform", FBConnectActivity.PLATFORM_NAME);
		params.put("ext_access_token", accessToken);
		params.put("time_stamp", timestamp);
		params.put("os_version", android.os.Build.VERSION.SDK_INT+"");
		params.put("udid", udid);
		params.put("ssign", ssign);		
		
		String res = HttpConnManager.post(URL_BASE+"login/facebook", params);
		System.out.println(res);
		
		User user = null;
		try {
			user = JSONProcessor.getUser(res);
		} catch (JSONException e) {
			throw new AcataAppException("Error durante el login.", e);
		}
		return user;
	}
	public static Place addPlace(Place p) throws AcataAppException {
		
		/*	name
			description
			address
			lat
			lon
			category_id
			owner_id
		 */
		
		Map <String, String> params = new HashMap<String, String>();
		
		params.put("name", p.getName());
		params.put("description", null);
		params.put("address", p.getAddress());
		params.put("lat", "" +p.getGeoPoint().getLatitudeE6());
		params.put("lon", "" + p.getGeoPoint().getLongitudeE6());
		params.put("category_id", p.getCategory().getCode());
		params.put("owner_id", null);		
		
		String string = HttpConnManager.post(URL_BASE + "place/", params);
		try {
			return JSONProcessor.getPlace(string);
		} catch (JSONException e) {
			throw new AcataAppException();
		}		

	}
	
//	public static void main(String[] args) {
////		Place p;
//		List<Place> places;
//		try {
////			p = CommManager.getPlace("4f522e0a4e0883c69a60939c");
////			System.out.println(p.getName());
//			
//			places = CommManager.getPlaces();
//			System.out.println(places.get(0).getName());			
//
//		} catch (AcataAppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
