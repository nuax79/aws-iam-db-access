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

public class RdsIamBasic2DataSource extends BasicDataSource implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String region;
    private String endpoint;
    private int port = 0;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(this.region)) {
            throw new BeanInitializationException("region property required");
        }
        if (StringUtils.isEmpty(this.endpoint)) {
            throw new BeanInitializationException("endpoint property required");
        }
        if (port == 0) {
            throw new BeanInitializationException("port property required");
        }
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        Properties properties = new Properties();
        properties.put("region", this.region);

        final String url = getUrl();
        final String user = getUsername();
        if (user != null) {
            properties.put("user", user);
        } else {
            log("DBCP DataSource configured without a 'username'");
        }

        RdsIamBasic2DriverConnectionFactory connectionFactory = new RdsIamBasic2DriverConnectionFactory(BasicDataSourceHelper.getInstance().createDriver(this), url, properties);
        connectionFactory.setApplicationContext(applicationContext);
        connectionFactory.setRegion(region);
        connectionFactory.setEndpoint(endpoint);
        connectionFactory.setPort(port);

        return connectionFactory;
    }
}
