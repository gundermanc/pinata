package com.pinata.shared;

import flexjson.JSON;
import flexjson.JSONSerializer;
import flexjson.JSONDeserializer;
import flexjson.JSONException;

/**
 * JSON Request/Response parent class.
 * @author Christian Gunderman
 */
public abstract class Json {

    /**
     * Serializes this object into JSON.
     * @return Serialized object as JSON.
     */
    public String serialize() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.exclude("*.class").serialize(this);
    }

    /**
     * Deserializes given JSON String into this object.
     * @param json Json serialized instance of a class.
     */
    public void deserializeFrom(String json) throws ApiException {
        try {
            new JSONDeserializer<Json>().use(null, 
                this.getClass()).deserializeInto(json, this);
        } catch (JSONException ex) {
            throw new ApiException(ApiStatus.JSON_DS_ERROR, ex);
        }
    }
}
