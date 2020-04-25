/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.witsystem.top.flutterwitsystem.tools;


/**
 * 把16进制转换成字符串
 *
 * @author yyj
 */
public class ByteToString {


  public static String bytesToHexString(byte[] src) {
    StringBuilder stringBuilder = new StringBuilder();
    if (src == null || src.length <= 0) {
      return null;
    }
    for (int i = 0; i < src.length; i++) {
      int v = src[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        stringBuilder.append(0);
      }
      stringBuilder.append(hv);
    }
    return stringBuilder.toString();
  }

  /**
   * 把16进制字符串转换成字节数组 @param hex @return
   * ke能存在问题
   */
/*  public static byte[] hexStringToByte(String hex) {
    int len = (hex.length() / 2);
    byte[] result = new byte[len];
    char[] achar = hex.toCharArray();
    for (int i = 0; i < len; i++) {
      int pos = i * 2;
      result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
    }
    return result;
  }
  private static byte toByte(char c) {
    byte b = (byte) "0123456789ABCDEF".indexOf(c);
    return b;
  }*/

  /**
   * 将字符串转为16进制
   * @param str
   * @return
   */
  public static String str2HexStr(String str) {
    char[] chars = "0123456789ABCDEF".toCharArray();
    StringBuilder sb = new StringBuilder("");
    byte[] bs = str.getBytes();
    int bit;
    for (int i = 0; i < bs.length; i++) {
      bit = (bs[i] & 0x0f0) >> 4;
      sb.append(chars[bit]);
      bit = bs[i] & 0x0f;
      sb.append(chars[bit]);
    }
    return sb.toString().trim();
  }

  /**
   * 把16进制字符串转换成字节数组 @param hex @return
   * @param data
   * @return
   */

  public static byte[] str2HexByte(String data) {
    if(1==data.length()%2) {
      return null;
    }else{
      byte[] li = new byte[data.length()/2];
      for (int i = 0; i < data.length(); i+=2) {
        int cp1 = data.codePointAt(i);
        int cp2 = data.codePointAt(i+1);
        li[i/2] = (byte)(asc2Hex(cp1)<<4|asc2Hex(cp2));
      }
      return li;
    }
  }

  private static int asc2Hex(int data){
    int li;
    if(data >= 0X30 && data <= 0X39){//0-9
      li = data - 0X30;
    }else if (data >= 0X41 && data <= 0X5A){//A-F
      li = data - 0X37;
    }else if(data >= 0X61 && data <= 0X7A){//a-f
      li = data - 0X57;
    }else{
      li = data;
    }
    return li;
  }

}
