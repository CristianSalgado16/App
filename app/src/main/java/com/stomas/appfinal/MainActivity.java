package com.stomas.appfinal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText txtCodigoDisco, txtNombreAlbum, txtArtista, txtAnoLanzamiento;
    private ListView lista;
    private Spinner spGenero;
    private FirebaseFirestore db;
    String[] EstiloMusical ={"Rock", "Electronica", "Clasica", "Folklore"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CargarListaFirestore();
        db = FirebaseFirestore.getInstance();

        txtCodigoDisco = findViewById(R.id.txtCodigoDisco);
        txtNombreAlbum = findViewById(R.id.txtNombreAlbum);
        txtArtista = findViewById(R.id.txtArtista);
        txtAnoLanzamiento = findViewById(R.id.txtAnoLanzamiento);
        spGenero = findViewById(R.id.spGenero);
        lista = findViewById(R.id.lista);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EstiloMusical);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spGenero.setAdapter(adapter);
    }
        public void enviarDatosFirestore(View view){

        String codigo = txtCodigoDisco.getText().toString();
        String nombre = txtNombreAlbum.getText().toString();
        String artista = txtArtista.getText().toString();
        String anolanzamiento = txtAnoLanzamiento.getText().toString();
        String generomusical = spGenero.getSelectedItem().toString();

        Map<String, Object> genero = new HashMap<>();
        genero.put("codigo", codigo);
        genero.put("nombre", nombre);
        genero.put("artista",artista);
        genero.put("anolanzamiento", anolanzamiento);
        genero.put("generomusical", generomusical);

            db.collection("genero")
                    .document(codigo)
                    .set(genero)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this, "Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error al enviar los datos a Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }

    public void CargarLista(View view){
        CargarListaFirestore();
    }
    public void CargarListaFirestore(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("genero")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> listaGenero = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String linea ="||" + document.getString("codigo") + " || " +
                                        document.getString("nombre") + " || " +
                                        document.getString("artista") + " || " +
                                        document.getString("anolanzamiento") + " || " +
                                        document.getString("generomusical");
                                listaGenero.add(linea);
                            }

                            ArrayAdapter<String> adapter= new ArrayAdapter<>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listaGenero
                            );
                            lista.setAdapter(adapter);
                        } else {
                            Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });


    }
}