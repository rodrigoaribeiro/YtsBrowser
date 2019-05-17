package com.digao.ytsbrowser.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.digao.ytsbrowser.Data.MovieRecyclerViewAdapter;
import com.digao.ytsbrowser.Model.Movie;
import com.digao.ytsbrowser.Model.Torrent;
import com.digao.ytsbrowser.R;
import com.digao.ytsbrowser.Utils.CfGlobal;
import com.digao.ytsbrowser.Utils.UtilsAndConst;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int pagina = 1;


    CfGlobal config;
    Menu main_menu;
    private UtilsAndConst utils;
    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;
    private RequestQueue queue;
    private AlertDialog.Builder alertBuilder;
    private AlertDialog dialog;
    private String searchTerm;
    private InterstitialAd interstitialAd;
    private RadioGroup idGrQlty;


//https://yts.am/api/v2/list_movies.json?sort_by=seeds&order_by=desc&limit=10&page=1
/*
 sugested trackers
 udp://open.demonii.com:1337/announce
- udp://tracker.openbittorrent.com:80
- udp://tracker.coppersurfer.tk:6969
- udp://glotorrents.pw:6969/announce
- udp://tracker.opentrackr.org:1337/announce
- udp://torrent.gresille.org:80/announcef
- udp://p4p.arenabg.com:1337
- udp://tracker.leechers-paradise.org:6969


 open url ....
 Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
Intent intent = new Intent(Intent.ACTION_VIEW, uri);
startActivity(intent);

OR startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.google.com"))

 */

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
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
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    void setSearchVisivel(Boolean visivel, Boolean criando) {
        EditText search = findViewById(R.id.edSearch);
        TextView textView2 = findViewById(R.id.textView2);
        View viewDiv = findViewById(R.id.view3);
        ImageView btProcura = findViewById(R.id.btProcura);
        if (!criando) {
            MenuItem itemenu = main_menu.findItem(R.id.action_settings);
            itemenu.setVisible(!visivel);
        }
        if (visivel) {
            btProcura.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            viewDiv.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            idGrQlty.setVisibility(View.VISIBLE);

            //R.id.action_settings


        } else {
            btProcura.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
            viewDiv.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            idGrQlty.setVisibility(View.INVISIBLE);
        }
        /* Work with Constraint */
        ConstraintSet set = new ConstraintSet();
        ConstraintLayout layout;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        layout = findViewById(R.id.constrMain);
        set.clone(layout);
        // The following breaks the connection.
        if (!visivel) {


            set.connect(R.id.recyclerView, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            //set.clear(R.id.recyclerView, ConstraintSet.TOP);
            // Comment out line above and uncomment line below to make the connection.
            // set.connect(R.id.bottomText, ConstraintSet.TOP, R.id.imageView, ConstraintSet.BOTTOM, 0);
        } else {
            set.connect(R.id.recyclerView, ConstraintSet.TOP, R.id.view3, ConstraintSet.BOTTOM, 0);

        }
        set.applyTo(layout);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        config = CfGlobal.getInstance(getApplicationContext());
        final ImageView btProcura = findViewById(R.id.btProcura);
        EditText search = findViewById(R.id.edSearch);
        idGrQlty = findViewById(R.id.idGrQld);
        idGrQlty.clearCheck();
        int vIndex = 0;

        if (config.getQuality().isEmpty()) {
            vIndex = 0;

        } else {
            vIndex = java.util.Arrays.asList(getResources().getStringArray(R.array.arrayQlty)).indexOf(config.getQuality());

        }
        idGrQlty.check(getResources().getIdentifier("rButton" + vIndex, "id", getPackageName()));


        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    btProcura.callOnClick();
                    return true;
                }
                return false;
            }
        });
//        imeOptions
        setSearchVisivel(false, true);
        btProcura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText search = findViewById(R.id.edSearch);
                if (!search.getText().toString().isEmpty()) {
                    InputMethodManager imm = (InputMethodManager) search.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    searchTerm = search.getText().toString();
                    getMovies(searchTerm, 0);
                }
            }
        });

        deleteCache(this);
        MobileAds.initialize(this, getString(R.string.APPLICATION_ID));

        AdView adview = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F8E6C0A9E1C2B73E4EA164ADC9A3BDB0").build();
        // Retornar na produção -->         AdRequest adRequest = new AdRequest.Builder().build();


        adview.loadAd(adRequest);

        // Create the InterstitialAd and set the adUnitId.
        interstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId(getString(R.string.interstitial));
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                AdRequest zdRequest = new AdRequest.Builder().addTestDevice("F8E6C0A9E1C2B73E4EA164ADC9A3BDB0").build();
//Retornar na Produção --> AdRequest zdRequest = new AdRequest.Builder().build();
                interstitialAd.loadAd(zdRequest);
            }
        });



        /* /
MobileAds.initialize(this, getString(R.string.APPLICATION_ID) ) ;
        //MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
*/
        searchTerm = "";
        //                showInputDialog();

        queue = Volley.newRequestQueue(this);

        utils = new UtilsAndConst();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieList = new ArrayList<>();
        //Prefs prefs = new Prefs(MainActivity.this);


        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this, movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        getMovies("", 0);

    }

    public void showInputDialog() {
        alertBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText newSearchEdit = view.findViewById(R.id.searchEdit);
        Button btSearch = view.findViewById(R.id.searchButton);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prefs prefs = new Prefs(MainActivity.this);
                if (!newSearchEdit.getText().toString().isEmpty()) {
                    getMovies(newSearchEdit.getText().toString(), 0);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        main_menu = menu;
        MenuItem zitem = main_menu.findItem(R.id.action_prior);

        zitem.setVisible(false);

//        final MenuItem item = menu.findItem(R.id.action_settings);
        //SearchView searchview = (SearchView) item.getActionView();
        //searchview.setVisibility(View.INVISIBLE);
        /* 05052019 final SearchView searchview = (SearchView) item.getActionView();
        searchview.setIconifiedByDefault(true);
        searchview.
                setQueryHint("nome do filme");

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if (movieList.size() > 0) {
                    getMovies(s, 0);
                    invalidateOptionsMenu();
                }
                return movieList.size() > 0;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        }); */
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
            setSearchVisivel(true, false);//            Toast.makeText(getApplicationContext(), "clicou na bagaça", Toast.LENGTH_LONG).show();
            EditText search = findViewById(R.id.edSearch);
            search.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);

            //item.setVisible(false);
//
            return true;
        }
        if (id == R.id.action_next) {

            getMovies(searchTerm, ++pagina);
            movieRecyclerViewAdapter.notifyDataSetChanged();

        }
        if (id == R.id.action_reset) {
            //Crashlytics.getInstance().crash();
            setSearchVisivel(false, false);
            setPagina(0);
            searchTerm = "";
            getMovies("", 0);
        }
        if (id == R.id.action_prior && pagina > 1) {
            //page--;

            getMovies(searchTerm, pagina--);
            item.setVisible(pagina > 1);


        }
        if (id == R.id.action_cfg) {

            startActivityForResult(new Intent(this, ActConfig.class), 99);
            // necessário ? getMovies("", 0);

        }

        return super.onOptionsItemSelected(item);
    }

    public List<Movie> getMovies(final String searchValue, int page) {


        movieList.clear();

        //movieRecyclerViewAdapter.notifyDataSetChanged();
        if (main_menu != null) {
            MenuItem item = main_menu.findItem(R.id.action_prior);

            item.setVisible(page > 1);
        }
        int vIndex = -1;
        if (idGrQlty.getVisibility() != View.GONE) {

            vIndex = idGrQlty.indexOfChild(idGrQlty.findViewById(idGrQlty.getCheckedRadioButtonId()));

        }
        String URL_SEARCH = config.getURL(searchValue, page, vIndex);
        Log.d("search", URL_SEARCH);
        /*
        String URL_SEARCH =utils.getURL();
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
        //Log.d("URL", URL_SEARCH);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL_SEARCH
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    String genre;
                    //Log.d("movieJ",data.getString("movie_count")+" " +data.getString("page_number"));
                    if (data.getInt("page_number") > 0) {
                        setPagina(data.getInt("page_number"));
                    }
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
                                torrent.setType(objTorrent.getString("type"));
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
                //Log.d("volley" , error.toString());
                //Log.d("volley","-------------------------------------------------" );
                //Log.d("volley",error.getMessage());
                //VolleyLog.d(error.toString());
                //Log.d("")
                Toast.makeText(getApplicationContext(), "Erro ao acessar https://" + config.getDomain() + "\nTente mais tarde por favor!\n" + error.toString(), Toast.LENGTH_LONG).show();
//                VolleyLog.d(error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
        if (main_menu != null) {
            MenuItem zitem = main_menu.findItem(R.id.action_next);
            zitem.setVisible(movieList.size() >= config.getLIMIT());
        }
        return movieList;
    }

    public void setPagina(int pagenum) {
        this.pagina = pagenum;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Retrieve data in the intent
        //String editTextValue = intent.getStringExtra("valueId");
        if (requestCode == 99) {
            int random = new Random().nextInt((99 - 0) + 1) + 0; // get a random number between 0 and 99
            if (random % 3 == 0) { // if the number is multiple of 3
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        }
        getMovies("", 0);
    }
}

/*
YtsBrowser é um browser para pesquisas do site https://yts.am baseado na API de web services REST disponível em https://yts.am/api  todas as informações estão no site, o aplicativo somente facilita a navegação e pesquisa.

YtsBrowser é um browser para pesquisas do site https://yts.am baseado na API de web services REST disponível em https://yts.am/api  todas as informações estão no site, o aplicativo somente facilita a navegação e pesquisa.

a pesquisa procura ou filtra por: Título do filme, código IMDB do filme, Nome do ator ou atriz, código IMDB do ator ou atriz, Nome do Diretor, código IMDB do diretor

 */