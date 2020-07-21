//
// Created by yyjjls on 2020/7/19.
//

import UIKit
import Foundation

//弹窗工具
public class ToastView: NSObject {

    static var instance: ToastView = ToastView()

    var windows = UIApplication.shared.windows
    let rv = UIApplication.shared.keyWindow?.subviews.first as UIView?

    //显示加载圈圈
    public func showLoadingView() {
        clear()
        let frame = CGRect(x: 0, y: 0, width: 78, height: 78)
        //let frame = UILabel(frame: CGRect(x: 0, y: 0, width: 78, height: 78))

        let loadingContainerView = UIView()
        loadingContainerView.layer.cornerRadius = 12
        loadingContainerView.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.8)

        let indicatorWidthHeight: CGFloat = 36
        let loadingIndicatorView = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.whiteLarge)
        loadingIndicatorView.frame = CGRect(x: frame.width / 2 - indicatorWidthHeight / 2, y: frame.height / 2 - indicatorWidthHeight / 2, width: indicatorWidthHeight, height: indicatorWidthHeight)
        loadingIndicatorView.startAnimating()
        loadingContainerView.addSubview(loadingIndicatorView)

        let window = UIWindow()
        window.backgroundColor = UIColor.clear
        window.frame = frame
        loadingContainerView.frame = frame

        window.windowLevel = UIWindow.Level.alert
        window.center = CGPoint(x: (rv?.center.x)!, y: rv!.center.y)
        window.isHidden = false
        window.addSubview(loadingContainerView)

        windows.append(window)

    }

//弹窗图片文字
    public func showToast(content: String, imageName: String = "icon_cool", duration: CFTimeInterval = 1.5) {
        clear()
        let frame = CGRect(x: 0, y: 0, width: 90, height: 90)

        let toastContainerView = UIView()
        toastContainerView.layer.cornerRadius = 10
        toastContainerView.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.7)

        let iconWidthHeight: CGFloat = 0
        //let iconWidthHeight: CGFloat = 36
//        let toastIconView = UIImageView(image: UIImage(named: imageName)!)
//        toastIconView.frame = CGRect(x: (frame.width - iconWidthHeight) / 2, y: 15, width: iconWidthHeight, height: iconWidthHeight)
//        toastContainerView.addSubview(toastIconView)

        let toastContentView = UILabel(frame: CGRect(x: 0, y: iconWidthHeight + 5, width: frame.height, height: frame.height - iconWidthHeight))
        toastContentView.font = UIFont.systemFont(ofSize: 13)
        toastContentView.textColor = UIColor.white
        toastContentView.text = content
        toastContentView.textAlignment = NSTextAlignment.center
        toastContainerView.addSubview(toastContentView)


        let window = UIWindow()
        window.backgroundColor = UIColor.clear
        window.frame = frame
        toastContainerView.frame = frame

        window.windowLevel = UIWindow.Level.alert
        window.center = CGPoint(x: (rv?.center.x)!, y: rv!.center.y * 16 / 10)
        window.isHidden = false
        window.addSubview(toastContainerView)
        windows.append(window)

        toastContainerView.layer.add(AnimationUtil.getToastAnimation(duration: duration), forKey: "animation")
        perform(#selector(removeToast), with: window, afterDelay: duration)
    }



//移除当前弹窗
    @objc public func removeToast(sender: AnyObject) {
        if let window = sender as? UIWindow {
            if let index = windows.firstIndex(where: { (item) -> Bool in
                return item == window
            }) {
                // print("find the window and remove it at index \(index)")
                windows.remove(at: index)
            }
        } else {
            // print("can not find the window")
        }
    }

//清除所有弹窗
    public func clear() {
        NSObject.cancelPreviousPerformRequests(withTarget: self)
        windows.removeAll(keepingCapacity: false)
    }

}

public class AnimationUtil {

//弹窗动画
    static func getToastAnimation(duration: CFTimeInterval = 1.5) -> CAAnimation {
        // 大小变化动画
        let scaleAnimation = CAKeyframeAnimation(keyPath: "transform.scale")
        scaleAnimation.keyTimes = [0, 0.1, 0.9, 1]
        scaleAnimation.values = [0.5, 1, 1, 0.5]
        scaleAnimation.duration = duration

        // 透明度变化动画
        let opacityAnimaton = CAKeyframeAnimation(keyPath: "opacity")
        opacityAnimaton.keyTimes = [0, 0.8, 1]
        opacityAnimaton.values = [0.5, 1, 0]
        opacityAnimaton.duration = duration

        // 组动画
        let animation = CAAnimationGroup()
        animation.animations = [scaleAnimation, opacityAnimaton]
        //动画的过渡效果1. kCAMediaTimingFunctionLinear//线性 2. kCAMediaTimingFunctionEaseIn//淡入 3. kCAMediaTimingFunctionEaseOut//淡出4. kCAMediaTimingFunctionEaseInEaseOut//淡入淡出 5. kCAMediaTimingFunctionDefault//默认
        animation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.linear)

        animation.duration = duration
        animation.repeatCount = 0// HUGE
        animation.isRemovedOnCompletion = false

        return animation
    }
}