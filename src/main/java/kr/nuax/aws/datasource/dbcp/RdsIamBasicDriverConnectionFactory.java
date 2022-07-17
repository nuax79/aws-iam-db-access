package kr.nuax.aws.datasource.dbcp;

import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import kr.nuax.aws.datasource.BasicDataSourceHelper;
import org.apache.commons.dbcp.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class RdsIamBasicDriverConnectionFactory implements ConnectionFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Driver driver;
    private ApplicationContext applicationContext;
    private final Properties properties;
    private String connectionString;
    private String region;
    private String endpoint;
    private int port;

    /**
     * Constructs a connection factory for a given Driver.
     *  @param driver
     *            The Driver.
     * @param connectString
     *            The connection string.
     * @param properties
     */
    public RdsIamBasicDriverConnectionFactory(Driver driver, String connectString, Properties properties) {
        this.driver = driver;
        this.connectionString = connectString;
        this.properties = properties;
    }

    @Override
    public Connection createConnection() throws SQLException {
        generateAuthToken();
        return this.driver.connect(this.connectionString, this.properties);
    }

    private void generateAuthToken() {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate Password Begin...");
        }

        String username = properties.getProperty("user");
        // RDS Generate Password
        RdsIamAuthTokenGenerator.Builder generator = RdsIamAuthTokenGenerator.builder()
                .credentials(BasicDataSourceHelper.getInstance().getCredentialsProvider(applicationContext));
        String password = generator.region(this.region).build()
                .getAuthToken(GetIamAuthTokenRequest.builder()
                        .hostname(this.endpoint).port(this.port).userName(username).build());

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
