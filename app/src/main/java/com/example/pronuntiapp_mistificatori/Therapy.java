package com.example.pronuntiapp_mistificatori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Therapy extends AppCompatActivity {

    private String codiceBimbo, nome;
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://pronuntiapp---mistificatori-default-rtdb.europe-west1.firebasedatabase.app");
    private FirebaseUser currentUser;
    private DatabaseReference myRef;
    private LinearLayout lista;
    private int uiMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapy);
        codiceBimbo = getIntent().getStringExtra("codice");
        nome = getIntent().getStringExtra("nome");

        lista = findViewById(R.id.listTherapy);
        uiMode = getResources().getConfiguration().uiMode;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Abilita il pulsante per tornare indietro
            actionBar.setTitle(nome + " - " + Therapy.this.getString(R.string.therapy));
        }

        String percorso = "logopedisti/ABC/Pazienti/" + codiceBimbo + "/Visite";
        myRef = database.getReference(percorso);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Date> dateList = new ArrayList<>();
                ArrayList<String> timeList = new ArrayList<>();

                // Itera attraverso tutti i dati nel database
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String dateStr = dateSnapshot.getKey();
                    String time = dateSnapshot.getValue(String.class);

                    // Converte la stringa della data in un oggetto Date
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = null;
                    try {
                        date = formatter.parse(dateStr);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Aggiungi data e ora alle rispettive liste
                    dateList.add(date);
                    timeList.add(time);
                }

                // Ordina le date
                Collections.sort(dateList);

                // Visualizza le date ordinate nella tua UI
                for (Date date : dateList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    String dateStr = formatter.format(date);
                    int index = dateList.indexOf(date);
                    String time = timeList.get(index);

                    View childView = getLayoutInflater().inflate(R.layout.list_element_parent, null);
                    TextView textViewData = childView.findViewById(R.id.esData);
                    textViewData.setText(dateStr);
                    TextView textViewTipo = childView.findViewById(R.id.esTipo);
                    LinearLayout bordi = childView.findViewById(R.id.bordo);
                    String clock = Therapy.this.getString(R.string.at) + time;
                    textViewTipo.setText(clock);
                    if((uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                        bordi.setForeground(getResources().getDrawable(R.drawable.border_drawable_white));
                    else    bordi.setForeground(getResources().getDrawable(R.drawable.border_drawable_black));
                    ImageView result = childView.findViewById(R.id.esResult);
                    result.setVisibility(View.GONE);

                    lista.addView(childView);

                    Space space = new Space(Therapy.this);
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

    public void call(View view) {
        DatabaseReference ref = database.getReference("logopedisti/ABC/tel");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tel = snapshot.getValue(String.class);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tel));
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}