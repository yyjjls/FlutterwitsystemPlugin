package com.witsystem.top.flutterwitsystem.sdk;


import com.witsystem.top.flutterwitsystem.induce.InduceUnlock;

/**
 * sdk接口
 */
public interface WitsSdk {



    /**
     * 获得感应开锁对象
     * @return
     */
    InduceUnlock getInduceUnlock();


    /**
     * 获得开锁对象 还是没有实现
     */
}
