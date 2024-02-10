package com.lsoria.qrsec.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongodbConfig implements InitializingBean {

    @Lazy
    @Autowired
    private MappingMongoConverter mappingMongoConverter;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }

}
