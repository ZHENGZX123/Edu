package cn.kiway.yjhz.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

/**
 * Created by arvin on 2017/3/9 0009.
 */

public class KiwayJson {

    private JSONObject jsonObject;
    private JSONObject json;

    public KiwayJson() {

    }

    public JSONObject getJSONObject(String str){
        JSONObject jsonO = null;
        if (str != null)
            try {
                jsonO = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return jsonO;
    }

    public JSONObject getJSONObject(JSONObject json,String key) {
        JSONObject jsonO = null;
        try {
            jsonO =  json.getJSONObject(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonO;
    }

}
