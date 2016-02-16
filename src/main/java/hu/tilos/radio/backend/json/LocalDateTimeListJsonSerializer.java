package hu.tilos.radio.backend.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class LocalDateTimeListJsonSerializer extends JsonSerializer<List<LocalDateTime>> {


    @Override
    public void serialize(List<LocalDateTime> localDateTimes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws java.io.IOException, JsonProcessingException {
        jsonGenerator.writeStartArray(localDateTimes.size());
        localDateTimes.forEach(dateTime -> {
            try {
                jsonGenerator.writeNumber(dateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        jsonGenerator.writeEndArray();

    }
}
