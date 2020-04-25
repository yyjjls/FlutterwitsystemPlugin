/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.witsystem.top.flutterwitsystem.tools;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class AesEncryption {

    /**
     * 这是一个加密类，更具FFF2给fff3加密
     *
     * @throws InvalidKeyException
     */
    // encrypt 加密返回加密后的token
    public static byte[] encrypt(byte[] token, String encryptionKey) {
        // 加密算法

        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec key = new SecretKeySpec(parseHexStringToBytes(encryptionKey), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(token);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        Log.i("扫描到设备", "执行加密算法" + token);
        return token;
    }


    public static byte[] parseHexStringToBytes(final String hex) {
        byte[] bytes = new byte[hex.length() / 2]; // every two letters in the
        // string are one byte
        // finally
        String part = "";
        for (int i = 0; i < bytes.length; ++i) {
            part = "0x" + hex.substring(i * 2, i * 2 + 2);
            bytes[i] = Long.decode(part).byteValue();
        }

        return bytes;
    }


    public static byte[] getOpenLockData(byte[] by) {
        byte[] openLock = new byte[by.length + 1];
        openLock[0] = 0x01;
        for (int i = 0; i < by.length; i++) {
            openLock[i + 1] = by[i];
        }
        return openLock;

    }

    public static byte[] authenticationData(byte[] by) {
        byte[] openLock = new byte[by.length + 1];
        openLock[0] = 0x02;
        for (int i = 0; i < by.length; i++) {
            openLock[i + 1] = by[i];
        }
        return openLock;
    }


}
