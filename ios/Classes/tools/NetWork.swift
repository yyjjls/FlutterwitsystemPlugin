//
// Created by yyjjls on 2020/7/15.
//

import Foundation

class NetWork {

    public static func isNetworkConnected() -> Bool {
        return requestUrl(urlString: "https://witsystem.top");
    }

/*
*  检测网络是否连接
*/
    private static func requestUrl(urlString: String) -> Bool {
        let url: URL = URL(string: urlString)!
        var request: URLRequest = URLRequest(url: url)
        request.timeoutInterval = 5
        var response: URLResponse?
        do {
            try NSURLConnection.sendSynchronousRequest(request, returning: &response)
            if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    return true
                }
            }
            return false
        } catch (let error) {
            return false
        }
    }
}
