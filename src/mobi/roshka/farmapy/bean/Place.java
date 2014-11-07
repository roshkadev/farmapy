package mobi.roshka.farmapy.bean;

import java.io.InputStream;

import com.google.android.maps.GeoPoint;

public class Place {

	public static final double NO_DISTANCE = -1.0;
	
	private String placeId;
	private String name;
	private String description;
	private GeoPoint geoPoint;
	private Category category;
	private String categoryId;
	private String address;
	private String phone;
	private int points;
	private String profilePicture;
	private Double distance; 
	
	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	private PlaceExtendedInfo extendedInfo;
	
	public Place() {
		// TODO Auto-generated constructor stub
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public PlaceExtendedInfo getExtendedInfo() {
		return extendedInfo;
	}

	public void setExtendedInfo(PlaceExtendedInfo extendedInfo) {
		this.extendedInfo = extendedInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	public InputStream getPicture() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
 
}
