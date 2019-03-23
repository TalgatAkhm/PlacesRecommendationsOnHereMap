package four.com.placesrecommendationsonheremap.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import four.com.placesrecommendationsonheremap.db.dao.FeatureDao;
import four.com.placesrecommendationsonheremap.db.dao.PlaceDao;
import four.com.placesrecommendationsonheremap.db.entities.Feature;
import four.com.placesrecommendationsonheremap.db.entities.Place;

@Database(entities = {Feature.class, Place.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FeatureDao featureDao();
    public abstract PlaceDao placeDao();
}