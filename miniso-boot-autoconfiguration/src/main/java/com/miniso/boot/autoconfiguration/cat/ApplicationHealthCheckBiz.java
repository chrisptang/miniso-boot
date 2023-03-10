package com.miniso.boot.autoconfiguration.cat;

import com.dianping.cat.Cat;
import com.miniso.boot.client.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ApplicationHealthCheckBiz implements Runnable {

    private int initialDelay = 10;

    private int period = 30;

    private static final AtomicBoolean IS_STARTED = new AtomicBoolean(false);

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    private Collection<HealthCheck> healthCheckList;

    public Collection<HealthCheck> getHealthCheckList() {
        return healthCheckList;
    }

    public void setHealthCheckList(Collection<HealthCheck> healthCheckList) {
        this.healthCheckList = healthCheckList;
    }

    public void start() {
        if (!IS_STARTED.get()) {
            Cat.logEvent("HealthCheck", "app-started");
            EXECUTOR_SERVICE.scheduleAtFixedRate(this,
                    this.initialDelay, this.period, TimeUnit.SECONDS);
            IS_STARTED.compareAndSet(false, true);
        }
    }

    public void preDestroy() {
        Cat.logEvent("HealthCheck", "app-stopping");
        EXECUTOR_SERVICE.shutdownNow();
    }

    @Override
    public void run() {
        if (log.isInfoEnabled()) {
            log.info("Preparing HealthCheck...");
        }
        if (CollectionUtils.isEmpty(healthCheckList)) {
            Cat.logEvent("HealthCheck", "No-HealthCheck-implementations", "failed", "");
            return;
        }
        for (HealthCheck healthChecker : healthCheckList) {
            try {
                Result<Boolean> checkStatus = healthChecker.check();
                if (Result.isFailed(checkStatus)) {
                    Cat.logEvent("HealthCheck", healthChecker.getName(), "failed", "msg=" + checkStatus.getMessage());
                    return;
                }
            } catch (Exception e) {
                Cat.logError("Exception while doing health check with:" + healthChecker.getName(), e);
                return;
            }
        }
        Cat.logEvent("HealthCheck", "success");
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        if (initialDelay > 0 && initialDelay <= 1000) {
            this.initialDelay = initialDelay;
        }
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        if (period > 0 && period <= 1000) {
            this.period = period;
        }
    }
}
