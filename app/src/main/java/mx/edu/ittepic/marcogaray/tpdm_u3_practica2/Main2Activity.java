package mx.edu.ittepic.marcogaray.tpdm_u3_practica2;

import android.content.DialogInterface;
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

public class Main2Activity extends AppCompatActivity {
    EditText nombre, modelo, marca, año;
    Button insertar2, eliminar, actualizar, consultar;
    ListView listaautos;
    List<Auto> datosConsultaAutos;

    DatabaseReference servicioRealtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nombre = findViewById(R.id.nombre);
        modelo = findViewById(R.id.modelo);
        marca = findViewById(R.id.marca);
        año = findViewById(R.id.año);
        listaautos = findViewById(R.id.listaautos);
        insertar2 = findViewById(R.id.insertara);
        eliminar = findViewById(R.id.eliminara);
        actualizar = findViewById(R.id.actualizara);
        consultar = findViewById(R.id.consultara);

        servicioRealtime = FirebaseDatabase.getInstance().getReference();

        insertar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarAuto();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarAuto();
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarAuto();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarAutos();
            }
        });

    }
    private void insertarAuto(){
        Auto auto = new Auto(nombre.getText().toString(), modelo.getText().toString(), marca.getText().toString(), año.getText().toString());
        servicioRealtime.child("autos").push().setValue(auto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main2Activity.this, "Insertado con éxito!", Toast.LENGTH_SHORT).show();
                        nombre.setText("");
                        año.setText("");
                        marca.setText("");
                        modelo.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this, "Error al insertar!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void eliminarAuto(){
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
                        Toast.makeText(Main2Activity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this, "Error al eliminar!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarAuto(){
        Map<String, Object> map = new HashMap<>();
        Auto auto = new Auto(nombre.getText().toString(), modelo.getText().toString(), marca.getText().toString(), año.getText().toString());
        map.put("autos", auto);

        servicioRealtime.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Main2Activity.this, "Bien", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main2Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void consultarAutos(){
        servicioRealtime.child("autos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                datosConsultaAutos = new ArrayList<>();

                servicioRealtime.child("autos").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snap : dataSnapshot.getChildren()){
                            Auto auto = snap.getValue(Auto.class);

                            if(marca!=null){
                                datosConsultaAutos.add(auto);
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
        if(datosConsultaAutos.size()<=0){
            return;
        }

        String[] nombres = new String[datosConsultaAutos.size()];
        for(int i = 0; i<nombres.length;i++){
            Auto j = datosConsultaAutos.get(i);
            nombres[i] = j.marca;

        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        listaautos.setAdapter(adaptador);
    }

}
