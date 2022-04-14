package com.luizbebe.vip.data.vip;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class VipSettings implements Serializable {

    private String name;
    private String skullURL;
    private String titleLine1;
    private String titleLine2;
    private List<String> lore;
    private EnumWrappers.Particle particleType;
    private Material material;
    private int data;
    private int periodBasis;
    private int quantity;
    private double degrees;
    private double x;
    private double y;
    private double z;
    private boolean enabledAnimation;
    private boolean customSkull;
    private boolean glow;

}
