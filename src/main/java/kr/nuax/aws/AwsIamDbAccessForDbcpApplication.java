package kr.nuax.aws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "kr.nuax.aws")
public class AwsIamDbAccessForDbcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsIamDbAccessForDbcpApplication.class, args);
    }

}
