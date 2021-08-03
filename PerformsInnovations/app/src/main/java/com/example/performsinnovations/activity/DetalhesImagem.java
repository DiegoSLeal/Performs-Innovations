package com.example.performsinnovations.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.performsinnovations.R;
import com.example.performsinnovations.model.Anuncio;
import com.synnapps.carouselview.CarouselView;

public class DetalhesImagem extends AppCompatActivity {

    private Anuncio anuncioSelecionado;
    private CarouselView carouselDetalhes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_imagem);

        Intent i = new Intent(getApplicationContext(), DetalhesProdutoActivity.class);
        startActivity(i);
    }
}