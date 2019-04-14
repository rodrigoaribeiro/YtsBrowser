package com.digao.ytsbrowser.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CfGlobal {
    private static CfGlobal instance;
    private Context ctx;
    private SharedPreferences prefsCFG;
    private SharedPreferences.Editor editor;
    private String URL = "https://yts.am/api/v2/list_movies.json"; //?sort_by=seeds&order_by=desc&limit=";
    private int LIMIT;
    private String QUALITY;
    public String SORTBY;
    public String ORDERBY;

    public String getURL() {
        return URL + "?limit=" + String.valueOf(getLIMIT());
    }

    public String getURL(String query, int page) {
        String vReturn = getURL();
        if (!query.isEmpty()) {
            try {
                vReturn += "&query_term=" + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (!getQuality().isEmpty()) {
            vReturn += "&quality=" + getQuality();
        }
        if (!getSortBy().isEmpty()) {
            vReturn += "&sort_by=" + getSortBy();
        }
        if (!getORDERBY().isEmpty()) {
            vReturn += "&order_by=" + getORDERBY();
        }
        if (page > 0) {
            vReturn += "&page=" + String.valueOf(page);
        }
        return vReturn;  //URL + String.valueOf(LIMIT) + "&page=" + String.valueOf(page);
    }

    public String getORDERBY() {
        this.ORDERBY = "";
        if (prefsCFG.contains("ORDERBY")) {
            this.ORDERBY = prefsCFG.getString("ORDERBY", "");
        }
        return this.ORDERBY;
    }

    public void setORDERBY(String order_by) {
        editor = prefsCFG.edit();
        editor.putString("ORDERBY", order_by);
        editor.commit();
        this.ORDERBY = order_by;
    }

    public String getQuality() {
        this.QUALITY = "";
        if (prefsCFG.contains("QUALITY")) {
            this.QUALITY = prefsCFG.getString("QUALITY", "");
        }
        return this.QUALITY;
    }

    public void setQUALITY(String Quality) {
        editor = prefsCFG.edit();
        editor.putString("QUALITY", Quality);
        editor.commit();
        this.QUALITY = Quality;
    }

    public String getSortBy() {
        /*
        String (title, year, rating, peers, seeds, download_count, like_count, date_added)
        orderby - String (desc, asc)
         */

        this.SORTBY = "";
        if (prefsCFG.contains("SORTBY")) {
            this.SORTBY = prefsCFG.getString("SORTBY", "");
        }
        return this.SORTBY;
    }

    public void setSORTBY(String SortBy) {
        editor = prefsCFG.edit();
        editor.putString("SORTBY", SortBy);
        editor.commit();
        this.SORTBY = SortBy;
    }

    public int getLIMIT() {
        this.LIMIT = 20;
        if (prefsCFG.contains("LIMIT")) {
            this.LIMIT = prefsCFG.getInt("LIMIT", 20);
        }
        return this.LIMIT;

    }

    public void setLIMIT(int LIMIT) {
        editor = prefsCFG.edit();
        editor.putInt("LIMIT", LIMIT);
        editor.commit();
        this.LIMIT = LIMIT;
    }


    private CfGlobal(Context context) {
        ctx = context;
        prefsCFG = ctx.getSharedPreferences("confApp_.prefs", 0);

    }

    public static synchronized CfGlobal getInstance(Context context) {

        if (instance == null) {
            instance = new CfGlobal(context);
        }

        return instance;
    }

}
