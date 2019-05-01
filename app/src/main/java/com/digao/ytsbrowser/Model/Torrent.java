package com.digao.ytsbrowser.Model;

import android.content.Context;

import com.digao.ytsbrowser.Utils.UtilsAndConst;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Torrent {
    private String hash;
    private String quality;
    private String Size;
    private int seeds;
    private int peers;
    private String Url;


    private String type;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public int getSeeds() {
        return seeds;
    }

    public void setSeeds(int seeds) {
        this.seeds = seeds;
    }

    public int getPeers() {
        return peers;
    }

    public void setPeers(int peers) {
        this.peers = peers;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMagnetLink(String title, Context ctx) {
        String result = "";
        //&tr=
        //magnet:?xt=urn:btih:" . $torrent["hash"] . "&dn=" . urlencode($movie["title"] . $trackers)
        UtilsAndConst util = new UtilsAndConst();
        try {
            result = "magnet:?xt=urn:btih:" + hash + "&dn=" + URLEncoder.encode(title, "UTF-8") + util.readTrackers(ctx);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;

    }
    /*
"torrents": [
    {
        "url": "https://yts.am/torrent/download/C39E1E22B323688A715439D1505D25ED32F84E71",
            "hash": "C39E1E22B323688A715439D1505D25ED32F84E71",
            "quality": "720p",
            "seeds": 1095,
            "peers": 2598,
            "size": "940.05 MB",
            "size_bytes": 985713869,
            "date_uploaded": "2018-05-23 05:17:11",
            "date_uploaded_unix": 1527045431
    },
    */

}
