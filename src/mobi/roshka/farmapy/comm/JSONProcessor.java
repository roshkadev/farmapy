package mobi.roshka.farmapy.comm;

import java.util.ArrayList;
import java.util.List;

import mobi.roshka.farmapy.FarmaPYApplication;
import mobi.roshka.farmapy.bean.Category;
import mobi.roshka.farmapy.bean.EmergencyNumber;
import mobi.roshka.farmapy.bean.Place;
import mobi.roshka.farmapy.bean.SanitaryAlert;
import mobi.roshka.farmapy.bean.User;
import mobi.roshka.farmapy.utils.CategoryHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class JSONProcessor {

	public static Place getPlace(String string) throws JSONException {
		return getPlace(new JSONObject(string));
	}

	public static Place getPlace(JSONObject json) throws JSONException {

		Place p = new Place();
		p.setName(json.getString("name"));
		p.setAddress(json.getString("address"));
		p.setGeoPoint(new GeoPoint(json.getInt("lat"), json.getInt("lon")));
		p.setPoints(json.getInt("points"));
		// p.setUpVotes(json.getInt("upVotes"));
		// p.setDownVotes(json.getInt("downVotes"));
		return p;
	}

	public static String getLoginId(String string) throws JSONException {
		JSONObject json = new JSONObject(string);
		return json.getString("id");
	}

	public static List<Place> getPlaces(String string) throws JSONException {
		return getPlaces(new JSONArray(string));
	}

	public static User getUser(String loginRes) throws JSONException {
		JSONObject jsonRes = new JSONObject(loginRes);

		User user = new User();
		user.setAcataAccessToken(jsonRes.getString("access_token"));

		JSONObject jsonUser = jsonRes.getJSONObject("user");
		user.setFbName(jsonUser.getString("name"));
		user.setFbNick(jsonUser.has("nick") ? jsonUser.getString("nick") : null);
		user.setFbPictureUrl(jsonUser.getString("picture"));
		user.setAcataUserId(jsonUser.getString("_id"));

		return user;
	}

	public static List<Place> getPlaces(JSONArray json) throws JSONException {
		List<Place> places = new ArrayList<Place>();

		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonPlace = (JSONObject) json.get(i);

			Place place = new Place();
			place.setPlaceId(jsonPlace.getString("placeId"));
			place.setName(jsonPlace.getString("name"));
			place.setAddress(jsonPlace.getString("address"));
			place.setDescription(jsonPlace.getString("description"));
			place.setGeoPoint(new GeoPoint(jsonPlace.getInt("lat"), jsonPlace.getInt("lon")));
			place.setPoints(jsonPlace.getInt("points"));
			
			try{
				place.setDistance(jsonPlace.getDouble("distance"));
			}catch (Exception e) {
				Log.w("JSONProcessor","No se puede obtener la distancia" + e.getMessage());
				place.setDistance(Place.NO_DISTANCE);
			}
			
			try {
				place.setProfilePicture(jsonPlace.getString("profilePicture"));
			} catch (Exception e) {
				//mientras no existe el profile picture nos callamos la excepcion. 
			}			
			//if (jsonPlace.getString("categoryId").compareTo("bus")!=0)
				place.setCategoryId(jsonPlace.getString("categoryId"));//use this to get the category later.
			//else
			//	place.setCategoryId("dru");//use this to get the category later.
			place.setProfilePicture("");

			places.add(place);
		}
		return places;
	}

	// used in categoryhelper.
	public static List<Category> getCategories(JSONArray json)
			throws JSONException {

		List<Category> categories = new ArrayList<Category>();

		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonCategory = (JSONObject) json.get(i);
			Category c = new Category();
			c.setCode(jsonCategory.getString("code"));
			c.setName(jsonCategory.getString("name"));
			c.setIcon(jsonCategory.getString("icon")); // url
			JSONArray subCats = jsonCategory.getJSONArray("subs");
			if (subCats != null) {
				List<Category> subCategories = getCategories(subCats);
				c.setSubCategories(subCategories);
			}
			categories.add(c);
		}
		return categories;
	}

	// ?????? NOTHING IS WHAT IT SEEMS.

	public static CategoryHelper loadCategories(String categoriesStr)
			throws JSONException {

		JSONArray a = new JSONArray(categoriesStr);

		return new CategoryHelper(a);

	}

	private static List<EmergencyNumber> getEmergencyNumbers(JSONArray json) throws JSONException {
		
		List<EmergencyNumber> ens = new ArrayList<EmergencyNumber>();
		
		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonEmergencyNumber = (JSONObject) json.get(i);
			
			EmergencyNumber en = new EmergencyNumber();
			try{		
				en.setEmergencyId(jsonEmergencyNumber.getInt("emergencyId"));
				en.setDescription(jsonEmergencyNumber.getString("description"));
				en.setNo01(jsonEmergencyNumber.getString("no01"));
				
				if(!jsonEmergencyNumber.isNull("no02"))
					en.setNo02(jsonEmergencyNumber.getString("no02"));
				
				if(!jsonEmergencyNumber.isNull("no03"))
					en.setNo03(jsonEmergencyNumber.getString("no03"));
				
				if(!jsonEmergencyNumber.isNull("additionalInfo"))
					en.setAdditionalInfo(jsonEmergencyNumber.getString("additionalInfo"));
				
				if(!jsonEmergencyNumber.isNull("type"))
					en.setType(jsonEmergencyNumber.getString("type"));
				
				if(!jsonEmergencyNumber.isNull("url"))
				en.setUrl(jsonEmergencyNumber.getString("url"));
				
				if(!jsonEmergencyNumber.isNull("ord"))
					en.setOrd(jsonEmergencyNumber.getInt("ord"));
				
				if(!jsonEmergencyNumber.isNull("active"))
					en.setActive(jsonEmergencyNumber.getBoolean("active"));
			}catch (JSONException e) {
				Log.e("JSONProcessor", "No se proceso un item del JSON"+ e.getMessage());
			}
			
			ens.add(en);
		}	
		return ens;
	}
	
	public static List<EmergencyNumber> getEmergencyNumbers(String string) throws JSONException {
		return getEmergencyNumbers(new JSONArray(string));
	}

	public static List<SanitaryAlert> getSanitaryAlert(String string) throws JSONException{
		return getSanitaryAlert(new JSONArray(string));
	}

	private static List<SanitaryAlert> getSanitaryAlert(JSONArray json) throws JSONException {
		List<SanitaryAlert> sal = new ArrayList<SanitaryAlert>();
		
		for (int i = 0; i < json.length(); i++) {
			JSONObject jsonSA = (JSONObject) json.get(i);
			
			SanitaryAlert sa = new SanitaryAlert();
			
			try{
				if(!jsonSA.isNull("sanitaryAlertId"))
					sa.setSanitaryAlertId(jsonSA.getInt("sanitaryAlertId"));
				
				if(!jsonSA.isNull("alertType"))
					sa.setAlertType(jsonSA.getString("alertType"));
				
				if(!jsonSA.isNull("description"))
					sa.setDescription(jsonSA.getString("description"));

				if(!jsonSA.isNull("additionalInfo"))
					sa.setAdditionalInfo(jsonSA.getString("additionalInfo"));

				if(!jsonSA.isNull("url"))
					sa.setUrl(jsonSA.getString("url"));

				if(!jsonSA.isNull("active"))
					sa.setActive(jsonSA.getBoolean("active"));

				if(!jsonSA.isNull("ord"))
					sa.setOrd(jsonSA.getInt("ord"));

				sal.add(sa);
			}catch (JSONException e) {
				Log.e("JSONProcessor", "No se proceso un item del JSON"+ e.getMessage());
			}
		}	
		return sal;

	}

}
