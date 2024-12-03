package com.hawk.config;

import com.hawk.framework.genid.IdentifierGenerator;
import com.hawk.framework.interceptor.CreateAndUpdateMetaObjectInterceptor;
import com.hawk.framework.interceptor.DataPermissionInterceptor;
import com.hawk.framework.interceptor.IdentifierInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Title: MybatisConfig
 * @ProjectName spring-safety-training
 * @Author May
 * @Date 2020/3/27 6:38
 */
@Configuration
public class MybatisConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("com.hawk.**.entity");

        sessionFactory.setMapperLocations(resolveMapperLocations());
        Objects.requireNonNull(sessionFactory.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);
        sessionFactory.getObject().getConfiguration().addInterceptor(new DataPermissionInterceptor());
        sessionFactory.getObject().getConfiguration().addInterceptor(new IdentifierInterceptor(new IdentifierGenerator()));
        sessionFactory.getObject().getConfiguration().addInterceptor(new CreateAndUpdateMetaObjectInterceptor());
        return sessionFactory.getObject();
    }

    private org.springframework.core.io.Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/*.xml");
        mapperLocations.add("classpath*:mapper/**/*.xml");
        List<org.springframework.core.io.Resource> resources = new ArrayList<>();
        for (String mapperLocation : mapperLocations) {
            try {
                org.springframework.core.io.Resource[] mappers = resourceResolver.getResources(mapperLocation);
                resources.addAll(Arrays.asList(mappers));
            } catch (IOException e) {
                // ignore
            }
        }
        return resources.toArray(new org.springframework.core.io.Resource[resources.size()]);
    }

    @Bean
    public IdentifierGenerator snowflakeIdGen(){
        return new IdentifierGenerator();
    }
}
