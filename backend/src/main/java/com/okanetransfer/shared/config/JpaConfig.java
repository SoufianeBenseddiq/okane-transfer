package com.okanetransfer.shared.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.okanetransfer.repository")
@ComponentScan(basePackages = "com.okanetransfer.shared.util")
public class JpaConfig {

    @Bean
    public DataSource dataSource() throws IOException {
        Properties props = new Properties();
        props.load(new ClassPathResource("application.properties").getInputStream());
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(props.getProperty("spring.datasource.url"));
        ds.setUsername(props.getProperty("spring.datasource.username"));
        ds.setPassword(props.getProperty("spring.datasource.password"));
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource, ConfigurableListableBeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.okanetransfer.entity");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaPropertyMap(Map.of(
                "hibernate.resource.beans.container",
                new SpringBeanContainer(beanFactory)
        ));
        Properties jpaProps = new Properties();
        jpaProps.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        jpaProps.setProperty("hibernate.hbm2ddl.auto", "update");
        jpaProps.setProperty("hibernate.show_sql", "true");
        jpaProps.setProperty("hibernate.format_sql", "true");
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}