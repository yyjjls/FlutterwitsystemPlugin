//
//  Aes.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/5/5.
//

import Foundation
enum CryptError: Error {
    case noIV
    case cryptFailed
    case notConvertTypeToData
}

extension Data {
///***********加密&解密************///
//===>>>>>>>AES128
public func dataCryptAES128(_ options: CCOptions?, _ operation: CCOperation, _ keyData: Data, _ iv: Data?)  throws -> Data {
    return try self.dataCrypt(options ?? CCOptions(kCCOptionECBMode),
                              operation,
                              keyData,
                              iv,
                              CCAlgorithm(kCCAlgorithmAES128))
}
//===>>>>>>>基本方法
func dataCrypt(_ options: CCOptions, _ operation: CCOperation, _ keyData: Data, _ iv: Data?, _ algorithm: UInt32) throws -> Data {
    
    if iv == nil && (options & CCOptions(kCCOptionECBMode)) == 0 {
        print("Error in crypto operation: dismiss iv!")
        throw(CryptError.noIV)
    }
    //key
    let keyBytes = keyData.bytes()
    let keyLength = size_t(kCCKeySizeAES128)
    //data(input)
    let dataBytes = self.bytes()
    let dataLength = size_t(self.count)
    //data(output)
    var buffer = Data(count: dataLength + Int(kCCBlockSizeAES128))
    let bufferBytes = buffer.mutableBytes()
    let bufferLength = size_t(buffer.count)
    //iv
    let ivBuffer: UnsafePointer<UInt8>? = iv == nil ? nil : iv!.bytes()

    var bytesDecrypted: size_t = 0
    
    let cryptState = CCCrypt(operation,
                             algorithm,
                             options,
                             keyBytes,
                             keyLength,
                             ivBuffer,
                             dataBytes,
                             dataLength,
                             bufferBytes,
                             bufferLength,
                             &bytesDecrypted)
    
    guard Int32(cryptState) == Int32(kCCSuccess) else {
        print("Error in crypto operation: \(cryptState)")
        throw(CryptError.cryptFailed)
    }

    buffer.count = bytesDecrypted
    return buffer
}

//===>>>>>>>Help Funcations<<<<<<<===//
func bytes() -> UnsafePointer<UInt8> {
    return self.withUnsafeBytes { (bytes: UnsafePointer<UInt8>) -> UnsafePointer<UInt8> in
        return bytes
    }
}

mutating func mutableBytes() -> UnsafeMutablePointer<UInt8> {
    return self.withUnsafeMutableBytes { (bytes: UnsafeMutablePointer<UInt8>) -> UnsafeMutablePointer<UInt8> in
        return bytes
    }
}
}
