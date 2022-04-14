package com.luizbebe.vip.dao;

import com.luizbebe.vip.data.vip.Vip;
import com.luizbebe.vip.data.vip.VipSettings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class VipDAO {

    @Getter
    private static final List<Vip> vips = new ArrayList<>();

    @Getter
    private static final List<VipSettings> settings = new ArrayList<>();

    public static void addVip(Vip vip, VipSettings vipSettings) {
        settings.add(vipSettings);
        vips.add(vip);
    }

    public static Vip getVip(String vipId) {
        return vips.stream().filter(vip -> vip.getId().equalsIgnoreCase(vipId)).findFirst().orElse(null);
    }

}
