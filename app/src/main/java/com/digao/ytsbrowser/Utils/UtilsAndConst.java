package com.digao.ytsbrowser.Utils;

import android.content.Context;
import android.util.Log;

import com.digao.ytsbrowser.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UtilsAndConst {
    private String URL = "https://yts.am/api/v2/list_movies.json?sort_by=seeds&order_by=desc&limit=";
    private int LIMIT = 10;

    public String getURL() {
        return URL + String.valueOf(LIMIT);
    }

    public String getURL(int page) {
        return URL + String.valueOf(LIMIT) + "&page=" + String.valueOf(page);
    }

    public int getLIMIT() {
        return LIMIT;
    }

    public void setLIMIT(int LIMIT) {
        this.LIMIT = LIMIT;
    }

    public String readTrackers(Context ctx) {

        StringBuilder contents = new StringBuilder();
        String sep = System.getProperty("line.separator");

        try {
            InputStream is = ctx.getResources().openRawResource(R.raw.trackers);

            BufferedReader input = new BufferedReader(new InputStreamReader(is), 1024 * 8);
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    contents.append("&tr=" + line + sep);
                    //contents.append(sep);
                }
            } finally {
                input.close();
            }
        } catch (FileNotFoundException ex) {
            Log.e("error", "Couldn't find the file " + ex);
            // return null;
        } catch (IOException ex) {
            Log.e("error", "Error reading file " + ex);
            //   return null;
        }
        return contents.toString();
    }

}
