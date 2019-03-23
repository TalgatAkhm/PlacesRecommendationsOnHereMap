package four.com.placesrecommendationsonheremap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import four.com.placesrecommendationsonheremap.kudago.KudaGoPlaceAsyncTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (new KudaGoPlaceAsyncTask()).execute();
    }
}
