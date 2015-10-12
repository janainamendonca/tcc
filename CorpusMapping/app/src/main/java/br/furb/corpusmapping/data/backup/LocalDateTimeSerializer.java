package br.furb.corpusmapping.data.backup;

import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;

public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();


    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        String retVal = fmt.print(src);
        Log.v("MY LOCALDATE SERIALIZED", retVal);
        return new JsonPrimitive(retVal);
    }


    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Log.v("LOCALDATE DESERIALIZED", json.getAsString());
        return fmt.parseLocalDateTime(json.getAsString());
    }
}