package com.witsystem.top.flutterwitsystem.ble;

public class BleCode {


    ///设备不支持低功耗蓝牙
    public static int DEVICE_NOT_BLUE = 100001;

    ///蓝牙没有打开
    public static int DEVICE_BLUE_OFF = 100002;

    ///扫描结束没有发现任何设备  暂时回调里面没有
    public static int SCAN_END_NOT_DEVICE = 100003;

    ///设备连接超时
    public static int CONNECTION_TIMEOUT = 100004;

    ///连接设备失败
    public static int CONNECTION_FAIL = 100005;

    ///获得蓝牙服务失败
    public static int GET_SERVICE_FAIL = 100006;

    ///获得特征值失败
    public static int GET_CHARACTERISTIC_FAIL = 100007;

    ///添加的设备不是新设备
    public static int NO_NEW_DEVICE = 100008;

    ///获取设备信息失败
    public static int GET_DEVICE_INFO_FAIL = 100009;

    ///添加的设备从没有进入设置状态变更到设置状态
    public static int SET_UP = 100010;

    ///服务器异常
    public static int SERVER_EXCEPTION = 100011;

    ///服务器认证失败 代表已经存在该设备
    public static int SERVER_VERIFY_EXCEPTION = 100012;

    ///蓝牙获取权限被拒绝
    public static int BEL_PERMISSION_DENIED = 100013;

    ///蓝牙未知的错误
    public static int BLE_UNKNOWN_MISTAKE = 100014;

    ///安全认证失败
    public static int FAILED_SECURITY_FAIL = 100016;

    ///添加的设备没有进入设置状态
    public static int NO_DEVICE_SET_UP = 100017;

    ///添加的设备从没有进入设置状态变更到设置状态
    public static int DEVICE_SET_UP = 100018;

    ///蓝牙被关闭 从开启到关闭
    public static int BLUE_OFF = 100019;

    ///开启蓝牙，从关闭到开启
    public static int BLUE_NO = 100020;


    ///蓝牙设备以外断开
    public static int UNEXPECTED_DISCONNECT = 100021;

    ///扫描超时
    public static int SCAN_OUT_TIME = 100022;

    ///GPS定位处于关闭状态 部分蓝牙需要gps的手机
    public static int NO_GPS = 100023;


    ///开锁的时候选择设备超时
    public static int SELECT_DEVICE_OVERTIME = 100024;


    ///当前一个设备都没有
    public static int NO_DEVICE = 100025;

    //手机其他app已经连接该设备
    public static int OTHER_APP_CONN_DEVICE = 100026;

    //串口认证超时
    public static int SERIAL_PORT_AUTH_OVERTIME = 100027;

    //串口认证成功
    public static int SERIAL_PORT_SUCCESS = 100028;

    //串口认证失败
    public static int SERIAL_PORT_FAIL = 100029;

    //发送数据成功
    public static int SERIAL_PORT_SEND_DATA_SUCCESS = 100030;

    //数据发送超时
    public static int SERIAL_PORT_SEND_DATA_OVERTIME = 100031;

    //蓝牙扫描失败
    public static int BLE_SCAN_FAIL = 100032;

    //当前网络不可用
    public static int NO_NETWORK = 100033;












    ///扫描到设备
    public static int SCAN_ADD_DEVICE_INFO = 200100;

    ///正在扫描 开始扫描
    public static int SCANNING = 200000;

    ///扫描结束
    public static int SCAN_END = 200001;


    ///正在连接设备
    public static int CONNECTING = 200002;

    ///连接成功
    public static int CONNECT_SUCCESS = 200003;

    ///正在进行安全认证
    public static int SECURITY_CERTIFICATION_ONGOING = 200004;

    ///安全认证完成
    public static int SAFETY_CERTIFICATION_COMPLETED = 200005;

    ///正在获取信息
    public static int ACCESS_INFORMATION = 200006;

    ///等待输入管理员密码
    public static int WAITING_INPUT_PASSWORD = 200007;

    ///输入密码完成第一次的时候
    public static int ENTER_INPUT_PASSWORD = 200008;

    ///输入两次密码不一直请重新输入
    public static int REENTER_INPUT_PASSWORD = 200009;

    ///获取信息完成
    public static int ACCESS_INFORMATION_COMPLETED = 200010;

    ///正在完成添加
    public static int ADDITIONS_BEING_COMPLETED = 200011;

    ///完成最后的添加
    public static int ADD_FINISH = 200012;

    ///添加成功
    public static int ADD_SUCCESS = 000000;

    //开门成功
    public static int UNLOCK_SUCCESS = 200000;
}
