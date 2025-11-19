package com.example.shop.settlement.presentation;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.v1}/settlements")
public class SettlementJobController {

    private final JobLauncher jobLauncher;
    private final Job sellerSettlementJob;

    public SettlementJobController(JobLauncher jobLauncher, Job sellerSettlementJob) {
        this.jobLauncher = jobLauncher;
        this.sellerSettlementJob = sellerSettlementJob;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(sellerSettlementJob, params);
        return ResponseEntity.ok("Settlement job started");
    }
}
