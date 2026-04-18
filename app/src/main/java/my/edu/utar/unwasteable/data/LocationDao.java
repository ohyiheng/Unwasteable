package my.edu.utar.unwasteable.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    void insert(Location location);

    @Query("SELECT * FROM locations")
    List<Location> getAllLocations();

    @Query("SELECT * FROM locations WHERE id = :id")
    Location getLocationById(int id);

    @Update
    void update(Location location);

    @Delete
    void delete(Location location);
}
