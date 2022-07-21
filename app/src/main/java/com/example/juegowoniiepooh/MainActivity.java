package com.example.juegowoniiepooh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    Button btnLogin, btnRegistro;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        imagen = findViewById(R.id.imageGif);

        Typeface typeface = Typeface.createFromAsset(MainActivity.this.getAssets(), "fuentes/zombie.TTF");
        btnLogin.setTypeface(typeface);
        btnRegistro.setTypeface(typeface);

        btnLogin.setOnClickListener( (event) -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        });

        btnRegistro.setOnClickListener( (event) -> {
            Intent intent = new Intent(this, Registro.class);
            startActivity(intent);
        });

        String url = "https://i.pinimg.com/originals/16/84/4e/16844ef7cc93fd3c7a608aefae306ac1.gif";
        Uri urlParse = Uri.parse(url);
        Glide.with(getApplicationContext()).load(urlParse).into(imagen);

    }
}