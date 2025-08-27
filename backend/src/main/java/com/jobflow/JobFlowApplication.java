package com.jobflow;

import com.jobflow.sources.adapters.AdzunaProperties;
import com.jobflow.sources.adapters.NaukriProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = "com.jobflow")
@EnableConfigurationProperties({ NaukriProperties.class,  AdzunaProperties.class })
public class JobFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobFlowApplication.class, args);
    }
} 