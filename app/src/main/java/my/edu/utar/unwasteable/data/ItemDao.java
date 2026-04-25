package my.edu.utar.unwasteable.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    long insert(Item item);

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getItems();

    @Query("SELECT * FROM items WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1")
    Item getItemByName(String name);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);
}