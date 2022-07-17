package kr.nuax.aws.datasource;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BasicDataSourceHelper {

    private static final Logger logger = LoggerFactory.getLogger(BasicDataSourceHelper.class);

    private static BasicDataSourceHelper basicDataSourceHelper = null;

    private BasicDataSourceHelper() {
    }

    public static BasicDataSourceHelper getInstance() {
        if (basicDataSourceHelper == null) {
            basicDataSourceHelper = new BasicDataSourceHelper();
        }

        return basicDataSourceHelper;
    }

    public Driver createDriver(final BasicDataSource basicDataSource) throws SQLException {
        // Load the JDBC driver class
        Driver driverToUse = basicDataSource.getDriver();
        String driverClassName = basicDataSource.getDriverClassName();
        ClassLoader driverClassLoader = basicDataSource.getDriverClassLoader();
        String url = basicDataSource.getUrl();

        if (driverToUse == null) {
            Class<?> driverFromCCL = null;
            if (driverClassName != null) {
                try {
                    try {
                        if (driverClassLoader == null) {
                            driverFromCCL = Class.forName(driverClassName);
                        }
                        else {
                            driverFromCCL = Class.forName(driverClassName, true, driverClassLoader);
                        }
                    }
                    catch (final ClassNotFoundException cnfe) {
                        driverFromCCL = Thread.currentThread().getContextClassLoader().loadClass(driverClassName);
                    }
                }
                catch (final Exception t) {
                    final String message = "Cannot load JDBC driver class '" + driverClassName + "'";
                    logger.error(message, t);
                    throw new SQLException(message, t);
                }
            }

            try {
                if (driverFromCCL == null) {
                    driverToUse = DriverManager.getDriver(url);
                }
                else {
                    // Usage of DriverManager is not possible, as it does not
                    // respect the ContextClassLoader
                    // N.B. This cast may cause ClassCastException which is
                    // handled below
                    driverToUse = (Driver) driverFromCCL.getConstructor().newInstance();
                    if (!driverToUse.acceptsURL(url)) {
                        throw new SQLException("No suitable driver", "08001");
                    }
                }
            }
            catch (final Exception t) {
                final String message = "Cannot create JDBC driver of class '"
                        + (driverClassName != null ? driverClassName : "") + "' for connect URL '" + url + "'";
                logger.error(message, t);
                throw new SQLException(message, t);
            }
        }
        return driverToUse;
    }

    public AWSCredentialsProvider getCredentialsProvider(ApplicationContext applicationContext) {
        AWSCredentialsProvider awsCredentialsProvider;
        try {
            awsCredentialsProvider = applicationContext.getBean(AWSCredentialsProvider.class);
        }
        catch (NoSuchBeanDefinitionException e) {
            awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();
        }
        return awsCredentialsProvider;
    }
}
