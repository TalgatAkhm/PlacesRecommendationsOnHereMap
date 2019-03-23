package four.com.placesrecommendationsonheremap;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerConnectAsyncTask extends AsyncTask<Void, Void, String> {

    private final String url = "";

    private final OkHttpClient client = new OkHttpClient();

    public interface ServerCallback {
        void onResult(String string);
    }

    private ServerCallback callback;

    private String request;

    public ServerConnectAsyncTask(ServerCallback callback, String request) {
        this.callback = callback;
        this.request = request;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String targetUrl = url + request;
        Request request = new Request.Builder()
                .url(targetUrl)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        callback.onResult(s);
    }
}
