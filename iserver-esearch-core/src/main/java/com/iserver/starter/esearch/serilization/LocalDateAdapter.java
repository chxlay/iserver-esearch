package com.iserver.starter.esearch.serilization;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 自定义Gson系列化格式
 *
 * @author Alay
 * @date 2020-11-20 10:34
 */
public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    final DateTimeFormatter dateFormatter = DatePattern.NORM_DATE_FORMATTER;

    @Override
    public void write(JsonWriter jsonWriter, LocalDate date) throws IOException {
        jsonWriter.value(date.format(dateFormatter));
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            LocalDate localDate = LocalDateTimeUtil.parseDate(in.nextString(), dateFormatter);
            return localDate;
        }
    }
}