package peace.developer.serj.photoloader.Api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import peace.developer.serj.photoloader.interfaces.ApiCallback;
import peace.developer.serj.photoloader.interfaces.PhotoCallback;

public class NetworkProvider {
    private final String TAG = "HTTP_PROVIDER";
    private final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static NetworkProvider mInstance;
    private String result;
    public static Handler mainThreadHandler;

    public static NetworkProvider getmInstance() {
        if (mInstance == null)
            mInstance = new NetworkProvider();
        return mInstance;
    }

    //Methods
    public void getUsers(final ApiCallback callback) {
        runAsyncRequest("/users", callback);
    }

    public void getUserAlbums(int id, ApiCallback callback) {
        runAsyncRequest("/users/" + id + "/albums", callback);
    }

    public void getPhotoByAlbumId(int id, ApiCallback callback) {
        runAsyncRequest("/albums/" + id + "/photos", callback);
    }

    public void getPhoto(String url, PhotoCallback callback) {
        runAsyncRequest(url,callback);
    }



    //AsyncRequests
    private void runAsyncRequest(final String path, final ApiCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callConnectionForJson(path, callback);
            }
        }).start();
    }
    private void runAsyncRequest(final String path, final PhotoCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callConnectionForPhoto(path, callback);
            }
        }).start();
    }


    //Connection
    private void callConnectionForJson(String path, final ApiCallback callback) {

        try {
            HttpsURLConnection connection = getConnection(BASE_URL + path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            result = builder.toString();

            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (result == null || result.equals(""))
                        callback.onError();
                    else
                        callback.onResponse(result);
                    result = null;
                }
            });

            reader.close();
            connection.disconnect();

        } catch (IOException e) {
            callback.onError();
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void callConnectionForPhoto(final String path, final PhotoCallback callback) {
        try {
            HttpsURLConnection connection = getConnection(path);
            InputStream inputStream = connection.getInputStream();
            if(inputStream != null){
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap == null)
                            callback.onError();
                        else
                            callback.onPhotoLoaded(bitmap);
                    }
                });
                inputStream.close();
                connection.disconnect();
            }


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private HttpsURLConnection getConnection(String path) {
        try {
            URL url = new URL(path);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;

    }


}
