package com.example.performsinnovations.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.performsinnovations.R;
import com.example.performsinnovations.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;
    private boolean exibirLixeira;


    public AdapterAnuncios(List<Anuncio> anuncios, Context context, Boolean exibirLixeira) {
        this.anuncios = anuncios;
        this.context = context;
        this.exibirLixeira = exibirLixeira;


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView valor;
        ImageView foto;
        ImageButton buttonlixeira;

        public MyViewHolder(View itemView) {
            super(itemView);


            titulo = itemView.findViewById(R.id.textTitulo);
            valor = itemView.findViewById(R.id.textPreco);
            foto = itemView.findViewById(R.id.imageAnuncio);
            buttonlixeira = itemView.findViewById(R.id.buttonlixeira);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Anuncio anuncio = anuncios.get(position);
        myViewHolder.titulo.setText(anuncio.getTitulo());
        myViewHolder.valor.setText(anuncio.getValor());
        myViewHolder.buttonlixeira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Você deseja excluir ")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Anuncio anuncioSelecionado = anuncios.get(position);
                                anuncioSelecionado.remover();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                   }
                               })
                        .create()
                        .show();
            }

        });

        if(exibirLixeira == true ){
            myViewHolder.buttonlixeira.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.buttonlixeira.setVisibility(View.GONE);
        }

        //pegar a primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }


}
