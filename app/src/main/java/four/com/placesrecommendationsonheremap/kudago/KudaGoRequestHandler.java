package four.com.placesrecommendationsonheremap.kudago;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * https://docs.kudago.com/api/#page:%D0%BF%D0%BE%D0%B8%D1%81%D0%BA,header:%D0%BF%D0%BE%D0%B8%D1%81%D0%BA-%D0%BF%D0%BE%D0%B8%D1%81%D0%BA
 */
public final class KudaGoRequestHandler extends AsyncTask<Object, Integer, String> {
    private final static String TAG = KudaGoRequestHandler.class.getName();

    public enum EKudaGoPlacesTypes {
        news, event, place, list
    }

    private static String DEFAULT_CITY = "spb";
    private static final String BASE_URL = "https://kudago.com/public-api/v1.4//?";

    private final OkHttpClient client = new OkHttpClient();
    private final EKudaGoPlacesTypes type;

    private String url;
    private String ctype;
    private String city = DEFAULT_CITY;
    private String q; // search request (поисковый запрос)
    private double lat = 60.003106, lon = 30.290727, rad = 100;

    public KudaGoRequestHandler(EKudaGoPlacesTypes type, String searchRequest) {
        this.type = type;
        this.q = searchRequest;
        url = BASE_URL + "q=" + searchRequest + "&type=";
        createUrl();
    }

    /**
     * @param type
     * @param city = msk || spb
     * @param lat
     * @param lon
     * @param rad
     */
    public KudaGoRequestHandler(EKudaGoPlacesTypes type, String city, double lat, double lon, double rad) {
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.rad = rad;
        this.city = city;
    }

    @Override
    @Nullable
    protected String doInBackground(Object... objects) {
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
//        txtString.setText(s);
        //TODO: implement this
        Log.d(TAG, s);
    }

    private void createUrl() {
        switch (type) {
            case news:
                ctype = "news";
                break;
            case event:
                ctype = "event";
                break;
            case place:
                ctype = "place";
                break;
            default:
                ctype = "list";
                break;
        }
        url = "https://kudago.com/public-api/v1.4/search/?q=" +
                "&lang=" +
                "&expand=" +
                "&location=" + city +
                "&ctype=" + ctype +
                "&is_free=" +
                "&lat=" + Double.toString(lat) +
                "&lon=" + Double.toString(lon) +
                "&radius=" + Double.toString(rad);

    }
}
