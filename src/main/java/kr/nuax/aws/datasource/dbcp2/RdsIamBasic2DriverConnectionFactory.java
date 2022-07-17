package kr.nuax.aws.datasource.dbcp2;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import kr.nuax.aws.datasource.BasicDataSourceHelper;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class RdsIamBasic2DriverConnectionFactory implements ConnectionFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Driver driver;
    private String connectionString;
    private final Properties properties;
    private String region;
    private String endpoint;
    private int port;

    private ApplicationContext applicationContext;

    /**
     * Constructs a connection factory for a given Driver.
     *  @param driver
     *            The Driver.
     * @param connectionString
     *            The connection string.
     * @param properties
     */
    public RdsIamBasic2DriverConnectionFactory(Driver driver, String connectionString, Properties properties) {
        this.driver = driver;
        this.connectionString = connectionString;
        this.properties = properties;
    }

    @Override
    public Connection createConnection() throws SQLException {
        generateAuthToken();
        return this.driver.connect(this.connectionString, this.properties);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this.driver + ";" +
                this.connectionString + ";" + this.properties + "]";
    }

    private void generateAuthToken() {
        BasicDataSourceHelper dataSourceHelper = BasicDataSourceHelper.getInstance();
        AWSCredentialsProvider awsCredentialsProvider = dataSourceHelper.getCredentialsProvider(this.applicationContext);

        if (logger.isDebugEnabled()) {
            logger.debug("Generate Password Begin...");
        }
        String username = properties.getProperty("user");
        // RDS Generate Password
        RdsIamAuthTokenGenerator.Builder generator = RdsIamAuthTokenGenerator.builder()
                .credentials(awsCredentialsProvider);
        String password = generator.region(region).build()
                .getAuthToken(GetIamAuthTokenRequest.builder()
                        .hostname(endpoint).port(port).userName(username).build());

        properties.setProperty("password", password);

        if (logger.isDebugEnabled()) {
            logger.debug("RDS Password : {} ", password);
            logger.debug("Generate Password End...");
        }
    }


    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
}
