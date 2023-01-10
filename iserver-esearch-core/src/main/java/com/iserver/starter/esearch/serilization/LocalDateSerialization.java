package com.iserver.starter.esearch.serilization;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate GSON 系列化
 *
 * @author Alay
 * @date 2022-06-20 13:02
 */
public class LocalDateSerialization implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    final DateTimeFormatter format = DatePattern.NORM_DATE_FORMATTER;

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext context) {
        return context.serialize(LocalDateTimeUtil.format(localDate, format));
    }

    @Override
    public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTimeUtil.parseDate(json.getAsString(), format);
    }

}
