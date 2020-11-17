package com.leqee.boot.autoconfiguration.common;

public enum SupportedEnv {
    Local("local"), Dev("dev"), Test("fat"), Prod("prod");


    SupportedEnv(String name) {
        this.name = name.toLowerCase();
    }

    private final String name;

    public String getName() {
        return name;
    }

    public static SupportedEnv getByName(String name) {
        if (null == name || name.trim().length() <= 0) {
            throw new IllegalArgumentException("Name is invalid:" + name);
        }
        switch (name.toLowerCase()) {
            case "local":
                return Local;
            case "dev":
                return Dev;
            case "test":
                return Test;
            case "fat":
                return Test;
            case "uat":
                return Test;
            case "prod":
                return Prod;
            default:
                return Dev;
        }
    }
}
