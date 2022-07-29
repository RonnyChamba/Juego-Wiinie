package com.example.juegowoniiepooh;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button btnJugar, btnPuntuacion, btnAcercaDe, btnCerrarSesion;

    TextView txtTituloMenu, txtUidJugadorMenu, txtZombieMenu, txtSubTituloMenu,
            txtCorreoJugadorMenu, txtNombreJugadorMenu;


    FirebaseDatabase database;
    DatabaseReference jugadores;
    ImageView imagen;

    private TextView txtPerfilMenu;
    private TextView txtFechaMenu;
    private TextView txtEdadMenu;
    private TextView txtPaisMenu;
    private ImageView imgEditar;
    private CircleImageView imgPerfil;
    private Button btnEditar;
    private Button btnCambiasPass;

    /* Para cambiar foto perfil*/

    private StorageReference referenciaAlmacenamiento;
    private String rutaAlmecamiento = "FotosDePerfil/*";

    /*Permisos*/
    private static final int CODIGO_SOLICITUD_ALMACENAMIENTO = 200;
    private static final int CODIGO_SELECCION_IMAGEN = 300;

    /*MATRICES*/
    private String[] permisosAlmacenamiento;
    private Uri imagenUri;
    private String perfil; // nombre column

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtTituloMenu = findViewById(R.id.txtTituloMenu);
        txtUidJugadorMenu = findViewById(R.id.txtUidMenu);
        txtSubTituloMenu = findViewById(R.id.txtSubtituloBotones);
        txtCorreoJugadorMenu = findViewById(R.id.txtCorreoJugadorMenu);
        txtNombreJugadorMenu = findViewById(R.id.txtNombreMenu);
        txtZombieMenu = findViewById(R.id.txtZombies);

        txtPerfilMenu = findViewById(R.id.txtTitlePerfil);
        txtEdadMenu = findViewById(R.id.txtEdadMenu);
        txtPaisMenu = findViewById(R.id.txtPaisMenu);
        txtFechaMenu = findViewById(R.id.txtFechaMenu);
        imgEditar = findViewById(R.id.imgEditar);
        imgPerfil = findViewById(R.id.imgPerfil);

        btnJugar = findViewById(R.id.btnJugar);
        btnPuntuacion = findViewById(R.id.btnPuntaciones);
        btnAcercaDe = findViewById(R.id.btnHacerca);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        imagen = findViewById(R.id.imageGif);

        btnEditar = findViewById(R.id.btnEditar);
        btnCambiasPass = findViewById(R.id.btnCambiarPass);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        jugadores = database.getReference("MI DATA BASE JUGADORES");

        // Iniciar para cambiar foto perfil
        referenciaAlmacenamiento = FirebaseStorage.getInstance().getReference();
        permisosAlmacenamiento = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btnJugar.setOnClickListener((event) -> {


            Intent intent = new Intent(Menu.this, EscenarioJuego.class);

            String nombre = txtNombreJugadorMenu.getText() +"";
            String uId = txtUidJugadorMenu.getText() +"";
            String email = txtCorreoJugadorMenu.getText() +"";
            String zombies = txtZombieMenu.getText() +"";

            intent.putExtra("uId", uId);
            intent.putExtra("nombres", nombre);
            intent.putExtra("email", email);
            intent.putExtra("zombie", zombies);
            startActivity(intent);


        });
        btnPuntuacion.setOnClickListener((event) -> {

           // startActivity( new Intent(this, Puntajes.class));
        });
        btnAcercaDe.setOnClickListener((event) -> {
            openDialogAbouth();
        });

        btnEditar.setOnClickListener((event) -> {
            editarDatos();
        });

        imgEditar.setOnClickListener( (event)-> {

            actualizarFoto();
        } );

        btnCambiasPass.setOnClickListener( (event) ->{
          //  startActivity( new Intent(Menu.this, CambioPassword.class));
        });

        Typeface typeface = Typeface.createFromAsset(Menu.this.getAssets(), "fuentes/zombie.TTF");

        txtTituloMenu.setTypeface(typeface);
        txtZombieMenu.setTypeface(typeface);
        txtSubTituloMenu.setTypeface(typeface);
        txtCorreoJugadorMenu.setTypeface(typeface);
        txtNombreJugadorMenu.setTypeface(typeface);


        btnJugar.setTypeface(typeface);
        btnPuntuacion.setTypeface(typeface);
        btnAcercaDe.setTypeface(typeface);
        btnCerrarSesion.setTypeface(typeface);
        btnCerrarSesion.setOnClickListener((event) -> cerrarSesion());

        txtPerfilMenu.setTypeface(typeface);
        txtEdadMenu.setTypeface(typeface);
        txtFechaMenu.setTypeface(typeface);
        txtPaisMenu.setTypeface(typeface);
        btnEditar.setTypeface(typeface);
        btnCambiasPass.setTypeface(typeface);


        String url = "https://i.pinimg.com/originals/16/84/4e/16844ef7cc93fd3c7a608aefae306ac1.gif";
        Uri urlParse = Uri.parse(url);
        Glide.with(getApplicationContext()).load(urlParse).into(imagen);

    }

    @Override
    protected void onStart() {
        usuarioLogeado();
        super.onStart();
    }

    private void openDialogAbouth() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //DialogFragmentAcerca dialog = new DialogFragmentAcerca();
        //dialog.setCancelable(false);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        //transaction.add(android.R.id.content, dialog)
          //      .addToBackStack(null).commit();
    }
    private void editarDatos() {

        String[] opciones = {"Cambiar Edad", "Cambiar Pais"};

        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        buider.setTitle("Editar Datos");
        buider.setItems(opciones, (dialog, index) -> {


            if (index == 0) {
                actualizarEdad();
            } else if (index == 1) {
                actualizarPais();
            }


        }).create().show();


    }
    private void actualizarPais() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Pais");

        LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(5, 5, 5, 5);


        Spinner spinner = new Spinner(this);

        String paises[] = {"Ecuador", "Colombia", "Peru", "Venezuela", "Argentina"};
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, paises));
        linearLayout.addView(spinner);
        builder.setView(linearLayout);

        builder.setPositiveButton("Actualizar", (di, index) -> {

            String value = spinner.getSelectedItem().toString();
            Map<String, Object> mapa = new HashMap<>();
            mapa.put("Pais", value);

            jugadores.child(user.getUid()).updateChildren(mapa)
                    .addOnSuccessListener((unsed) -> {

                        Toast.makeText(Menu.this, "Pais actualizado", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener((exception) -> {

                Toast.makeText(Menu.this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });


        builder.setNegativeButton("Cancelar", (di, index) -> {
        });

        builder.create().show();


    }

    private void actualizarEdad() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Edad");

        LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(5, 5, 5, 5);

        EditText editText = new EditText(this);
        editText.setHint("Ingrese nueva edad");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayout.addView(editText);
        builder.setView(linearLayout);

        builder.setPositiveButton("Actualizar", (di, index) -> {

            String value = editText.getText().toString();

            value = value.isEmpty() ? "0" : value;

            Map<String, Object> mapa = new HashMap<>();
            mapa.put("Edad", value);

            jugadores.child(user.getUid()).updateChildren(mapa)
                    .addOnSuccessListener((unsed) -> {

                        Toast.makeText(Menu.this, "Edad actualizada", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener((exception) -> {

                Toast.makeText(Menu.this, "Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });


        builder.setNegativeButton("Cancelar", (di, index) -> {
        });

        builder.create().show();

    }


    private void actualizarFoto() {

        perfil = "Imagen";
        String[] opciones = {"Galeria"};

        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        buider.setTitle("Selecciona imagen");
        buider.setItems(opciones, new DialogInterface.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {

                // Galeria
                if (index == 0) {

                    if (!comprobarPermisoAlmacenamiento()) {

                        solicitarPermisoAlmacenamiento();

                    } else elegirImagenGaleria();

                }
            }
        });


        buider.create().show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisoAlmacenamiento() {

        requestPermissions(permisosAlmacenamiento, CODIGO_SOLICITUD_ALMACENAMIENTO);
    }

    /**
     * Verifica si los permisos de almacenamientos estan habilitados o no
     *
     * @return
     */
    private boolean comprobarPermisoAlmacenamiento() {


        boolean resultado = ContextCompat.
                checkSelfPermission(
                        Menu.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return resultado;
    }
    private void usuarioLogeado() {

        if (user != null) {
            consulta();
            Toast.makeText(this, "en linea", Toast.LENGTH_LONG).show();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
    /**
     * Abre la galeria
     */
    private void elegirImagenGaleria() {

        Intent intentGaleria = new Intent(Intent.ACTION_PICK);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria, CODIGO_SELECCION_IMAGEN);
    }


    /**
     * Se llama cuando el usuario permite| denega el cuadro de dialogo
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {


            case CODIGO_SOLICITUD_ALMACENAMIENTO: {

                if (grantResults.length > 0) {

                    boolean escrituraAlmacenamientoAcertado = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (escrituraAlmacenamientoAcertado) {

                        // Permiso fue habilitado

                        elegirImagenGaleria();
                    } else
                        Toast.makeText(this, "Habilite Permiso de la Galeria", Toast.LENGTH_SHORT).show();


                }

            }
            break;
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * Se llama cuando el jugador ya ha elegido la imagen
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {

            // De la img vamos obtener la URI
            if (requestCode == CODIGO_SELECCION_IMAGEN) {

                imagenUri = data.getData();

                subirFoto(imagenUri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Cambia la foto de perfil y actualizado en la base de datos
     *
     * @param imagenUri
     */
    private void subirFoto(Uri imagenUri) {


        String rutaArchivoNombre = rutaAlmecamiento + "" + perfil + "" + user.getUid();

        StorageReference storageReference = referenciaAlmacenamiento.child(rutaArchivoNombre);
        storageReference.putFile(imagenUri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;

            Uri dowloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()) {

                Map<String, Object> resultado = new HashMap<>();
                resultado.put(perfil, dowloadUri.toString());

                jugadores.child(user.getUid()).updateChildren(resultado)
                        .addOnSuccessListener((unsed) -> {

                            Toast.makeText(Menu.this, "Imagen perfil actualizada con exito", Toast.LENGTH_SHORT).show();

                        }).addOnFailureListener((exception) -> {

                    Toast.makeText(Menu.this, "Ha ocurrido un error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else Toast.makeText(Menu.this, "Algo a salido mal", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener((exception) -> {

            Toast.makeText(Menu.this, "Algo a ido mal " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }



    private void cerrarSesion() {
        auth.signOut();
        startActivity(new Intent(Menu.this, MainActivity.class));
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();

    }

    private void consulta() {
        Query query = jugadores.orderByChild("Email").equalTo(user.getEmail());

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String email = ds.child("Email").getValue() + "";
                    String uId = ds.child("Uid").getValue() + "";
                    String nombres = ds.child("Nombres").getValue() + "";
                    String zombies = ds.child("Zombies").getValue() + "";

                    String edad = ds.child("Edad").getValue() + "";
                    String fecha = ds.child("Fecha").getValue() + "";
                    String pais = ds.child("Pais").getValue() + "";
                    String imagen = ds.child("Imagen").getValue() + "";

                    txtCorreoJugadorMenu.setText(email);
                    txtNombreJugadorMenu.setText(nombres);
                    txtZombieMenu.setText(zombies);
                    txtUidJugadorMenu.setText(uId);

                    txtEdadMenu.setText(edad);
                    txtPaisMenu.setText(pais);
                    txtFechaMenu.setText(fecha);

                    if (!imagen.equals("")) {
                        Picasso.get().load(imagen).into(imgPerfil);
                    } else Picasso.get().load(R.drawable.default_perfil).into(imgPerfil);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}