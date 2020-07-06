//
//  HttpsClient.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//网络请求

import Foundation


class HttpsClient {


    /**
      POST请求
      */
    public static func POSTAction(urlStr: String, param: [String: String]) -> NSDictionary? {
        /* //请求URL
         let url: NSURL! = NSURL(string: urlStr.contains("https") ? urlStr : "https://witsystem.top" + urlStr);
         let request: NSMutableURLRequest = NSMutableURLRequest(url: url as URL)
         let list = NSMutableArray()
         // var paramDic = [String: String]()
         if param.count > 0 {
             request.httpMethod = "POST"
             //拆分字典,subDic是其中一项，将key与value变成字符串
             for subDic in param {
                 let tmpStr = "\(subDic.0)=\(subDic.1)"
                 list.add(tmpStr)
             }
             //用&拼接变成字符串的字典各项
             let paramStr = list.componentsJoined(by: "&")
             //UTF8转码，防止汉字符号引起的非法网址
             let paraData = paramStr.data(using: String.Encoding.utf8)
             //设置请求体
             request.httpBody = paraData;
         }
         //默认session配置
         let config = URLSessionConfiguration.default
         let session = URLSession(configuration: config)
         var jsonData: NSDictionary? = nil;
         let semaphore = DispatchSemaphore(value: 0);
         //发起请求
         let dataTask = session.dataTask(with: request as URLRequest) { (data, response, error) in
             if (error == nil) {
                 let str: String! = String(data: data!, encoding: String.Encoding.utf8)
                 print("网络请求值\(str)");
                 //转Json
                 jsonData = try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as! NSDictionary
             }
             semaphore.signal();
         } as URLSessionTask;
         //请求开始
         dataTask.resume();
         semaphore.wait()*/
        let jsonStr: String = POSTAction(urlStr: urlStr, param: param) ?? "";
        if (jsonStr == "") {
            return nil;
        }
        return TypeTo.getDictionaryFromJSONString(jsonString: jsonStr);
    }




    /**
    POST请求 返回字符串
    */
    public static func POSTAction(urlStr: String, param: [String: String]) -> String? {
        //请求URL
        let url: NSURL! = NSURL(string: urlStr.contains("https") ? urlStr : "https://witsystem.top" + urlStr);
        let request: NSMutableURLRequest = NSMutableURLRequest(url: url as URL)
        let list = NSMutableArray()
        // var paramDic = [String: String]()
        if param.count > 0 {
            request.httpMethod = "POST"
            //拆分字典,subDic是其中一项，将key与value变成字符串
            for subDic in param {
                let tmpStr = "\(subDic.0)=\(subDic.1)"
                list.add(tmpStr)
            }
            //用&拼接变成字符串的字典各项
            let paramStr = list.componentsJoined(by: "&")
            //UTF8转码，防止汉字符号引起的非法网址
            let paraData = paramStr.data(using: String.Encoding.utf8)
            //设置请求体
            request.httpBody = paraData;
        }
        //默认session配置
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        var jsonData: String? = nil;
        let semaphore = DispatchSemaphore(value: 0);
        //发起请求
        let dataTask = session.dataTask(with: request as URLRequest) { (data, response, error) in
            //print("网络请求值\(response)");
            if (error == nil) {
                jsonData = String(data: data!, encoding: String.Encoding.utf8)
            }
            semaphore.signal();
        } as URLSessionTask;
        //请求开始
        dataTask.resume();
        semaphore.wait()
        return jsonData;
    }

    /**
       GET请求
       */
    func GETACtion() {
        //请求URL
        let url: NSURL! = NSURL(string: "http://iappfree.candou.com:8080/free/applications/limited")
        let request: NSMutableURLRequest = NSMutableURLRequest(url: url as URL)
        let list = NSMutableArray()
        var paramDic = [String: String]()

        if paramDic.count > 0 {
            //设置为GET请求
            request.httpMethod = "GET"
            //拆分字典,subDic是其中一项，将key与value变成字符串
            for subDic in paramDic {
                let tmpStr = "\(subDic.0)=\(subDic.1)"
                list.add(tmpStr)
            }
            //用&拼接变成字符串的字典各项
            //  let paramStr = list.componentsJoined(by: "&")
            //UTF8转码，防止汉字符号引起的非法网址
            //  let paraData = paramStr.data(using: String.Encoding, usingEncoding: NSUTF8StringEncoding)
            //设置请求体
            //  request.HTTPBody = paraData
        }
        //默认session配置
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        //发起请求
        let dataTask = session.dataTask(with: request as URLRequest) { (data, response, error) in

            //            let str:String! = String(data: data!, encoding: NSUTF8StringEncoding)
            //            print("str:\(str)")
            //转Json
            let jsonData: NSDictionary = try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as! NSDictionary

            print(jsonData)

        }
        //请求开始
        dataTask.resume()

    }


}
