package com.example.juegowoniiepooh;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class EscenarioJuego extends AppCompatActivity {
    String nombre, email, uId, zombie;
    TextView tvContador, tvNombre, tvTiempo;
    ImageView imgZombie;
    Random aleatorio;
    int anchoPantalla, altoPantalla;
    int contador = 0;

    boolean gameOver;
    Dialog miDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenario_juego);

        tvContador = findViewById(R.id.txtContadorEsc);
        tvNombre = findViewById(R.id.txtNombreEs);
        tvTiempo = findViewById(R.id.txtTiempoEsc);
        imgZombie = findViewById(R.id.imgJuego);
        Bundle intent = getIntent().getExtras();

        uId = intent.getString("uId");
        nombre = intent.getString("nombres");
        email = intent.getString("email");
        zombie = intent.getString("zombie");
        tvNombre.setText(nombre);
        tvContador.setText(zombie);
        miDialog = new Dialog(EscenarioJuego.this);
        pantalla();
        imgZombie.setOnClickListener((event) -> {
            if (!gameOver) {
                contador++;
                tvContador.setText(String.valueOf(contador));
                imgZombie.setImageResource(R.drawable.foto_5);
                new Handler().postDelayed((() -> {
                    movimiento();
                    imgZombie.setImageResource(R.drawable.foto_3_zombie);
                }), 500);
            }
        });
        Typeface fuente = Typeface.createFromAsset(EscenarioJuego.this.getAssets(), "fuentes/zombie.TTF");
        tvNombre.setTypeface(fuente);
        tvContador.setTypeface(fuente);
        tvTiempo.setTypeface(fuente);

        cuentaAtras();
    }

    private void cuentaAtras() {

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                tvTiempo.setText(segundosRestantes + " s");
            }
            @Override
            public void onFinish() {
                tvTiempo.setText("0s");
                gameOver = true;
                mensajeGameOver();
            }
        }.start();
    }
    private void pantalla() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        altoPantalla = point.y;
        anchoPantalla = point.x;
        aleatorio = new Random();
    }
    private void movimiento() {
        int min = 0;
        int maxX = anchoPantalla - imgZombie.getWidth();
        int maxY = anchoPantalla - imgZombie.getHeight();
        int randomX = aleatorio.nextInt(((maxX - min) + 1) + min);
        int randomY = aleatorio.nextInt(((maxY - min) + 1) + min);
        imgZombie.setX(randomX);
        imgZombie.setY(randomY);
    }
    private void mensajeGameOver() {

        Typeface typeface = Typeface.createFromAsset(EscenarioJuego.this.getAssets(), "fuentes/zombie.TTF");

        TextView seAcaboTxt, hasMatadoTxt, numeroTxt;
        Button jugarDeNuevo, irMenu, puntajes;

        miDialog.setContentView(R.layout.gameover);
        miDialog.setCancelable(false);

        seAcaboTxt = miDialog.findViewById(R.id.seacaboTxt);
        hasMatadoTxt = miDialog.findViewById(R.id.hasMatadoTxt);
        numeroTxt = miDialog.findViewById(R.id.numeroTxt);

        jugarDeNuevo = miDialog.findViewById(R.id.jugarDeNuevo);
        irMenu = miDialog.findViewById(R.id.irMenu);
        puntajes = miDialog.findViewById(R.id.puntajes);

        String zombies = String.valueOf(contador);
        numeroTxt.setText(zombies);

        seAcaboTxt.setTypeface(typeface);
        hasMatadoTxt.setTypeface(typeface);
        numeroTxt.setTypeface(typeface);
        jugarDeNuevo.setTypeface(typeface);
        irMenu.setTypeface(typeface);
        puntajes.setTypeface(typeface);
        miDialog.show();

    }
}