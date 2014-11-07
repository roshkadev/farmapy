package mobi.roshka.farmapy.activity;

import mobi.roshka.farmapy.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
//        TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
//        try {
//			int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
//			versionNumber.setText("Versión " + versionCode);
//		} catch (NameNotFoundException e) {			
//			e.printStackTrace();
//			versionNumber.setText("Versión 1.0");
//		}

    }

	
}
