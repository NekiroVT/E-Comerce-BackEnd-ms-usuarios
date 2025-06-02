package com.msusuarios.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private static final String BUCKET_NAME = "wimine-ventas-app.appspot.com"; // ✅ Tu bucket correcto

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(
                    "src/main/resources/firebase/firebase-credentials.json" // ✅ Tu archivo de credenciales
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(BUCKET_NAME)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    public String subirImagen(String username, File imagen) throws IOException {
        String nombreArchivo = "profile_photos/" + username + "_" + UUID.randomUUID() + ".jpg";

        StorageClient.getInstance().bucket().create(
                nombreArchivo,
                Files.readAllBytes(imagen.toPath()),
                "image/jpeg"
        );

        return "https://storage.googleapis.com/" + BUCKET_NAME + "/" + nombreArchivo;
    }

    public void eliminarImagenAnterior(String urlImagen) {
        if (urlImagen == null || urlImagen.isBlank()) {
            return;
        }

        try {
            String rutaFirebase = extraerRutaDesdeUrl(urlImagen);
            if (rutaFirebase != null) {
                StorageClient.getInstance().bucket().get(rutaFirebase).delete();
            }
        } catch (Exception e) {
            System.err.println("❌ No se pudo eliminar la imagen anterior: " + e.getMessage());
        }
    }

    private String extraerRutaDesdeUrl(String url) {
        int index = url.indexOf("/profile_photos/");
        if (index == -1) {
            return null;
        }
        return url.substring(index + 1); // ✅ Extrae la ruta relativa a Firebase
    }
}
