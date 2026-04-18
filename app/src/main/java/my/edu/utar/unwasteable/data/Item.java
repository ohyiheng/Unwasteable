package my.edu.utar.unwasteable.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "items",
    foreignKeys = {
        @ForeignKey(
            entity = Product.class,
            parentColumns = "id",
            childColumns = "product_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "location_id",
            onDelete = ForeignKey.SET_NULL
        )
    },
    indices = {@Index("product_id"), @Index("location_id")}
)
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "product_id")
    public int productId;

    @ColumnInfo(name = "location_id")
    public int locationId;
    public double quantity;

    @ColumnInfo(name = "expiry_date")
    public String expiryDate;

    @ColumnInfo(name = "opened_date")
    public String openedDate;

    public String status;
}
