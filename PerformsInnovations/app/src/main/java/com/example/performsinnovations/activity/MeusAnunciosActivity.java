package com.example.performsinnovations.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.performsinnovations.adapter.AdapterAnuncios;
import com.example.performsinnovations.helper.ConfiguracaoFirebase;
import com.example.performsinnovations.model.Anuncio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.example.performsinnovations.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private AlertDialog dialog;
    private AlertDialog.Builder alerta;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        //configurações iniciais
        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());
        inicializarComponentes();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Evento de click





    }

    public void inicializarComponentes() {

        alerta = new AlertDialog.Builder(this);

        //Configurar Recyclerview
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncios, this,true);
        recyclerAnuncios.setAdapter(adapterAnuncios);
        adapterAnuncios.notifyDataSetChanged();
        //Recupera anuncios para o usuario
        recuperarAnuncios();

        //Adicionar evento de clique no recyclerview
//        recyclerAnuncios.addOnItemTouchListener(
//                new RecyclerItemClickListener(
//                        this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        String titulo = anuncios.get(position).getTitulo();
//                        //DELETAR ANUNCIO
//                        //Alerta excluir anuncio
////                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                        alerta.setTitle("Excluir o anuncio " + "\"" + titulo + "\"?")
//                                .setMessage("Você realmente quer excluir o anúncio?")
//
//                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Anuncio anuncioSelecionado = anuncios.get(position);
//                                        anuncioSelecionado.remover();
//                                        adapterAnuncios.notifyDataSetChanged();
//                                        Toast.makeText(MeusAnunciosActivity.this, "Anúncio " + titulo + " excluido!", Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                })
//                                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                })
//                                .create()
//                                .show();
//                    }
//
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    }
//                }
//                )
//        );
    }



    private void recuperarAnuncios() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncios.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(anuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}