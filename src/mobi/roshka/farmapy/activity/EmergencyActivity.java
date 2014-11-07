package mobi.roshka.farmapy.activity;

import java.util.List;

import mobi.roshka.farmapy.AcataAndroidActivity;
import mobi.roshka.farmapy.R;
import mobi.roshka.farmapy.bean.EmergencyNumber;
import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.exception.AcataAppException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyActivity extends Activity {
	private ListView emergencyListView;
	private List<EmergencyNumber> emergenciesNumber;
	protected ProgressBar activityIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency);
        //setupCommonComponents();
        
        this.activityIndicator = (ProgressBar) this.findViewById(R.id.commonActivityIndicator);         
        emergencyListView = (ListView) this.findViewById(R.id.emergencyList);

        Log.d("EmergencyActivity", "Launching GetEmergencyTask task.");
		reload();
	}
	
	private void reload(){
		activityIndicator.setVisibility(View.VISIBLE);
		new  GetEmergenciesTask().execute();
	}

	public ListView getEmergencyListView() {
		return emergencyListView;
	}

	public void setEmergencyListView(ListView emergencyListView) {
		this.emergencyListView = emergencyListView;
		
	}

	public List<EmergencyNumber> getEmergenciesNumber() {
		return emergenciesNumber;
	}

	public void setEmergenciesNumber(List<EmergencyNumber> emergenciesNumber) {
		this.emergenciesNumber = emergenciesNumber;
		
		emergencyListView.setAdapter(new EmergencyListAdapter());
		//accountsListView.setVisibility(View.VISIBLE);

	}
	
	
	//======================================================================
	
	private class GetEmergenciesTask extends AsyncTask<Void, Void, List<EmergencyNumber>> {
		private AcataAppException farmaPyEx;
		@Override
		protected List<EmergencyNumber> doInBackground(Void... params) {
			try {
				return CommManager.getEmergencyNumbers();
			} catch (AcataAppException e) {
				Log.e("AccountActivity", "Error while retrieving emergencies", e);
				//checkSessionStatus(e);
				farmaPyEx = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(List<EmergencyNumber> emergencies) {
			Log.d("EmergencyActivity", "Finished executing getEmergencies()");
			activityIndicator.setVisibility(View.GONE);
			if(farmaPyEx!=null){
//				showMessage(ErrorHandling.getUiMessage(farmaPyEx.getType(), farmaPyEx.getCode()));
				Log.e("Emergency", "estamos en la B " + farmaPyEx.getMessage());
				return;
			}

			emergenciesNumber = emergencies;
			setEmergenciesNumber(emergencies);
		}
		
	}
	
	//======================================================================
	
	private class EmergencyListAdapter extends BaseAdapter
	{
		private LayoutInflater layoutInflater;
		
		EmergencyListAdapter()
		{
			layoutInflater = LayoutInflater.from(EmergencyActivity.this);
		}

		@Override
		public int getCount() {
			if (getEmergenciesNumber() != null)
				return getEmergenciesNumber().size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (getEmergenciesNumber() != null) {
				EmergencyNumber en = getEmergenciesNumber().get(position);
				return en;
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
				convertView = layoutInflater.inflate(R.layout.emergency_entry, parent, false);
			}
			TextView description 	= (TextView) convertView.findViewById(R.id.emergDescrip);
			TextView number01 		= (TextView) convertView.findViewById(R.id.emergNro01);
			TextView number02 		= (TextView) convertView.findViewById(R.id.emergNro02);
			TextView number03 		= (TextView) convertView.findViewById(R.id.emergNro03);
			TextView infoAdic 		= (TextView) convertView.findViewById(R.id.emergInfoAdic);
			TextView url 			= (TextView) convertView.findViewById(R.id.emergUrl);

			description.setVisibility(View.GONE);
			number01.setVisibility(View.GONE);
			number02.setVisibility(View.GONE);
			number03.setVisibility(View.GONE);
			infoAdic.setVisibility(View.GONE);
			url.setVisibility(View.GONE);
			
			
			final EmergencyNumber en = getEmergenciesNumber().get(position);
			
			if(en.getDescription()!=null){
				description.setVisibility(View.VISIBLE);
				description.setText(en.getDescription());
			}
			
			if(en.getNo01()!=null){
				number02.setVisibility(View.VISIBLE);
				number01.setText(en.getNo01());
				number01.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
						builder.setMessage("¿Está seguro que desea llamar a " + en.getDescription() + "?")
							   .setTitle("FarmaPY")
						       .setCancelable(false)
						       .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
									    String url = "tel:"+ en.getNo01();// "tel:+595216171000";
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
			
			
			if(en.getNo02()!=null){
				number02.setVisibility(View.VISIBLE);
				number02.setText(en.getNo02());
				number02.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
						builder.setMessage("¿Está seguro que desea llamar a " + en.getDescription() + "?")
							   .setTitle("FarmaPY")
						       .setCancelable(false)
						       .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
									    String url = "tel:"+ en.getNo02();// "tel:+595216171000";
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
			
			if(en.getNo03()!=null){
				number03.setText(en.getNo03());
				number03.setVisibility(View.VISIBLE);
				number03.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
						builder.setMessage("¿Está seguro que desea llamar a " + en.getDescription() + "?")
							   .setTitle("FarmaPY")
						       .setCancelable(false)
						       .setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
									    String url = "tel:"+ en.getNo03();// "tel:+595216171000";
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
			
			if(en.getAdditionalInfo()!=null){
				infoAdic.setVisibility(View.VISIBLE);
				infoAdic.setText(en.getAdditionalInfo());
			}
			
			if(en.getUrl()!=null){
				url.setVisibility(View.VISIBLE);
				url.setText(en.getUrl());
				
				
				url.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try{
							String url = en.getUrl();
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							startActivity(i);
						}catch (Exception e) {
							Log.e("EmergencyActivity",e.getMessage());
				    		Toast.makeText(EmergencyActivity.this, getString(R.string.emergencyNumberErrorUrl) , Toast.LENGTH_LONG).show();
						}
					}
				});
			}
			
			return convertView;
		}
		
	}
	
	


}
