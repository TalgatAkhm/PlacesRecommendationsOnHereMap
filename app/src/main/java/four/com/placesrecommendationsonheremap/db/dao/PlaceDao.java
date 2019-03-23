package four.com.placesrecommendationsonheremap.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import four.com.placesrecommendationsonheremap.db.entities.Feature;

@Dao
public interface PlaceDao {
    @Insert
    long insertOne(Feature feature);

    @Update
    void update(Feature feature);

    @Delete
    void delete(Feature feature);
}
