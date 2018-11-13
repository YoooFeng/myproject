package com.iscas.yf.IntelliPipeline.common.util;

import java.util.UUID;

public class Uniques {

    public static String getUniqueString(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
