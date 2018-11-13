package com.iscas.yf.IntelliPipeline.common.controller;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class BaseController {
    public static final Map<String, String> SUCCESS =
            ImmutableMap.<String, String> of("result", "success");

    public static class DResponseBuilder {
        private Map<String, Object> map;

        public static DResponseBuilder instance(){
            return new DResponseBuilder();
        }

        private DResponseBuilder(){

        }

        public DResponseBuilder add(String key, Object value){
            map.put(key, value);
            return this;
        }

        public Map<String, Object> build(){
            return map;
        }
    }
}
