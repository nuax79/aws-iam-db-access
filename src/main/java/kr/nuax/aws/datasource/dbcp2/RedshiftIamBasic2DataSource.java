package kr.nuax.aws.datasource.dbcp2;

import kr.nuax.aws.datasource.BasicDataSourceHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.sql.SQLException;
import java.util.Properties;

public class RedshiftIamBasic2DataSource extends BasicDataSource implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String region;
    private String clusterId;

    public void setRegion(String region) {
        this.region = region;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(this.region)) {
            throw new BeanInitializationException("region property required");
        }
        if (StringUtils.isEmpty(this.clusterId)) {
            throw new BeanInitializationException("clusterId property required");
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        Properties properties = new Properties();

        final String url = getUrl();
        final String user = getUsername();
        if (user != null) {
            properties.put("user", user);
        } else {
            log("DBCP DataSource configured without a 'username'");
        }

        BasicDataSourceHelper dataSourceHelper = BasicDataSourceHelper.getInstance();
        RedshiftIamBasic2DriverConnectionFactory connectionFactory = new RedshiftIamBasic2DriverConnectionFactory(
                dataSourceHelper.createDriver(this), url, properties);
        connectionFactory.setApplicationContext(this.applicationContext);
        connectionFactory.setRegion(region);
        connectionFactory.setClusterId(clusterId);

        return connectionFactory;
    }
}
