package com.luizbebe.vip.data.vip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Vip implements Serializable {

    private VipSettings settings;
    private String id;
    private String group;
    private String name;
    private String date;
    private List<String> commands;
    private long time;

    public boolean isEternal() {
        return time == -1;
    }

}
