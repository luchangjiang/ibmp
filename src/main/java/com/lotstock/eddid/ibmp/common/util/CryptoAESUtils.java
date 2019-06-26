package com.lotstock.eddid.ibmp.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 加|解密工具类
 */
public class CryptoAESUtils {

    /* 默认加密模式 */
    private final static String ALGORITHM_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    private final static String ALGORITHM_CBC_NO = "AES/CBC/NoPadding";

    private final static String ALGORITHM_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    /* 默认偏移量 */
    private final static String IV_KEY = "0102030405060708";

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        // 创建一个空的16位字节数组（默认值为0）
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return new SecretKeySpec(arrB, "AES");
    }

    private static byte[] cypto(String strKey, String ivKey, String algorithm, int mode, byte[] data) throws Exception {
        if (StringUtils.isEmpty(strKey)) {
            return null;
        }
        SecretKeySpec skeySpec = getKey(strKey);
        Cipher cipher = Cipher.getInstance(algorithm);
        byte[] plaintext = null;
        if (Cipher.ENCRYPT_MODE == mode && ALGORITHM_CBC_NO.equals(algorithm)) {
            // 跨平台填充模式（末尾填充0）
            int blockSize = cipher.getBlockSize();
            int plaintextLength = data.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            plaintext = new byte[plaintextLength];
            System.arraycopy(data, 0, plaintext, 0, data.length);
        } else {
            plaintext = data;
        }
        if (ivKey != null) {
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            cipher.init(mode, skeySpec, iv);
        } else {
            cipher.init(mode, skeySpec);
        }
        return cipher.doFinal(plaintext);
    }


    /**
     * 加密
     *
     * @param strKey    密匙
     * @param ivKey     偏移量
     * @param algorithm 加密模式
     * @param data      数据
     * @return 加密后的值 byte[]
     * @throws Exception
     */
    public static byte[] encrypt(String strKey, String ivKey, String algorithm, byte[] data) throws Exception {
        return cypto(strKey, ivKey, algorithm, Cipher.ENCRYPT_MODE, data);
    }

    /**
     * 加密
     *
     * @param strKey    密匙
     * @param ivKey     偏移量
     * @param algorithm 加密模式
     * @param strIn     数据 字符串
     * @return 加密后的值 byte[]
     * @throws Exception
     */
    public static byte[] encrypt(String strKey, String ivKey, String algorithm, String strIn) throws Exception {
        return encrypt(strKey, ivKey, algorithm, strIn.getBytes());
    }

    /**
     * 加密
     *
     * @param strKey    密匙
     * @param ivKey     偏移量
     * @param algorithm 加密模式
     * @param strIn     数据 字符串
     * @return 加密后的字符串 base64
     * @throws Exception
     */
    public static String encryptString(String strKey, String ivKey, String algorithm, String strIn, boolean isUrlSafe) throws Exception {
        byte[] encrypted = encrypt(strKey, ivKey, algorithm, strIn);
        return isUrlSafe ? Base64.encodeBase64URLSafeString(encrypted) : Base64.encodeBase64String(encrypted);
    }

    /**
     * 加密
     *
     * @param strKey 密匙
     * @param ivKey  偏移量
     * @param strIn  数据 字符串
     * @return 加密后的字符串 base64
     * @throws Exception
     */
    public static String encryptString(String strKey, String ivKey, String strIn) throws Exception {
        return encryptString(strKey, ivKey, ALGORITHM_CBC_PKCS5, strIn, true);
    }

    /**
     * 加密
     *
     * @param strKey 密匙
     * @param strIn  数据 字符串
     * @return 加密后的字符串 base64
     * @throws Exception
     */
    public static String encryptString(String strKey, String strIn) throws Exception {
        return encryptString(strKey, IV_KEY, strIn);
    }


    /**
     * 解密
     *
     * @param strKey 密匙
     * @param ivKey  偏移量
     * @param data   数据
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(String strKey, String ivKey, String algorithm, byte[] data) throws Exception {
        return cypto(strKey, ivKey, algorithm, Cipher.DECRYPT_MODE, data);
    }

    /**
     * 解密
     *
     * @param strKey 密匙
     * @param ivKey  偏移量
     * @param strIn  数据
     * @return 解密后的数据
     * @throws Exception
     */
    public static byte[] decrypt(String strKey, String ivKey, String algorithm, String strIn) throws Exception {
        return decrypt(strKey, ivKey, algorithm, Base64.decodeBase64(strIn));
    }

    /**
     * 解密
     *
     * @param strKey 密匙
     * @param ivKey  偏移量
     * @param strIn  数据
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptString(String strKey, String ivKey, String algorithm, String strIn) throws Exception {
        byte[] original = decrypt(strKey, ivKey, algorithm, strIn);
        return new String(original, "utf-8");
    }

    /**
     * 解密
     *
     * @param strKey 密匙
     * @param ivKey  偏移量
     * @param strIn  数据
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptString(String strKey, String ivKey, String strIn) throws Exception {
        return decryptString(strKey, ivKey, ALGORITHM_CBC_PKCS5, strIn);
    }

    /**
     * 解密
     *
     * @param strKey 密匙
     * @param strIn  数据
     * @return 解密后的数据
     * @throws Exception
     */
    public static String decryptString(String strKey, String strIn) throws Exception {
        return decryptString(strKey, IV_KEY, strIn);
    }


    /*public static void main(String[] args) throws Exception {
        String str110 = CyptoAESUtils.encryptString("dzpz5678@pzdz110", null, ALGORITHM_ECB_PKCS5, "123456", true);
        System.out.println(str110);
        String destr110 = CyptoAESUtils.decryptString("dzpz5678@pzdz110", null, ALGORITHM_ECB_PKCS5, str110);
        System.out.println(destr110);
        String str1 = CyptoAESUtils.encryptString("zscf1234@fcsz432", "1234zscf@fcsz432", "955630");
        System.out.println(str1);
        String str2 = CyptoAESUtils.decryptString("zscf1234@fcsz432", "1234zscf@fcsz432", "olmY6GHcHP3F2kks+YYELg==");
        System.out.println(str1);
        System.out.println(str2);

        // 跨平台（JS可用：ALGORITHM_CBC_PKCS5 | ALGORITHM_CBC_NO）
        String ttt = CyptoAESUtils.encryptString("dufy20170329java", "dufy20170329java", ALGORITHM_CBC_NO, "123", false);
        System.out.println(ttt);
        System.out.println(CyptoAESUtils.decryptString("dufy20170329java", "dufy20170329java", ALGORITHM_CBC_NO, ttt));

    }*/

    public static void main(String[] args) throws Exception {

        String s = "2spH+iz/qOp7YVBZ1uquH2MxEzT0EzMvqetIhTQb6F9aNFX+1XTmqTyrWb0h7/ZMnkHnZ04RmJtsZljUTGN4AWo3jGZYOS5Tigqkd2HgY5PZSJ8X8CmYJHcONSvKWr07obTOHANFM4lTXLgY82HAq20qn+GnNufG1GmeGO3vVEc=";
        String c644d198d815329c = CryptoAESUtils.decryptString("e6385d39ec9394f2", "f3a354d9d2b88eec", s);
        System.out.println("args = [" + c644d198d815329c + "]");
    }


}
