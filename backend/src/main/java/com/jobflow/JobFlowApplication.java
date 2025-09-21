package com.jobflow;

import com.jobflow.sources.adapters.AdzunaProperties;
import com.jobflow.sources.adapters.NaukriProperties;
import com.jobflow.sources.ports.JobFetchPort;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "com.jobflow")
@EnableConfigurationProperties({ NaukriProperties.class,  AdzunaProperties.class })
@EnableScheduling
public class JobFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobFlowApplication.class, args);
    }

    @Bean
    ApplicationRunner printAdaptersAtStartup(List<JobFetchPort> adapters) {
        return args -> {
            String names = adapters.stream()
                    .map(JobFetchPort::sourceKey)
                    .collect(Collectors.joining(", "));
            System.out.println("[Startup] adapters detected: [" + names + "]");
        };
    }
} 