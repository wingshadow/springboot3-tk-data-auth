package com.hawk.iot.message;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: springboot3-tk-data-auth
 * @description:
 * @author: zhb
 * @create: 2025-06-27 14:03
 */
@Data
public class ReportData implements Serializable {

    public String qn;
    public String tid;
    public String ver;
    public String cp;
    public String dn;
    public List<Integer> cps;
    public List<Integer> cns;
    public List<Integer> mn;
    public PowerSupplyStatus vol;
    public Double ampere;
    public Double humidity;
    public Double temperature;
    public Integer doorStatus;
    public Integer posture;
    public Integer surge;
    public Integer pa;
    public Integer les;
    public Integer ov;
    public Integer ocps;
    public Integer bat;
    public Integer sim;
    /**
     * 水浸
     */
    public Integer water;
    public Double apower;
    public Double akw;
    public List<Integer> relay;

    public List<Double> chv;
    public List<Double> cha;
    public List<Double> chp;
    public List<Double> chk ;
    public Integer miu;
}
