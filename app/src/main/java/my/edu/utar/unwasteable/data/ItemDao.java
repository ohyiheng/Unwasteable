package my.edu.utar.unwasteable.data;

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
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItemById(int id);

    @Query("SELECT * FROM items WHERE product_id = :productId")
    List<Item> getItemsByProductId(int productId);

    @Query("SELECT * FROM items WHERE location_id = :locationId")
    List<Item> getItemsByLocationId(int locationId);

    @Query("SELECT * FROM items WHERE expiry_date < :currentDate")
    List<Item> getExpiredItems(String currentDate);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);
}
