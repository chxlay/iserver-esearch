package com.iserver.starter.esearch.serilization;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate GSON 系列化
 *
 * @author Alay
 * @date 2022-06-20 13:02
 */
public class LocalDateTimeSerialization implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    final DateTimeFormatter format = DatePattern.NORM_DATETIME_FORMATTER;

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
        return context.serialize(LocalDateTimeUtil.format(localDateTime, format));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDateTimeUtil.parse(json.getAsString(), format);
    }

}
