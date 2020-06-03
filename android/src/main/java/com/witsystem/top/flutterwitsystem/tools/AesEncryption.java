/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.witsystem.top.flutterwitsystem.tools;


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
        return token;
    }


    public static byte[] parseHexStringToBytes(final String hex) {
        byte[] bytes = new byte[hex.length() / 2]; // every two letters in the
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
        System.arraycopy(by, 0, openLock, 1, by.length);
        return openLock;

    }

    public static byte[] authenticationData(byte[] by) {
        byte[] openLock = new byte[by.length + 1];
        openLock[0] = 0x02;
        System.arraycopy(by, 0, openLock, 1, by.length);
        return openLock;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }
}
