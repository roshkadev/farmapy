package mobi.roshka.farmapy.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
 
import org.apache.http.util.ByteArrayBuffer;
 
import android.R.drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageManager {
    private static final String PATH = "/data/data/mobi.roshka.acata/";  //put the downloaded file here
    private static HashMap<String,Drawable> myDrawables = new HashMap<String,Drawable>();
    
    public static void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
            try {
                    URL url = new URL(imageURL); //you can write here any link
                    File file = new File(PATH, fileName);

                    long startTime = System.currentTimeMillis();
                    Log.d("ImageManager", "download begining");
                    Log.d("ImageManager", "download url:" + url);
                    Log.d("ImageManager", "downloaded file name:" + fileName);
                    /* Open a connection to that URL. */
                    URLConnection ucon = url.openConnection();

                    /*
                     * Define InputStreams to read from the URLConnection.
                     */
                    InputStream is = ucon.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);

                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
                    ByteArrayBuffer baf = new ByteArrayBuffer(50);
                    int current = 0;
                    while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                    }

                    /* Convert the Bytes read to a String. */
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baf.toByteArray());
                    fos.close();
                    Log.d("ImageManager", "download ready in"
                                    + ((System.currentTimeMillis() - startTime) / 1000)
                                    + " sec");

            } catch (IOException e) {
                    Log.d("ImageManager", "Error: " + e);
            }

    }
    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }
    
    public static Drawable getDrawable(String imageName, String url){
    	//buscar en cache, sino bajar y guardar y luego retornar como drawable;
    	Drawable d = myDrawables.get(imageName);

    	//File f = new File(PATH + imageName);
    	if(d == null) {
    		try {
				d = drawableFromUrl(url);
	    		myDrawables.put(imageName, d);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	//Drawable d = Drawable.createFromPath(PATH + imageName);
    	return d;
    }
}
