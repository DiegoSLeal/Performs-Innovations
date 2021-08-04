package com.example.performsinnovations.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.performsinnovations.R;
import com.example.performsinnovations.adapter.AdapterAnuncios;
import com.example.performsinnovations.helper.ConfiguracaoFirebase;
import com.example.performsinnovations.helper.RecyclerItemClickListener;
import com.example.performsinnovations.model.Anuncio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria, buttonLimpar;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;
    private TextView textViewLimpar;
    private List<String> listaAnuncios2 = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        //configuração iniciais firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
        buttonLimpar = findViewById(R.id.buttonLimpar);
        textViewLimpar = findViewById(R.id.textViewLimpar);

        //Configurar RecyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this, false);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        //Evento de click


        //Configurações iniciais
        recuperarAnunciosPublicos();
        selecionarAnuncio();
    }

    public void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        listaAnuncios.clear();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot estados: snapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);
                        }
                    }
                    String key = estados.getKey();
                    listaAnuncios2.add(key);
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void selecionarAnuncio(){

        //Aplicar evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnunciosPublicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Anuncio anuncioSelecionado = listaAnuncios.get(position);
                                Intent intent = new Intent(AnunciosActivity.this, DetalhesProdutoActivity.class);
                                intent.putExtra("anuncioSelecionado", anuncioSelecionado);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    public void filtrarPorEstado(View view){
        AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o estado desejado");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        //Configurar spinner de estados
        Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);

        //Configurar tela de seleção de estado
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                listaAnuncios2
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        dialogEstado.setView(viewSpinner);
        dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                buttonLimpar.setVisibility(View.VISIBLE);
                textViewLimpar.setVisibility(View.VISIBLE);
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                recuperarAnunciosPorEstado();
                filtrandoPorEstado = true;
            }
        });
        dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = dialogEstado.create();
        dialog.show();
    }

    public void filtarPorCategoria(View view){

        if (filtrandoPorEstado == true){

            AlertDialog.Builder dialogEstado = new AlertDialog.Builder(this);
            dialogEstado.setTitle("Selecione a categoria desejado");

            //Configurar spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            //Configurar spinner de categorias
            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);
            String[] categorias = getResources().getStringArray(R.array.categorias);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item,
                    categorias
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapter);

            dialogEstado.setView(viewSpinner);
            dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buttonLimpar.setVisibility(View.VISIBLE);
                    textViewLimpar.setVisibility(View.VISIBLE);
                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                    recuperarAnunciosPorCategoria();
                }
            });
            dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = dialogEstado.create();
            dialog.show();

        }else{
            Toast.makeText(this, "Escolha primeiro uma região",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void recuperarAnunciosPorCategoria(){
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configurar nó por categoria

        anunciosPublicosRef
                .child(filtroEstado)
                .child(filtroCategoria)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAnuncios.clear();

                for(DataSnapshot anuncios: snapshot.getChildren()){
                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    listaAnuncios.add(anuncio);
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void recuperarAnunciosPorEstado(){

        //Configurar nó por estado
        anunciosPublicosRef
                .child(filtroEstado)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaAnuncios.clear();
                for (DataSnapshot categorias : snapshot.getChildren()) {
                    for (DataSnapshot anuncios : categorias.getChildren()) {
                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        listaAnuncios.add(anuncio);
                    }
                    Collections.reverse(listaAnuncios);
                    adapterAnuncios.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void limparFiltros(View view){
        listaAnuncios.clear();
        filtrandoPorEstado = false;
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot estados: snapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);
                        }
                    }
                }

                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        buttonLimpar.setVisibility(View.GONE);
        textViewLimpar.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(autenticacao.getCurrentUser() == null){//usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else{//usuario logado
            menu.setGroupVisible(R.id.group_logado,true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
                break;
            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}