package com.iserver.starter.esearch.serilization;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义Gson系列化格式
 *
 * @author Alay
 * @date 2020-11-20 09:57
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    final DateTimeFormatter dateTimeFormatter = DatePattern.NORM_DATETIME_FORMATTER;

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime dateTime) throws IOException {
        jsonWriter.value(dateTime.format(dateTimeFormatter));
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            LocalDateTime localDateTime = LocalDateTimeUtil.parse(in.nextString(), dateTimeFormatter);
            return localDateTime;
        }
    }

}