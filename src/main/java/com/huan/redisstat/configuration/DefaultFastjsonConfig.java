
package com.huan.redisstat.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lihuan fastjson配置类
 */

@Configuration("defaultFastjsonConfig")
@ConditionalOnClass(com.alibaba.fastjson.JSON.class)
@ConditionalOnMissingBean(FastJsonHttpMessageConverter.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(//2
        name = {"spring.http.converters.preferred-json-mapper"},
        havingValue = "fastjson",
        matchIfMissing = true
)
public class DefaultFastjsonConfig {

    @Value("${config.json.format:false}")
    Boolean jsonFormat;



    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter jsonFilterHttpMessageConverter = new FastJsonHttpMessageConverter();
        jsonFilterHttpMessageConverter.setFastJsonConfig(fastjsonConfig());
        jsonFilterHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaType());
        return jsonFilterHttpMessageConverter;
    }


    /**
     * fastjson的配置
     */

    public FastJsonConfig fastjsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        if (jsonFormat) {
            fastJsonConfig.setSerializerFeatures(
                    SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
        } else {
            fastJsonConfig.setSerializerFeatures(
                    SerializerFeature.WriteMapNullValue);
        }
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        ValueFilter valueFilter = new ValueFilter() {
            @Override
            public Object process(Object o, String s, Object o1) {
                if (null == o1) {
                    o1 = "";
                }
                return o1;
            }
        };
        fastJsonConfig.setCharset(Charset.forName("utf-8"));
        fastJsonConfig.setSerializeFilters(valueFilter);
        return fastJsonConfig;
    }


    /**
     * 支持的mediaType类型
     */

    public List<MediaType> getSupportedMediaType() {
        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        return mediaTypes;
    }

}

