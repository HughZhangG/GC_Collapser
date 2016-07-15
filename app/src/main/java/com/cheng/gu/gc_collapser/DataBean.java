package com.cheng.gu.gc_collapser;

import java.util.List;

/**
 * Created by Guangcheng.Zhang on 2016/7/13.
 * email:guangcheng.zhang@archermind.com
 */
public class DataBean {
    private String name;
    private List<String> sons;


    public DataBean(String name, List<String> sons) {
        this.name = name;
        this.sons = sons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSons() {
        return sons;
    }

    public void setSons(List<String> sons) {
        this.sons = sons;
    }
}
