package mobi.roshka.farmapy.activity;

import java.util.Calendar;

import mobi.roshka.farmapy.R;
import mobi.roshka.farmapy.schedule.ScheduleClient;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author cdpuerto
 */
public class PillActivity extends Activity  {
	// This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;
    // This is the date picker used to select the date for our notification
	private DatePicker pickerinit;
	private DatePicker pickerend;
	private TimePicker pickertime;
	
	private TextView tvDisplayDateInit;
	private ImageButton btnChangeDateInit;
	private TextView tvDisplayDateEnd;
	private ImageButton btnChangeDateEnd;
	private TextView tvDisplayTimeInit;
	private ImageButton btnChangeTimeInit;
	
	private String array_spinner[];
	private String array_spinner2[];
	static final int DATE_DIALOG_ID1 = 999;
	static final int DATE_DIALOG_ID2 = 992;
	static final int DATE_DIALOG_ID3 = 993;
	
	int hours;
	int minutes;
	int dayx;
	int monthx;
	int yearx;
	int dayy;
	int monthy;
	int yeary;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.pill);
        
        // Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

        // Get a reference to our date picker
        pickerinit = (DatePicker) findViewById(R.id.scheduleTimePickerinit);
        pickerend = (DatePicker) findViewById(R.id.scheduleTimePickerend);
 
        tvDisplayDateInit = (TextView) findViewById(R.id.textDateinit);
        tvDisplayDateEnd = (TextView) findViewById(R.id.textDateend);
        tvDisplayTimeInit = (TextView) findViewById(R.id.textTimeend);
 
		final Calendar c = Calendar.getInstance();
		yearx = c.get(Calendar.YEAR);
		monthx = c.get(Calendar.MONTH);
		dayx = c.get(Calendar.DAY_OF_MONTH);
		yeary = c.get(Calendar.YEAR);
		monthy = c.get(Calendar.MONTH);
		dayy = c.get(Calendar.DAY_OF_MONTH);
		hours = c.get(Calendar.HOUR_OF_DAY);
		minutes = c.get(Calendar.MINUTE);
		// set current date into textview
    	tvDisplayDateInit.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(dayx).append("-").append(monthx + 1).append("-")
				.append(yearx).append(" "));       
        
    	tvDisplayDateEnd.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(dayy).append("-").append(monthy + 1).append("-")
				.append(yeary).append(" "));       
    	if(minutes < 10)
    	tvDisplayTimeInit.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(hours).append(":0").append(minutes));  
    	else
    		tvDisplayTimeInit.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(hours).append(":").append(minutes));  
    	
    	pickerinit.init(yearx, monthx, dayx, null);
    	pickerend.init(yeary, monthy, dayy, null);
    	
    	array_spinner=new String[5];
        array_spinner[0]="1";
        array_spinner[1]="2";
        array_spinner[2]="3";
        array_spinner[3]="4";
        array_spinner[4]="6";

        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,
        android.R.layout.simple_spinner_item, array_spinner);
        s.setAdapter(adapter);
        
        addListenerOnButton();

    }
	
    /**
     * This is the onClick called from the XML to set a new notification 
     */
    public void onDateSelectedButtonClick(View v){
    	// Get the date from our datepicker
    	Spinner s = (Spinner) findViewById(R.id.spinner);
    	int st = (int) (s.getSelectedItemId() + 1);
    	if(st == 5)
    		st++;
    	//Spinner s2 = (Spinner) findViewById(R.id.spinner2);
    	int st2 = hours;
    	int day1 = pickerinit.getDayOfMonth();
    	int month1 = pickerinit.getMonth();
    	int year1 = pickerinit.getYear();
    	Calendar c1 = Calendar.getInstance();
    	c1.set(year1, month1, day1);
    	c1.set(Calendar.HOUR_OF_DAY, st2);
    	c1.set(Calendar.MINUTE, 0);
    	c1.set(Calendar.SECOND, 0);
    	int day2 = pickerend.getDayOfMonth();
    	int month2 = pickerend.getMonth();
    	int year2 = pickerend.getYear();
    	Calendar c2 = Calendar.getInstance();
    	c2.set(year2, month2, day2);
    	c2.set(Calendar.HOUR_OF_DAY, st2);
    	c2.set(Calendar.MINUTE, 0);
    	c2.set(Calendar.SECOND, 0);
        		
        
    	if(c1.getTimeInMillis() > c2.getTimeInMillis()){
        	Toast.makeText(this, "La fecha final no puede ser menor", Toast.LENGTH_SHORT).show();
    	}
    	else{
    	// Create a new calendar set to the date chosen
    	// we set the time to midnight (i.e. the first minute of that day)
    	// Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
            long res = c2.getTimeInMillis() - c1.getTimeInMillis();
            long _24Hours = 1000*60*60*24;
            long d = Math.round(res/(_24Hours));
        	for (int j = 0; j <= d; j++){
        		for (int i = 0; i < st; i++){
        			Calendar c = Calendar.getInstance();
        			c.set(year1, month1, day1);
        			c.set(Calendar.HOUR_OF_DAY, st2 + (24 / st) * i);
        			c.set(Calendar.MINUTE, 0);
        			c.set(Calendar.SECOND, 0);
        			c.add(Calendar.DATE, j);
        			scheduleClient.setAlarmForNotification(c);
        		}
        	}
    	// Notify the user what they just did
    		Toast.makeText(this, "Notificationes creadas", Toast.LENGTH_SHORT).show();
    
    	}
    } 	
    
    @Override
    protected void onStop() {
    	// When our activity is stopped ensure we also stop the connection to the service
    	// this stops us leaking our activity into the system *bad*
    	if(scheduleClient != null)
    		scheduleClient.doUnbindService();
    	super.onStop();
    }
    

    
	public void addListenerOnButton() {

		btnChangeDateInit = (ImageButton) findViewById(R.id.button1);
		btnChangeDateInit.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID1);

			}

		});

		btnChangeDateEnd = (ImageButton) findViewById(R.id.button2);
		btnChangeDateEnd.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID2);

			}

		});
		
		btnChangeTimeInit = (ImageButton) findViewById(R.id.button3);
		btnChangeTimeInit.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(DATE_DIALOG_ID3);

			}

		});
		
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID1:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener1, yearx, monthx,
					dayx);
		case DATE_DIALOG_ID2:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener2, yeary, monthy,
					dayy);
		case DATE_DIALOG_ID3:
			// set date picker as current date
			return new TimePickerDialog(this, timePickerListener, hours, minutes, true);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			yearx = selectedYear;
			monthx = selectedMonth;
			dayx = selectedDay;

	    	tvDisplayDateEnd.setText(new StringBuilder()
					// Month is 0 based, just add 1
					.append(dayx).append("-").append(monthx + 1).append("-")
					.append(yearx).append(" "));       
	    	
	    	pickerinit.init(yearx, monthx, dayx, null);

		}
	};    
 
	private DatePickerDialog.OnDateSetListener datePickerListener2 = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			yeary = selectedYear;
			monthy = selectedMonth;
			dayy = selectedDay;

	    	tvDisplayDateEnd.setText(new StringBuilder()
					// Month is 0 based, just add 1
					.append(dayy).append("-").append(monthy + 1).append("-")
					.append(yeary).append(" "));       
	    	
	    	pickerend.init(yeary, monthy, dayy, null);
		}
	};    

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		// when dialog box is closed, below method will be called.
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hours = selectedHour;
			minutes = selectedMinute;
	    	if(minutes < 10)
	        	tvDisplayTimeInit.setText(new StringBuilder()
	    				// Month is 0 based, just add 1
	    				.append(hours).append(":0").append(minutes));  
	        	else
	        		tvDisplayTimeInit.setText(new StringBuilder()
	    				// Month is 0 based, just add 1
	    				.append(hours).append(":").append(minutes));    
	    	pickerend.init(yeary, monthy, dayy, null);			
		}
	}; 	
	
}