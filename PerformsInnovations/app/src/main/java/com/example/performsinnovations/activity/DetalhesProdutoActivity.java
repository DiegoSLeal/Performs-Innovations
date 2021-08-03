package com.example.performsinnovations.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo, descricao, estado, preco;
    private Anuncio anuncioSelecionado;
    private RecyclerView recyclerPropaganda;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaPropaganda = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private FirebaseAuth autenticacao;
    private int posicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        //Configurar toolbar
        getSupportActionBar().setTitle("Detalhe produto");

        //Recuperar anúncio para exibicao
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
//        posicao = getIntent().getIntExtra("position", 0);
//        Log.d("TAG", "posicao" + posicao);


        //Recuperando anuncios firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot estados: snapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaPropaganda.add(anuncio);
                        }
                    }
                }
                //PRECISA ARRUMAR, EXCLUIR ITEM DO CARROCEU ANUNCIO
                //listaPropaganda.remove(posicao);
                Collections.reverse(listaPropaganda);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Inicializar componentes de interface
        inicializarComponentes();

        recyclerPropaganda.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        recyclerPropaganda.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaPropaganda, this);
        recyclerPropaganda.setAdapter(adapterAnuncios);

        recyclerPropaganda.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerPropaganda,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Anuncio anuncioSelecionado = listaPropaganda.get(position);
                                posicao = position;
                                Intent i = new Intent(DetalhesProdutoActivity.this, DetalhesProdutoActivity.class);
                                i.putExtra("anuncioSelecionado", anuncioSelecionado);
                                i.putExtra("position", position);
                                startActivity(i);


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

        if(anuncioSelecionado != null){

            titulo.setText(anuncioSelecionado.getTitulo());
            descricao.setText(anuncioSelecionado.getDescricao());
            estado.setText(anuncioSelecionado.getEstado());
            preco.setText(anuncioSelecionado.getValor());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                   String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

            carouselView.setImageClickListener(new ImageClickListener() {
                @Override
                public void onClick(int position) {
                    openClassImagemDetalhes();
                }
            });
        }
    }

    public void visualizarTelefone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(),null));
        startActivity(i);
    }
    private void inicializarComponentes(){
        recyclerPropaganda = findViewById(R.id.recyclerPropaganda);
        carouselView = findViewById(R.id.carouselDetalhe);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);
        preco = findViewById(R.id.textPrecoDetalhe);
    }

    public void openClassImagemDetalhes(){
        Intent intent = new Intent(DetalhesProdutoActivity.this, DetalhesImagem.class);
        intent.putExtra("anuncioSelecionado", anuncioSelecionado);
        startActivity(intent);
    }

}