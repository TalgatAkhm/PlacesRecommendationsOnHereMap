package four.com.placesrecommendationsonheremap;

import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerConnectAsyncTask extends AsyncTask<Void, Void, String> {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final String url = "http://192.168.43.187:8000/hacksite/get_relevant_places/";

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
        String targetUrl = url;

        RequestBody body = RequestBody.create(JSON, request);

        Request request = new Request.Builder()
                .url(targetUrl)
                .post(body)
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
