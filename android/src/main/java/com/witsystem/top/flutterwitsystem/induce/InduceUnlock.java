package com.witsystem.top.flutterwitsystem.induce;


public interface InduceUnlock {

    //是否支持感应开锁
    boolean isReaction();

    //感应开锁是否已经运行
    boolean isRunningInduceUnlock();

    //开启感应开锁
    boolean openInduceUnlock();

    //关闭感应开锁
    boolean stopInduceUnlock();


}
