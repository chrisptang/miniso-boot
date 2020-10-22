package com.leqee.boot.autoconfiguration.cat;

import com.leqee.boot.client.result.Result;

public class DefaultHealthCheckImpl implements HealthCheck {
    @Override
    public Result<Boolean> check() {
        return Result.success(true);
    }

    @Override
    public String getName() {
        return "DefaultHealthCheckImpl";
    }
}
