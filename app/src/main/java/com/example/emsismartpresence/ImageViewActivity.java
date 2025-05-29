package com.example.emsismartpresence;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView imageView = findViewById(R.id.imageViewDocument);

        // Récupérer l’URL de l’image passée en paramètre
        String imageUrl = getIntent().getStringExtra("DOCUMENT_URL");

        if (imageUrl != null) {
            // Charger l’image depuis l’URL dans l’ImageView (avec Glide)
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }
    }
}
