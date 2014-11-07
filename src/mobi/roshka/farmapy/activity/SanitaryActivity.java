package mobi.roshka.farmapy.activity;

import java.util.List;

import mobi.roshka.farmapy.R;
import mobi.roshka.farmapy.bean.SanitaryAlert;
import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.exception.AcataAppException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SanitaryActivity extends Activity {
	private ListView sanitaryListView;
	private List<SanitaryAlert> sanitaryAlert;
	protected ProgressBar activityIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sanitary);
        //setupCommonComponents();

        this.activityIndicator = (ProgressBar) this.findViewById(R.id.commonActivityIndicator);      
        
        sanitaryListView = (ListView) this.findViewById(R.id.sanitaryList);

        Log.d("EmergencyActivity", "Launching GetEmergencyTask task.");
	
        reload();

	}
	
	private void reload(){
		activityIndicator.setVisibility(View.VISIBLE);
		new  GetSanitaryTask().execute();
	}

	public ListView getSanitaryListView() {
		return sanitaryListView;
	}

	public void setSanitaryListView(ListView sanitaryListView) {
		this.sanitaryListView = sanitaryListView;
	}

	public List<SanitaryAlert> getSanitaryAlert() {
		return sanitaryAlert;
	}

	public void setSanitaryAlert(List<SanitaryAlert> sanitaryAlert) {
		this.sanitaryAlert = sanitaryAlert;
		sanitaryListView.setAdapter(new SanitaryListAdapter());
	}

	//======================================================================

	private class GetSanitaryTask extends AsyncTask<Void, Void, List<SanitaryAlert>> {
		private AcataAppException farmaPyEx;
		@Override
		protected List<SanitaryAlert> doInBackground(Void... params) {
			try {
				return CommManager.getSanitaryAlert();
			} catch (AcataAppException e) {
				Log.e("AccountActivity", "Error while retrieving Sanitary Alerts", e);
				farmaPyEx = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<SanitaryAlert> sanitaryAlerts) {
			Log.d("sanitaryAlerts", "Finished executing sanitaryAlerts");
			if(farmaPyEx!=null){
				Log.e("Sanitary", "estamos en la B " + farmaPyEx.getMessage());
				return;
			}

			sanitaryAlert = sanitaryAlerts;
			setSanitaryAlert(sanitaryAlerts);
			
			activityIndicator.setVisibility(View.GONE);
		}
		
	}
	
	//======================================================================
	
	private class SanitaryListAdapter extends BaseAdapter
	{
		private LayoutInflater layoutInflater;
		
		SanitaryListAdapter()
		{
			layoutInflater = LayoutInflater.from(SanitaryActivity.this);
		}

		@Override
		public int getCount() {
			if (getSanitaryAlert() != null)
				return getSanitaryAlert().size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (getSanitaryAlert() != null) {
				SanitaryAlert sa = getSanitaryAlert().get(position);
				return sa;
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
				convertView = layoutInflater.inflate(R.layout.sanitary_entry, parent, false);
			}
			TextView description 	= (TextView) convertView.findViewById(R.id.sanitaryDescrip);
			TextView infoAdic 		= (TextView) convertView.findViewById(R.id.sanitaryInfoAdic);
			TextView url 			= (TextView) convertView.findViewById(R.id.sanitaryUrl);
			ImageView icon 			= (ImageView) convertView.findViewById(R.id.sanitaryIcon);

			infoAdic.setVisibility(View.GONE);
			url.setVisibility(View.GONE);

			final SanitaryAlert sa = getSanitaryAlert().get(position);
			description.setText(sa.getDescription());
			
			if(sa.getAdditionalInfo()!=null){
				infoAdic.setVisibility(View.VISIBLE);
				infoAdic.setText(sa.getAdditionalInfo());
			}
			
			if(sa.getUrl()!=null){
				url.setVisibility(View.VISIBLE);
				url.setText(sa.getUrl());
			}
			if(sa.getAlertType().equalsIgnoreCase("Epidemia")){
				icon.setImageResource(R.drawable.farma_py_alert_critical);
			}
			
			return convertView;
		}
		
	}
	
	


}
