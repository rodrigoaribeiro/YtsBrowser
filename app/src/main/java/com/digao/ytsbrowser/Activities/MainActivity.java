package com.digao.ytsbrowser.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.digao.ytsbrowser.Data.MovieRecyclerViewAdapter;
import com.digao.ytsbrowser.Model.Movie;
import com.digao.ytsbrowser.Model.Torrent;
import com.digao.ytsbrowser.R;
import com.digao.ytsbrowser.Utils.UtilsAndConst;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int page = 1;

    private UtilsAndConst utils;
    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;
    private RequestQueue queue;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog dialog;
    private String searchTerm;


//https://yts.am/api/v2/list_movies.json?sort_by=seeds&order_by=desc&limit=10&page=1
/*
 sugested trackers
 udp://open.demonii.com:1337/announce
- udp://tracker.openbittorrent.com:80
- udp://tracker.coppersurfer.tk:6969
- udp://glotorrents.pw:6969/announce
- udp://tracker.opentrackr.org:1337/announce
- udp://torrent.gresille.org:80/announce
- udp://p4p.arenabg.com:1337
- udp://tracker.leechers-paradise.org:6969


 open url ....
 Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
Intent intent = new Intent(Intent.ACTION_VIEW, uri);
startActivity(intent);

OR startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com"))

 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, getString(R.string.APPLICATION_ID));

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);
/* /
MobileAds.initialize(this, getString(R.string.APPLICATION_ID) ) ;
        //MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
*/
        searchTerm = "";
        //                showInputDialog();

        queue = Volley.newRequestQueue(this);

        utils = new UtilsAndConst();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieList = new ArrayList<>();
        //Prefs prefs = new Prefs(MainActivity.this);


        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this, movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        getMovies("", 0);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_settings);
        final SearchView searchview = (SearchView) item.getActionView();
        searchview. setIconifiedByDefault(true);
        searchview.
        setQueryHint( "nome do filme");

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                page=1;
                if (movieList.size()>0) {
                    getMovies(s, page );
                    invalidateOptionsMenu();
                }
                return  movieList.size()>0;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //showInputDialog();
            return true;
        }
        if ( id == R.id.action_next) {

            getMovies(searchTerm,++page );
            movieRecyclerViewAdapter.notifyDataSetChanged();

        }
        if ( id == R.id.action_reset) {
            page=0;
            searchTerm="";
            getMovies("",page);
        }
        if ( id == R.id.action_prior  && page>1) {
            //page--;
            getMovies("",page--);
            if (page == 1) {

            }

        }

        return super.onOptionsItemSelected(item);
    }
    public void showInputDialog() {
        alertBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view,null);
        final EditText newSearchEdit = (EditText) view.findViewById( R.id.searchEdit);
        Button btSearch = (Button) view.findViewById(R.id.searchButton);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prefs prefs = new Prefs(MainActivity.this);
                if (!newSearchEdit.getText().toString().isEmpty()) {
                    getMovies( newSearchEdit.getText().toString(),0 );
                 //   prefs.setSearch(newSearchEdit.getText().toString());
                    //movieRecyclerViewAdapter.notifyDataSetChanged();
                    //movieRecyclerViewAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        alertBuilder.setView(view);
        dialog = alertBuilder.create();
        dialog.show();

    }

    public List<Movie> getMovies(final String searchValue, int page) {
        trimCache(this);
        movieList.clear();
        //movieRecyclerViewAdapter.notifyDataSetChanged();

        String URL_SEARCH = utils.getURL();
        if (!searchValue.isEmpty()) {
            searchTerm = searchValue;
            try {
                URL_SEARCH = URL_SEARCH + "&query_term=" + URLEncoder.encode(searchValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (page == 0) {
            this.page = 1;
        }
        URL_SEARCH = URL_SEARCH + "&page=" + String.valueOf(page);
/*
        if ( pagAnterior != null) {
           pagAnterior.setVisible(this.page >1);
        }
*/
        Log.d("URL", URL_SEARCH);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL_SEARCH
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String genre;
                    Log.d("movieJ",data.getString("movie_count")+" " +data.getString("page_number"));
                    if ((data.getInt("movie_count") > 0) && (data.has("movies"))) {

                        JSONArray movieArray = data.getJSONArray("movies");
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject movieObj = movieArray.getJSONObject(i);
                            genre = "";
                            Movie movie = new Movie();
                            movie.setId(movieObj.getInt("id"));
                            movie.setCover(movieObj.getString("small_cover_image"));
                            movie.setYear(movieObj.getString("year"));
                            movie.setLanguage(movieObj.getString("language"));
                            movie.setTitle(movieObj.getString("title"));
                            movie.setRating(movieObj.getString("rating"));
                            movie.setImdbId(movieObj.getString("imdb_code"));
                            movie.setSinopse(movieObj.getString("synopsis"));
                            JSONArray genres = movieObj.getJSONArray("genres");
                            for (int z = 0; z < genres.length(); z++) {
                                if (z > 0) {
                                    genre = genre + " / ";
                                }
                                genre = genre + genres.getString(z);
                            }
                            movie.setGenre(genre);
                            JSONArray ztorr = movieObj.getJSONArray("torrents");
                            for (int z = 0; z < ztorr.length(); z++) {
                                JSONObject objTorrent = ztorr.getJSONObject(z);
                                Torrent torrent = new Torrent();
                                torrent.setHash(objTorrent.getString("hash"));
                                torrent.setQuality(objTorrent.getString("quality"));
                                torrent.setSize(objTorrent.getString("size"));
                                torrent.setSeeds(objTorrent.getInt("seeds"));
                                torrent.setPeers(objTorrent.getInt("peers"));
                                torrent.setUrl(objTorrent.getString("url"));
                                movie.addTorrent(torrent);
                            }

                            movieList.add(movie);
                            movieRecyclerViewAdapter.notifyDataSetChanged();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
        return movieList;
    }


    @Override
    protected void onStop(){
        super.onStop();
    }

    //Fires after the OnStop() state
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }




}

/*
YtsBrowser é um browser para pesquisas do site https://yts.am baseado na API de web services REST disponível em https://yts.am/api  todas as informações estão no site, o aplicativo somente facilita a navegação e pesquisa.
 */