package ar.diawigocd.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
    public static Gson GSON = new GsonBuilder()
            .serializeNulls()
            .create();
}
