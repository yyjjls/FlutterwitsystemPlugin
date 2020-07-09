//
//  Aes.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/5/5.
//

import Foundation
//aes 加密
extension Data{
    func aesEncrypt( keyData: Data,  operation: Int) -> Data {
        let dataLength = self.count
        let cryptLength  = size_t(dataLength + kCCBlockSizeAES128)
        var cryptData = Data(count:cryptLength)
        let keyLength = size_t(kCCKeySizeAES128)
        let options = CCOptions(kCCOptionECBMode)
        let ivData=Data.init();
        var numBytesEncrypted :size_t = 0
        let cryptStatus = cryptData.withUnsafeMutableBytes {cryptBytes in
            self.withUnsafeBytes {dataBytes in
                ivData.withUnsafeBytes {ivBytes in
                    keyData.withUnsafeBytes {keyBytes in
                        CCCrypt(
                                CCOperation(operation),
                                CCAlgorithm(kCCAlgorithmAES),
                                options,
                                keyBytes,
                                keyLength,
                                ivBytes,
                                dataBytes,
                                dataLength,
                                cryptBytes,
                                cryptLength,
                                &numBytesEncrypted
                        )
                    }
                }
            }
        }

        if UInt32(cryptStatus) == UInt32(kCCSuccess) {
            cryptData.removeSubrange(numBytesEncrypted..<cryptData.count)

        } else {
            print("Error: \(cryptStatus)")
        }

        return cryptData;
    }

}
