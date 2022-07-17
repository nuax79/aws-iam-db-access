package kr.nuax.aws.config;

import kr.nuax.aws.datasource.dbcp.RdsIamBasicDataSource;
import kr.nuax.aws.datasource.dbcp.RedshiftIamBasicDataSource;
import kr.nuax.aws.datasource.dbcp2.RdsIamBasic2DataSource;
import kr.nuax.aws.datasource.dbcp2.RedshiftIamBasic2DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean("rdsBasicDataSource")
    @ConfigurationProperties("datasource.rds")
    public DataSource rdsBasicDataSource(){
        RdsIamBasicDataSource dataSource = new RdsIamBasicDataSource();
        return dataSource;
    }

    @Bean("redshiftBasicDataSource")
    @ConfigurationProperties("datasource.redshift")
    public DataSource redshiftBasicDataSource(){
        RedshiftIamBasicDataSource dataSource = new RedshiftIamBasicDataSource();
        return dataSource;
    }

    @Bean("rdsBasic2DataSource")
    @ConfigurationProperties("datasource.rds")
    public DataSource rdsBasic2DataSource(){
        RdsIamBasic2DataSource dataSource = new RdsIamBasic2DataSource();
        return dataSource;
    }

    @Bean("redshiftBasic2DataSource")
    @ConfigurationProperties("datasource.redshift")
    public DataSource redshiftBasic2DataSource(){
        RedshiftIamBasic2DataSource dataSource = new RedshiftIamBasic2DataSource();
        return dataSource;
    }
}
