package com.hawk.framework.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hawk.framework.jackson.BigNumberSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.dfp.DfpField;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * jackson 配置
 *
 * @author Lion Li
 */
@Slf4j
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            // 全局配置序列化返回 JSON 处理
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(Double.TYPE, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(Double.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigDecimal.class, BigDecimalSerializer.INSTANCE);
            javaTimeModule.addSerializer(Date.class, DateSerializer.INSTANCE);
            DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(formatDate));
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(formatDate));
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatTime));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatTime));
            builder.modules(javaTimeModule);
            builder.timeZone(TimeZone.getDefault());
            log.info("初始化 jackson 配置");
        };
    }

    @JacksonStdImpl
    private static class DateSerializer extends StdScalarSerializer<Date> {
        public static final DateSerializer INSTANCE = new DateSerializer();

        public DateSerializer() {
            super(Date.class);
        }

        @Override
        public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN));
            /*if (dateStr.endsWith(" 00:00:00")) {
                gen.writeString(dateStr.substring(dateStr.indexOf(" ")));
            } else {
                gen.writeString(dateStr);
            }*/
        }
    }

    @JacksonStdImpl
    private static class BigDecimalSerializer extends StdScalarSerializer<BigDecimal> {
        /**
         * 根据 JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER 得来
         */
        private static final long MAX_SAFE_INTEGER = 9007199254740991L;
        private static final long MIN_SAFE_INTEGER = -9007199254740991L;
        /**
         * 提供实例
         */
        public static final BigDecimalSerializer INSTANCE = new BigDecimalSerializer();

        public BigDecimalSerializer() {
            super(BigDecimal.class);
        }

        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            BigDecimal result = value.setScale(2, DfpField.RoundingMode.ROUND_UP.ordinal());
            // 超出范围 序列化位字符串
            if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
                gen.writeNumber(result);
            } else {
                gen.writeString(result.toString());
            }
        }
    }

}
