package com.example.shop.settlement.batch;

import com.example.shop.settlement.domain.SellerSettlement;
import com.example.shop.settlement.domain.SellerSettlementRepository;
import com.example.shop.settlement.domain.SettlementStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableBatchProcessing
public class SellerSettlementBatchConfig {

    private static final Logger log = LoggerFactory.getLogger(SellerSettlementBatchConfig.class);

    @Bean
    public Job sellerSettlementJob(JobRepository jobRepository,
                                   Step settlementStep) {
        return new JobBuilder("sellerSettlementJob", jobRepository)
                .start(settlementStep)
                .build();
    }

    @Bean
    public Step settlementStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               SellerSettlementRepository sellerSettlementRepository) {
        return new StepBuilder("settlementStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<SellerSettlement> pending = sellerSettlementRepository.findByStatus(SettlementStatus.PENDING);
                    if (pending.isEmpty()) {
                        log.info("No pending settlements to process.");
                        return RepeatStatus.FINISHED;
                    }

                    Map<UUID, BigDecimal> totals = pending.stream()
                            .collect(Collectors.groupingBy(
                                    SellerSettlement::getSellerId,
                                    Collectors.reducing(BigDecimal.ZERO, SellerSettlement::getAmount, BigDecimal::add)
                            ));

                    pending.forEach(SellerSettlement::markCompleted);
                    sellerSettlementRepository.saveAll(pending);

                    totals.forEach((sellerId, total) ->
                            log.info("Settled seller {} amount {}", sellerId, total));

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
