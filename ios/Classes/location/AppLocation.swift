//
// Created by yyjjls on 2020/7/14.
//

import Foundation
import CoreLocation

/**
 * 定位
 */
class AppLocation: NSObject, CLLocationManagerDelegate {

    private var locationManager: CLLocationManager?
    private var location: CLLocation?;
    private var adr: String?;

    private override init() {
        locationManager = CLLocationManager()
    }

    public static let getInstance = AppLocation();

    //开锁定位
    public func startLocation() {
        locationManager!.delegate = self
        locationManager!.requestAlwaysAuthorization()
        locationManager!.desiredAccuracy = kCLLocationAccuracyBest//定位最佳
        locationManager!.distanceFilter = 500.0//更新距离
        locationManager!.requestWhenInUseAuthorization()
        if (CLLocationManager.locationServicesEnabled()) {
            locationManager!.startUpdatingLocation()
           // print("定位开始")
        }
    }

    func locationManager(_ manager: CLLocationManager, didFinishDeferredUpdatesWithError error: Error?) {
       // print("获取经纬度发生错误")
        print(error)
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let thelocations: NSArray = locations as NSArray
        let location = thelocations.lastObject as! CLLocation
        self.location = location;
        locationAddress(location: location);
  /*      let latitude = location.coordinate.latitude
        let longitude = location.coordinate.longitude
        let latitudeStr = Float(latitude)
        let longitudeStr = Float(longitude)
        print("经纬度\(latitudeStr)\(longitudeStr)")
        print(latitudeStr)
        print(longitudeStr)*/
        closeLocation();
    }

    public func getLocation() -> CLLocation? {
//        let latitude = location.coordinate.latitude
//        let longitude = location.coordinate.longitude
        return location;
    }


/**
   * 关闭定位
   */
    public func closeLocation() {
        locationManager!.stopUpdatingLocation()
    }


    /**
    * 将经纬度转换成中文地址
    */
    public func locationAddress(location: CLLocation?) {
        let geoCoder = CLGeocoder();
        geoCoder.reverseGeocodeLocation(location!) { (placemarks, error) in
            if ((placemarks?.count)! > 0) {
                let placeMark = placemarks?.first
                //print(placeMark?.addressDictionary)
                self.adr = ((placeMark?.addressDictionary?["FormattedAddressLines"] as! NSArray)[0] as! String);
               // print("》》》》》》\( self.adr)")
            } else if (error == nil && placemarks?.count == 0) {//m没有地址返回
                self.adr = "";
            } else if ((error) != nil) {//获取地址错误
                print("location error\(String(describing: error))");
                self.adr = nil;
            }
        }
    }

    //获得定位好的地址
    public func getLocationAddress() -> String? {
        return adr;
    }
}
