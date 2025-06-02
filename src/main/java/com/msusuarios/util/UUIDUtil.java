package com.msusuarios.util;

import java.util.UUID;

public class UUIDUtil {

    /**
     * Convierte un UUID en string en cualquier formato a UUID estándar.
     * Acepta sin guiones, mayúsculas o con espacios.
     */
    public static UUID parseFlexibleUUID(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("El UUID proporcionado está vacío o es nulo");
        }

        raw = raw.trim().toLowerCase();

        // Si no tiene guiones, los insertamos al estilo estándar
        if (!raw.contains("-")) {
            raw = raw.replaceAll(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            );
        }

        return UUID.fromString(raw);
    }
}
