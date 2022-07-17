package kr.nuax.aws.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider(){
        return new DefaultAWSCredentialsProviderChain();
    }
}
