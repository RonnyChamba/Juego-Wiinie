package com.example.juegowoniiepooh;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    private  String zombies;
    private ImageView imagen;

    public DialogFragment(String zombies){

        this.zombies = zombies;
    }
   public DialogFragment(){

   }

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
        });

        irMenu.setOnClickListener( (vi) ->{

            startActivity( new Intent( getContext(),Menu.class ));
        });
        puntajes.setOnClickListener( (vi) ->{
            startActivity( new Intent(   getContext(), Puntajes.class));
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
