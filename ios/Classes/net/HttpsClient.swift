//
//  HttpsClient.swift
//  flutterwitsystem
//
//  Created by yyjjls on 2020/7/3.
//网络请求

import Foundation

class HttpsClient{
    
    
    
    
       /**
         POST请求
         */
   public static  func POSTACtion(urlStr:String,param:[String: String])->NSDictionary {
            //请求URL
        let url:NSURL! = NSURL(string: urlStr.contains("https") ? urlStr : "http://witsystem.top"+urlStr);
            let request:NSMutableURLRequest = NSMutableURLRequest(url: url as URL)
            let list  = NSMutableArray()
           // var paramDic = [String: String]()
            if param.count > 0 {
                //设置为POST请求
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
                
               // request.httpBody = getBody(params: params).data(using: String.Encoding.utf8)
            }
            //默认session配置
            let config = URLSessionConfiguration.default
            let session = URLSession(configuration: config)
            var jsonData:NSDictionary? = nil ;
            //发起请求
            let dataTask = session.dataTask(with: request as URLRequest) { (data, response, error) in

                let str:String! = String(data: data!, encoding: String.Encoding.utf8)
                //            print("str:\(str)")
                print("网络请求值\(url)" );
                print("网络请求值\(response)" );
                //转Json
              //  jsonData = try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as! NSDictionary

                print(jsonData ?? "没有值");
               
            }
            //请求开始
            dataTask.resume()
        return jsonData ?? [String: String]() as NSDictionary;
        }


    
    
    
    
    
    
      /**
         GET请求
         */
        func GETACtion() {
            //请求URL
            let url:NSURL! = NSURL(string: "http://iappfree.candou.com:8080/free/applications/limited")
            let request:NSMutableURLRequest = NSMutableURLRequest(url: url as URL)
            let list  = NSMutableArray()
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
                let jsonData:NSDictionary = try! JSONSerialization.jsonObject(with: data!, options: .mutableContainers) as! NSDictionary

                print(jsonData)

            }
            //请求开始
            dataTask.resume()

        }

    
}
