package four.com.placesrecommendationsonheremap.kudago;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class KudaGoPlaceAsyncTask extends AsyncTask<Void, Void, String> {
    private final static String TAG = KudaGoPlaceAsyncTask.class.getName();

    private final String BASE_URL = "https://kudago.com/public-api/v1.4/places/?";
    private final String PAGE_SIZE = "100";
    private final String FIELDS = "id,title,address,timetable,phone,description,coords,foreign_url,is_closed,categories,tags";
    private final String LOCATION = "spb";

    private Map<String, String> parametersMap = new HashMap<>();
    private final OkHttpClient client = new OkHttpClient();

    private KudaGoPlaceAsyncTaskCallback callback;

    interface KudaGoPlaceAsyncTaskCallback {
        void getResponseFromKudaGoPlace(String response);
    }

    public KudaGoPlaceAsyncTask(KudaGoPlaceAsyncTaskCallback callback) {
        this.callback = callback;
        addDefaultParameters();
    }

    public KudaGoPlaceAsyncTask(KudaGoPlaceAsyncTaskCallback callback, double lon, double lan, double radius) {
        this.callback = callback;
        addDefaultParameters();
        parametersMap.put("lon", lon+"");
        parametersMap.put("lan", lan+"");
        parametersMap.put("radius", radius+"");
    }

    @Override
    protected String doInBackground(Void... voids) {
        String url = createUrl();

        Request request = new Request.Builder()
                .url(url)
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
        callback.getResponseFromKudaGoPlace(s);
    }

    private void addDefaultParameters() {
        parametersMap.put("page_size", PAGE_SIZE);
        parametersMap.put("fields", FIELDS);
        parametersMap.put("location", LOCATION);
    }

    private String createUrl() {
        StringBuilder builder = new StringBuilder(BASE_URL);

        for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        return builder.deleteCharAt(builder.length() - 1).toString();
    }
}
