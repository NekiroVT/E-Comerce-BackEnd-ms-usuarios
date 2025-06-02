package com.msusuarios.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.UUID;

public class FlexibleUUIDDeserializer extends JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getText().trim().toLowerCase();

        if (!raw.contains("-")) {
            // Inserta guiones en el formato estándar de UUID
            raw = raw.replaceAll(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            );
        }

        return UUID.fromString(raw);
    }
}
