package mobi.roshka.farmapy.bean;

import com.google.android.maps.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;

public class GeoPointParceable implements Parcelable {

	private GeoPoint point;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(point.getLatitudeE6());
		dest.writeInt(point.getLongitudeE6());

	}
	
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<GeoPointParceable> CREATOR = new Parcelable.Creator<GeoPointParceable>() {
        public GeoPointParceable createFromParcel(Parcel in) {
            return new GeoPointParceable(in);
        }

        public GeoPointParceable[] newArray(int size) {
            return new GeoPointParceable[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private GeoPointParceable(Parcel in) {
        int lat = in.readInt();
        int log = in.readInt();
        
        point = new GeoPoint(lat, log);
    }

    
    public GeoPointParceable(GeoPoint gp) {
    	point = gp;
    }

	public GeoPoint getPoint() {
		return point;
	}
    
}
