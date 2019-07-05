package com.digao.ytsbrowser.Data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digao.ytsbrowser.Model.Movie;
import com.digao.ytsbrowser.Model.Torrent;
import com.digao.ytsbrowser.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private Context context;

    private List<Movie> movieList;
    private final String IMDBURL = "http://www.imdb.com/title/";

    public MovieRecyclerViewAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movieList = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = movieList.get(position);

        Torrent torrentAux;
        int indxTorrent;

        String posterLink = movie.getCover();
        holder.title.setText(movie.getTitle());


        holder.year.setText(movie.getYear() + " / " + movie.getLanguage());
        holder.genre.setText(movie.getGenre());
        holder.synopsis.setText(movie.getSinopse());

        holder.btImdb.setText(movie.getRating());
        holder.btImdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(IMDBURL + movie.getImdbId())));
            }
        });
        holder.bt3d.setVisibility(View.INVISIBLE);
        holder.bt720p.setVisibility(View.INVISIBLE);
        holder.bt1080p.setVisibility(View.INVISIBLE);
        holder.imgTrailer.setVisibility(View.VISIBLE);
        //holder.synopsis.setText(movie.getCode_trailer());
        if (movie.getCode_trailer().isEmpty()) {
            holder.imgTrailer.setVisibility(View.INVISIBLE);
        }
        holder.imgTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getLinkTrailer())));
            }
        });
        indxTorrent = movie.indexOfTorrent("1080p");
        if (indxTorrent >= 0) {
            torrentAux = movie.getTorrent(indxTorrent);
            holder.bt1080p.setVisibility(View.VISIBLE);
            holder.bt1080p.setText("1080p (" + torrentAux.getSeeds() + ")\n" + torrentAux.getType() + "\n" + torrentAux.getSize());
            final String magnet1080 = torrentAux.getMagnetLink(movie.getTitle(), context);
            if (!magnet1080.isEmpty()) {
                holder.bt1080p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openUrl(view, magnet1080);
                    }
                });
            }


        }

        indxTorrent = movie.indexOfTorrent("720p");
        if (indxTorrent >= 0) {
            torrentAux = movie.getTorrent(indxTorrent);
            holder.bt720p.setVisibility(View.VISIBLE);
            holder.bt720p.setText("720p (" + torrentAux.getSeeds() + ")\n" + torrentAux.getType() + "\n" + torrentAux.getSize());
            final String magnet720p = torrentAux.getMagnetLink(movie.getTitle(), context);
            if (!magnet720p.isEmpty()) {
                holder.bt720p.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openUrl(view, magnet720p);
                        //view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(magnet720p)));
                    }
                });
            }

        }

        indxTorrent = movie.indexOfTorrent("3D");
        if (indxTorrent >= 0) {
            torrentAux = movie.getTorrent(indxTorrent);
            holder.bt3d.setVisibility(View.VISIBLE);
            holder.bt3d.setText("3D (" + torrentAux.getSeeds() + ")\n" + torrentAux.getType() + "\n" + torrentAux.getSize());
            final String magnet3d = torrentAux.getMagnetLink(movie.getTitle(), context);
            if (!magnet3d.isEmpty()) {
                holder.bt3d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openUrl(view, magnet3d);
                        //view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(magnet3d)));
                    }
                });
            }
        }


        try {
            Picasso.get().load(posterLink).placeholder(R.drawable.film000)
                    .into(holder.cover);
        } catch (Exception e) {
            //e.printStackTrace();
            //Toast.makeText( context, "erro ao carregar poster!", Toast.LENGTH_LONG).show();

            Log.e("error: ", "Can't Reach: " + posterLink);
        }
    }

    public void openUrl(View view, String magnetLink) {
        Intent zIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(magnetLink));
        if (zIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
            view.getContext().startActivity(zIntent);
        } else {
            Toast.makeText(view.getContext(), "Não existe um app associado ao protocolo MAGNET !!!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, year, genre, synopsis;
        Button btImdb, bt3d, bt720p, bt1080p;
        ImageView cover, imgTrailer;
        Context ctx;


        public ViewHolder(View itemView, final Context ctx) {
            super(itemView);
            this.ctx = ctx;
            title = itemView.findViewById(R.id.movieTitleID);
            cover = itemView.findViewById(R.id.movieImageID);
            year = itemView.findViewById(R.id.movieYear);
            genre = itemView.findViewById(R.id.movieGenre);
            synopsis = itemView.findViewById(R.id.movieSynopsis);


            btImdb = itemView.findViewById(R.id.btImdb);
            imgTrailer = itemView.findViewById(R.id.imgTrailer);
            bt3d = itemView.findViewById(R.id.bt3D);
            bt720p = itemView.findViewById(R.id.bt720);
            bt1080p = itemView.findViewById(R.id.bt1080);


/*
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText( view.getContext(),"Row Tapped !", Toast.LENGTH_LONG).show();
                    Movie movie = movieList.get(getAdapterPosition());
                    Intent intent = new Intent(ctx, MovieDetailActivity.class);
                    intent.putExtra("movie", movie);
                    ctx.startActivity(intent);


                }
            }); */
        }

        @Override
        public void onClick(View view) {

        }
    }
}