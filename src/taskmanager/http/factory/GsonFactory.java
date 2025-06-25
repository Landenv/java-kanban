package taskmanager.http.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import taskmanager.http.adapter.DurationAdapter;
import taskmanager.http.adapter.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonFactory {
    public static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
