package kr.nuax.aws.config;

import kr.nuax.aws.AwsIamDbAccessForDbcpApplication;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;

@ActiveProfiles("test")
@SpringJUnitConfig
@SpringBootTest(classes = AwsIamDbAccessForDbcpApplication.class)
public class DatabaseConfigurationTests {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "rdsBasicDataSource")
    private DataSource rdsBasicDataSource;

    @Resource(name = "rdsBasic2DataSource")
    private DataSource rdsBasic2DataSource;

    @Resource(name = "redshiftBasicDataSource")
    private DataSource redshiftBasicDataSource;

    @Resource(name = "redshiftBasic2DataSource")
    private DataSource redshiftBasic2DataSource;

    @Test
    public void getConnectionTest() throws SQLException {
        boolean rdsBasicValid = rdsBasicDataSource.getConnection().isValid(5);
        boolean rdsBasic2Valid = rdsBasic2DataSource.getConnection().isValid(5);
        boolean redshiftBasicValid = redshiftBasicDataSource.getConnection().isValid(5);
        boolean redshift2BasicValid = redshiftBasic2DataSource.getConnection().isValid(5);

        logger.debug("rdsBasicDataSource connection : {}", rdsBasicValid);
        logger.debug("rdsBasic2DataSource connection : {}", rdsBasic2Valid);
        logger.debug("redshiftBasicDataSource connection : {}", redshiftBasicValid);
        logger.debug("redshiftBasic2DataSource connection : {}", redshift2BasicValid);

        Assert.isTrue(rdsBasicValid, "rdsBasicDataSource");
        Assert.isTrue(rdsBasic2Valid, "rdsBasic2DataSource");
        Assert.isTrue(redshiftBasicValid, "redshiftBasicDataSource");
        Assert.isTrue(redshift2BasicValid, "redshiftBasic2DataSource");
    }
}
