package my.edu.utar.unwasteable.data;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class DateConverters {
    @TypeConverter
    public static LocalDate fromString(String string) {
        return string == null ? null : LocalDate.parse(string);
    }

    @TypeConverter
    public static String toString(LocalDate date) {
        return date == null ? null : date.toString();
    }
}
