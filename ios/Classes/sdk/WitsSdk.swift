//
//  WitsSdk.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/4/30.
//

import Foundation

protocol WitsSdk {


    func getBleLockDevice() -> String;

    func getInduceUnlock() -> Induce;

    func getBleUnlock() -> BleUnlock;


}
