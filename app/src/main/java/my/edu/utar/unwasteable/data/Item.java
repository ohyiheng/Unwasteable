package my.edu.utar.unwasteable.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
    tableName = "items",
    foreignKeys = {
        @ForeignKey(
            entity = Category.class,
            parentColumns = "id",
            childColumns = "category_id",
            onDelete = ForeignKey.SET_NULL
        ),
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "location_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {@Index("category_id"), @Index("location_id")}
)
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    @ColumnInfo(name = "location_id")
    public Integer locationId;

    @ColumnInfo(name = "category_id")
    public Integer categoryId;

    public double quantity;

    @ColumnInfo(name = "expiry_date")
    public LocalDate expiryDate;
}
