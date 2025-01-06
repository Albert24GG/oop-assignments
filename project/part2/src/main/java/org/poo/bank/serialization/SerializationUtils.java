package org.poo.bank.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public final class SerializationUtils {
    private SerializationUtils() {
    }

    public static final class RoundedDoubleSerializer extends JsonSerializer<Double> {
        @Override
        public void serialize(final Double value, final JsonGenerator jsonGenerator,
                              final SerializerProvider serializerProvider) throws IOException {
            if (value != null) {
                BigDecimal roundedValue =
                        BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP);
                jsonGenerator.writeNumber(roundedValue.doubleValue());
            } else {
                jsonGenerator.writeNull();
            }
        }
    }
}
