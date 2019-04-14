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
            Log.e("error", "Arquivo não encontrado " + ex);
            // return null;
        } catch (IOException ex) {
            Log.e("error", "Erro ao ler arquivo " + ex);
            //   return null;
        }
        return contents.toString();
    }

}
