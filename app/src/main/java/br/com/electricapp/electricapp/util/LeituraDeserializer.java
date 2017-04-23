package br.com.electricapp.electricapp.util;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import br.com.electricapp.electricapp.model.Leitura;

/**
 * Created by Nathalia on 19/04/2017.
 */
public class LeituraDeserializer implements JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement leitura = json.getAsJsonObject();

        if (json.getAsJsonObject().get("leitura") != null) {
            leitura = json.getAsJsonObject().get("leitura");
        }

        return new Gson().fromJson(leitura, Leitura.class);
    }
}
