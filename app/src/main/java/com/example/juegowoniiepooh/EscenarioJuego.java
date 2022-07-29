package com.example.juegowoniiepooh;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EscenarioJuego extends AppCompatActivity {

    String nombre, email, uId, zombie;
    TextView txtContador;
    TextView txtNombre;
    TextView txtTiempo;
    ImageView imgZombie;

    Random aleatorio;
    int anchoPantalla;
    int altoPantalla;
    int contador = 0;

    boolean gameOver = true;
    Dialog miDialog;

    private FirebaseAuth auth;
    private FirebaseDatabase dataBase;
    private DatabaseReference dbReference;
    private FirebaseUser user;

    ImageView iniciarJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenario_juego);

        txtContador = findViewById(R.id.txtContadorEsc);
        txtNombre = findViewById(R.id.txtNombreEs);
        txtTiempo = findViewById(R.id.txtTiempoEsc);
        imgZombie = findViewById(R.id.imgJuego);
        iniciarJuego = findViewById(R.id.img_iniciarJuego);
        initFirebase();

        Bundle intent = getIntent().getExtras();

        uId = intent.getString("uId");
        nombre = intent.getString("nombres");
        email = intent.getString("email");
        zombie = intent.getString("zombie");
        miDialog = new Dialog(EscenarioJuego.this);
        txtNombre.setText(nombre);
        txtContador.setText(zombie);

        pantalla();
        imgZombie.setOnClickListener((event) -> {
            if (!gameOver) {
                contador++;
                txtContador.setText(String.valueOf(contador));
                imgZombie.setImageResource(R.drawable.img_atrapado_2);
                new Handler().postDelayed((() -> {
                    movimiento();
                    imgZombie.setImageResource(R.drawable.foto_2);
                }), 500);
            }else Toast.makeText(this, "Inicie un nuevo juego", Toast.LENGTH_SHORT).show();
        });


        iniciarJuego.setOnClickListener( (event) ->{
            gameOver = false;
            cuentaAtras();

        });
        Typeface typeface = Typeface.createFromAsset(EscenarioJuego.this.getAssets(), "fuentes/zombie.TTF");

        txtNombre.setTypeface(typeface);
        txtContador.setTypeface(typeface);
        txtTiempo.setTypeface(typeface);
    }



    private void  initFirebase(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        dataBase = FirebaseDatabase.getInstance();
        dbReference = dataBase.getReference(Constantes.NAME_BD);
    }

    private  void cuentaAtras() {

        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                txtTiempo.setText(segundosRestantes + " s");
            }

            @Override
            public void onFinish() {
                txtTiempo.setText("0s");
                gameOver = true;
                updateDataPlayer();
                dialogSms();
            }
        }.start();
    }
    private void dialogSms(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment  dialog = new DialogFragment(String.valueOf(contador));
        dialog.setCancelable(false);

        contador = 0;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, dialog)
                .addToBackStack(null).commit();
    }

    private  void updateDataPlayer(){

        Map<String, Object> data =  new HashMap<>();
        data.put("Zombies", contador);
        dbReference.child(user.getUid()).updateChildren(data).addOnCompleteListener( (task) ->{
            Toast.makeText(this, "El puntaje ha sido actualizado correctamente", Toast.LENGTH_SHORT).show();
        });

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

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
    }

    class DialogFragmentGameOver extends androidx.fragment.app.DialogFragment {

        private  String zombies;
        private ImageView imagen;
        public DialogFragmentGameOver(String zombies){

            this.zombies = zombies;
        }
        public DialogFragmentGameOver(){}

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            //return super.onCreateView(inflater.inflate(), container, savedInstanceState);
            View  view = inflater.inflate(R.layout.gameover, container, false);
            mensajeGameOver(view);

            return  view;
        }
        private void mensajeGameOver(View view) {

            Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fuentes/zombie.TTF");


            TextView seAcaboTxt, hasMatadoTxt, numeroTxt;
            Button jugarDeNuevo, irMenu, puntajes;

            seAcaboTxt = view.findViewById(R.id.seacaboTxt);
            hasMatadoTxt = view.findViewById(R.id.hasMatadoTxt);
            numeroTxt = view.findViewById(R.id.numeroTxt);

            jugarDeNuevo = view.findViewById(R.id.jugarDeNuevo);
            irMenu = view.findViewById(R.id.irMenu);
            puntajes = view.findViewById(R.id.puntajes);

            jugarDeNuevo.setOnClickListener((vi) -> {

                this.dismiss();
                gameOver = false;
                cuentaAtras();


            });

            irMenu.setOnClickListener( (vi) ->{

                startActivity( new Intent( getContext(),Menu.class ));
            });
            puntajes.setOnClickListener( (vi) ->{
                this.dismiss();
               // startActivity( new Intent(   getContext(), Puntajes.class));
            });
            numeroTxt.setText(zombies);
            seAcaboTxt.setTypeface(typeface);
            hasMatadoTxt.setTypeface(typeface);
            numeroTxt.setTypeface(typeface);

            jugarDeNuevo.setTypeface(typeface);
            irMenu.setTypeface(typeface);
            puntajes.setTypeface(typeface);
            onloadGif(view);
        }

        private void onloadGif(View view){

            imagen = view.findViewById(R.id.imageGif);
            String url = "https://i.pinimg.com/originals/16/84/4e/16844ef7cc93fd3c7a608aefae306ac1.gif";
            Uri urlParse = Uri.parse(url);
            Glide.with(view.getContext()).load(urlParse).into(imagen);

        }

    }

}