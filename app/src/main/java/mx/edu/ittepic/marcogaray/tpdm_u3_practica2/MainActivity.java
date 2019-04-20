package mx.edu.ittepic.marcogaray.tpdm_u3_practica2;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.JetPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText nmarca, año, fundador, oficina;
    Button insertar, eliminar, actualizar, consultar, insertarauto;
    ListView lista;
    List<Marca> datosConsultaMarcas;

    DatabaseReference servicioRealtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nmarca = findViewById(R.id.marca);
        año = findViewById(R.id.año);
        fundador = findViewById(R.id.fundador);
        oficina = findViewById(R.id.oficina);
        lista = findViewById(R.id.listamarcas);
        insertar = findViewById(R.id.insertar);
        eliminar = findViewById(R.id.eliminar);
        actualizar = findViewById(R.id.actualizar);
        consultar = findViewById(R.id.consultar);
        insertarauto = findViewById(R.id.insertarAuto);

        servicioRealtime = FirebaseDatabase.getInstance().getReference();

        insertarauto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
                finish();
            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarMarca();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarTodos();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarM();
            }
        });

    }

    private void actualizarM(){
        Map<String, Object> map = new HashMap<>();
        Marca marca = new Marca(nmarca.getText().toString(), año.getText().toString(), fundador.getText().toString(), oficina.getText().toString());
        map.put("marcas", marca);

        servicioRealtime.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Bien", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void consultarTodos(){
        servicioRealtime.child("marcas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datosConsultaMarcas = new ArrayList<>();

                servicioRealtime.child("marcas").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            Marca marca = snap.getValue(Marca.class);

                            if(marca!=null){
                                datosConsultaMarcas.add(marca);
                            }
                        }
                        crearListView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void crearListView(){
        if(datosConsultaMarcas.size()<=0){
            return;
        }

        String[] nombres = new String[datosConsultaMarcas.size()];
        for(int i = 0; i<nombres.length;i++){
            Marca j = datosConsultaMarcas.get(i);
            nombres[i] = j.marca;

        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lista.setAdapter(adaptador);

    }

    private void insertarMarca(){
        Marca marca = new Marca(nmarca.getText().toString(), año.getText().toString(), fundador.getText().toString(), oficina.getText().toString());
        servicioRealtime.child("marcas").child(nmarca.getText().toString()).push().setValue(marca)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Insertado con éxito!", Toast.LENGTH_SHORT).show();
                        nmarca.setText("");
                        año.setText("");
                        fundador.setText("");
                        oficina.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al insertar!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void eliminar(){
        AlertDialog.Builder a = new AlertDialog.Builder(this);
        final EditText id = new EditText(this);
        id.setHint("Id a eliminar");
        a.setTitle("Eliminar")
                .setMessage("Ingrese el id a eliminar")
                .setView(id).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarId(id.getText().toString());
            }
        })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarId(String s){
        servicioRealtime.child("marcas").child(s).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al eliminar!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
