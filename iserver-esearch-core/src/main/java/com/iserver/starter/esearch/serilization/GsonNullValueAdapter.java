package com.iserver.starter.esearch.serilization;


import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Gson 忽略的字段(null 值处理)
 *
 * @author Alay
 * @date 2022-05-05 16:20
 */
public class GsonNullValueAdapter extends TypeAdapter<Object> {

    @Override
    public void write(JsonWriter jsonWriter, Object source) throws IOException {
        // 忽略的字段,直接返回 null
        jsonWriter.nullValue();
    }

    @Override
    public Object read(JsonReader jsonReader) throws IOException {
        // 忽略的字段,直接返回 null
        return null;
    }
}
