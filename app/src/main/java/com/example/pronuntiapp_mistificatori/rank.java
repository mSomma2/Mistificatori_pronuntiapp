package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class rank extends AppCompatActivity {

    private String codiceBimbo, nome;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private LinearLayout lista;
    private int uiMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        codiceBimbo = getIntent().getStringExtra("codice");
        //nome = getIntent().getStringExtra("nome");

        lista = findViewById(R.id.listTherapy);

        uiMode = getResources().getConfiguration().uiMode;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Abilita il pulsante per tornare indietro
            actionBar.setTitle(rank.this.getString(R.string.rank));
        }

        String percorso = "logopedisti/ABC/Pazienti/";
        myRef = database.getReference(percorso);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> code = new ArrayList<>();
                ArrayList<String> nome = new ArrayList<>();
                ArrayList<Integer> punteggio = new ArrayList<Integer>();

                // Itera attraverso tutti i dati nel database
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    code.add(dateSnapshot.getKey());
                    nome.add(dateSnapshot.child("Nome").getValue(String.class));
                    punteggio.add(dateSnapshot.child("punteggio").getValue(Integer.class));
                }

                for (int i = 0; i < punteggio.size() - 1; i++) {
                    for (int j = i + 1; j < punteggio.size(); j++) {
                        if (punteggio.get(i) < punteggio.get(j)) {
                            // Scambia i valori nei tre ArrayList
                            Collections.swap(code, i, j);
                            Collections.swap(nome, i, j);
                            Collections.swap(punteggio, i, j);
                        }
                    }
                }

                for(int i = 0; i < punteggio.size(); i++){
                    View childView = getLayoutInflater().inflate(R.layout.list_element_parent, null);
                    TextView textViewData = childView.findViewById(R.id.esData);
                    textViewData.setText(nome.get(i));
                    TextView textViewTipo = childView.findViewById(R.id.esTipo);
                    LinearLayout bordi = childView.findViewById(R.id.bordo);
                    String points = rank.this.getString(R.string.punteggio) + punteggio.get(i);
                    textViewTipo.setText(points);
                    ImageView result = childView.findViewById(R.id.esResult);
                    if(i == 0) result.setImageResource(R.drawable.first);
                    else if(i == 1) result.setImageResource(R.drawable.second);
                    else if (i == 2) result.setImageResource(R.drawable.third);
                    else result.setVisibility(View.GONE);

                    if(Objects.equals(code.get(i), codiceBimbo)){
                        bordi.setBackground(getResources().getDrawable(R.drawable.border_drawable_green_fill));
                        textViewTipo.setTextColor(getResources().getColor(R.color.black));
                    }else if((uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                        bordi.setForeground(getResources().getDrawable(R.drawable.border_drawable_white));
                    else bordi.setForeground(getResources().getDrawable(R.drawable.border_drawable_black));

                    lista.addView(childView);

                    Space space = new Space(rank.this);
                    space.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80)); // Altezza dello spazio: 16dp
                    lista.addView(space);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}