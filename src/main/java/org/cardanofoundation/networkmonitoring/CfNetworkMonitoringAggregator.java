package org.cardanofoundation.networkmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.cardanofoundation.networkmonitoring")
@EntityScan({
        "org.cardanofoundation.networkmonitoring.entity"
})
@EnableJpaRepositories({
        "org.cardanofoundation.networkmonitoring.repository"
})
public class CfNetworkMonitoringAggregator {

    public static void main(String[] args) {
        SpringApplication.run(CfNetworkMonitoringAggregator.class, args);
    }

}
