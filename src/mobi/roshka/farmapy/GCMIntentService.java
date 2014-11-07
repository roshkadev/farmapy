package mobi.roshka.farmapy;

import mobi.roshka.farmapy.comm.CommManager;
import mobi.roshka.farmapy.exception.AcataAppException;
import mobi.roshka.farmapy.util.FarmaPYNotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {
	}

	public GCMIntentService(String... senderIds) {
		super(senderIds);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("GCMIntentService", "Got an error: " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d("GCMIntentService", "Got a message!");
		new FarmaPYNotificationManager().handleMessage(intent);
	}

	@Override
	protected void onRegistered(Context ctx, String regId) {
		try {
			CommManager.gcmRegister(regId);
		} catch (AcataAppException e) {
			Log.e("GCMIntentService", "Error while executing onRegistered", e);
		}
	}

	@Override
	protected void onUnregistered(Context ctx, String regId) {
		try {
			CommManager.gcmUnregister(regId);
		} catch (AcataAppException e) {
			Log.e("GCMIntentService", "Error while executing onUnregistered", e);
		}
	}

}
