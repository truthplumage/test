package com.example.shop.settlement.batch;

import com.example.shop.seller.domain.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.UUID;

@Component
public class SellerSettlementScheduler {

    private static final Logger log = LoggerFactory.getLogger(SellerSettlementScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job sellerSettlementJob;
    private final SellerRepository sellerRepository;

    private final ThreadPoolTaskExecutor settlementTaskExecutor;
    private final boolean settlementAsyncEnabled;

    public SellerSettlementScheduler(JobLauncher jobLauncher,
                                     Job sellerSettlementJob,
                                     SellerRepository sellerRepository,
                                     ThreadPoolTaskExecutor settlementTaskExecutor,
                                     @Value("${settlement.async.enabled:false}") boolean settlementAsyncEnabled) {
        this.jobLauncher = jobLauncher;
        this.sellerSettlementJob = sellerSettlementJob;
        this.sellerRepository = sellerRepository;
        this.settlementTaskExecutor = settlementTaskExecutor;
        this.settlementAsyncEnabled = settlementAsyncEnabled;
    }

    /**
     * 매일 00시에 모든 판매자별 배치를 순차 실행한다.
     */
    @Scheduled(cron = "${spring.task.scheduling.cron.settlement}")
    public void runMidnightSettlements() {
        Pageable pageable = Pageable.ofSize(100);
        Page<UUID> page;
        do {
            page = sellerRepository.findAll(pageable).map(seller -> seller.getId());
            List<UUID> sellerIds = page.getContent();
            if (sellerIds.isEmpty()) {
                break;
            }
            log.info("Settlement batch chunk for {} sellers (page {}/{})",
                    sellerIds.size(), page.getNumber() + 1, page.getTotalPages());
            sellerIds.forEach(this::runJobForSeller);
            pageable = page.hasNext() ? page.nextPageable() : Pageable.unpaged();
        } while (page.hasNext());
    }

    private void runJobForSeller(UUID sellerId) {
        try {
            Runnable executeJob = () -> {
                try {
                    JobParameters params = new JobParametersBuilder()
                            .addLong("timestamp", System.currentTimeMillis())
                            .addString("sellerId", sellerId.toString())
                            .toJobParameters();
                    jobLauncher.run(sellerSettlementJob, params);
                    log.info("Settlement job triggered for seller {}", sellerId);
                } catch (Exception ex) {
                    log.error("Failed to run settlement job for seller {}", sellerId, ex);
                }
            };

            if (settlementAsyncEnabled) {
                settlementTaskExecutor.execute(executeJob);
            } else {
                executeJob.run();
            }
        } catch (Exception ex) {
            log.error("Failed to run settlement job for seller {}", sellerId, ex);
        }
    }
}
