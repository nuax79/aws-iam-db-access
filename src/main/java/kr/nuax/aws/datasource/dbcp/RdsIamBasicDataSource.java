package kr.nuax.aws.datasource.dbcp;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.SQLNestedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class RdsIamBasicDataSource extends BasicDataSource implements InitializingBean, ApplicationContextAware {

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
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {

        // Load the JDBC driver class
        Class driverFromCCL = null;
        if (driverClassName != null) {
            try {
                try {
                    if (driverClassLoader == null) {
                        Class.forName(driverClassName);
                    }
                    else {
                        Class.forName(driverClassName, true, driverClassLoader);
                    }
                }
                catch (ClassNotFoundException cnfe) {
                    driverFromCCL = Thread.currentThread(
                    ).getContextClassLoader().loadClass(
                            driverClassName);
                }
            }
            catch (Throwable t) {
                String message = "Cannot load JDBC driver class '" +
                        driverClassName + "'";
                logWriter.println(message);
                t.printStackTrace(logWriter);
                throw new SQLNestedException(message, t);
            }
        }

        // Create a JDBC driver instance
        Driver driver = null;
        try {
            if (driverFromCCL == null) {
                driver = DriverManager.getDriver(url);
            }
            else {
                // Usage of DriverManager is not possible, as it does not
                // respect the ContextClassLoader
                driver = (Driver) driverFromCCL.newInstance();
                if (!driver.acceptsURL(url)) {
                    throw new SQLException("No suitable driver", "08001");
                }
            }
        }
        catch (Throwable t) {
            String message = "Cannot create JDBC driver of class '" +
                    (driverClassName != null ? driverClassName : "") +
                    "' for connect URL '" + url + "'";
            logWriter.println(message);
            t.printStackTrace(logWriter);
            throw new SQLNestedException(message, t);
        }

        // Can't test without a validationQuery
        if (validationQuery == null) {
            setTestOnBorrow(false);
            setTestOnReturn(false);
            setTestWhileIdle(false);
        }

        // Set up the driver connection factory we will use
        String user = username;
        if (user != null) {
            connectionProperties.put("user", user);
        }
        else {
            log("DBCP DataSource configured without a 'username'");
        }

        String pwd = password;
        if (pwd != null) {
            connectionProperties.put("password", pwd);
        } else {
            log("DBCP DataSource configured without a 'password'");
        }

        RdsIamBasicDriverConnectionFactory driverConnectionFactory = new RdsIamBasicDriverConnectionFactory(driver, url, connectionProperties);
        driverConnectionFactory.setApplicationContext(applicationContext);
        driverConnectionFactory.setRegion(region);
        driverConnectionFactory.setEndpoint(endpoint);
        driverConnectionFactory.setPort(port);
        return driverConnectionFactory;
    }
}
