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
    void insert(Item item);

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(int id);

    @Query("SELECT * FROM items WHERE expiry_date < :currentDate")
    List<Item> getExpiredItems(String currentDate);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);
}