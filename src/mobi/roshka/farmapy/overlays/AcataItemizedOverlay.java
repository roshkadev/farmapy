package mobi.roshka.farmapy.overlays;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class AcataItemizedOverlay extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> mOverlays = new ArrayList<CustomOverlayItem>();
	private Context context;
	public AcataItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView);
		//this.
		this.context = mapView.getContext();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
	
	public void addOverlay(CustomOverlayItem overlay) {
	    mOverlays.add(overlay);
	}
	
	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		/*
   	  OverlayItem item1 = mOverlays.get(index);
   	  AlertDialog.Builder dialog = new AlertDialog.Builder(context);
   	  dialog.setTitle(item1.getTitle());
   	  dialog.setMessage(item1.getSnippet());
  	  dialog.show();
		Toast.makeText(context, "onBalloonTap for overlay index " + index,
				Toast.LENGTH_LONG).show();
				*/
   	  return true;
   	}	
	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}
	public void doPopulate(){
	    populate();
	    setLastFocusedIndex(-1);
	}

	 public void removeOverlay(CustomOverlayItem itemizedOverlay2)
	 {
	 mOverlays.remove(itemizedOverlay2);
	 setLastFocusedIndex(-1);
	 populate();
	 }
	 public void clear() {
		 mOverlays.clear();
	 }	
}
