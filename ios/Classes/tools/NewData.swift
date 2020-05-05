//
//  NewData.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/5/5.
//

import Foundation
import Foundation


extension Data {
 

  public func toHexString() -> String {
    `lazy`.reduce(into: "") {
      var s = String($1, radix: 16)
      if s.count == 1 {
        s = "0" + s
      }
      $0 += s
    }
  }
}
