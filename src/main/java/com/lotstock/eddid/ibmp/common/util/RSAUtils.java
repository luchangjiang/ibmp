package com.lotstock.eddid.ibmp.common.util;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.UUID;

public class RSAUtils {

    /* 加密算法RSA */
    public static String ALGORITHM_DEFAULT = "RSA";
    public static String ALGORITHM_NONE_PKCS1PADDING = "RSA/None/PKCS1Padding";

    /* 签名算法 */
    public static final String ALGORITHM_SIGNATURE = "MD5withRSA";

    // 默认key大小
    public static int KEY_SIZE = 1024; // 512..

    /* 是否分片 */
    public static boolean IS_BLOCK = false;
    /* RSA最大加密明文大小 */
//    private static final int MAX_ENCRYPT_BLOCK = 117;
    /* RSA最大解密密文大小 */
//    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥对(公钥和私钥)
     */
    public static KeyPair generateKeyPair(String algorithm, int keySize) {
        try {
            // 随机数
            SecureRandom random = new SecureRandom();
//            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, "BC");
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
            generator.initialize(keySize, random);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(ALGORITHM_DEFAULT, KEY_SIZE);
    }


    /**
     * 从文件读取key
     *
     * @param keyPath 路径
     * @return
     * @throws Exception
     */
    public static KeyPair loadKeyPair(String keyPath) throws Exception {
        FileInputStream fis = null;
        ObjectInputStream oos = null;
        KeyPair kp;
        try {
            fis = new FileInputStream(keyPath);
            oos = new ObjectInputStream(fis);
            kp = (KeyPair) oos.readObject();
        } finally {
            oos.close();
            fis.close();
        }
        return kp;
    }

    /**
     * 保存到文件
     *
     * @param kp
     * @param keyPath
     * @throws Exception
     */
    public static void saveKeyPair(KeyPair kp, String keyPath) throws Exception {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(keyPath);
            oos = new ObjectOutputStream(fos);
            // 生成密钥
            oos.writeObject(kp);
        } finally {
            oos.close();
            fos.close();
        }
    }

    /**
     * 生成Base64公钥
     *
     * @return
     */
    public static String generateBase64PublicKey(KeyPair keyPair) {
        RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
        return new String(Base64.encodeBase64(key.getEncoded()));
    }

    /**
     * 生成Base64私钥
     *
     * @return
     */
    public static String generateBase64PrivateKey(KeyPair keyPair) {
        RSAPrivateKey key = (RSAPrivateKey) keyPair.getPrivate();
        return new String(Base64.encodeBase64(key.getEncoded()));
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return
     * @throws NoSuchAlgorithmException 无此算法
     * @throws InvalidKeySpecException  公钥非法
     * @throws IOException              公钥数据内容读取错误
     * @throws NullPointerException     公钥数据为空
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
//      BASE64Decoder base64Decoder = new BASE64Decoder();
//      byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
//        KeyFactory keyFactory = KeyFactory.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return
     * @throws NoSuchAlgorithmException 无此算法
     * @throws InvalidKeySpecException  公钥非法
     * @throws IOException              公钥数据内容读取错误
     * @throws NullPointerException     公钥数据为空
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return loadPublicKey(publicKeyStr, ALGORITHM_DEFAULT);
    }

    /**
     * 从字符串中加载公钥
     *
     * @param modulus        公钥数据字符串
     * @param publicExponent 公钥数据字符串
     * @return
     * @throws NoSuchAlgorithmException 无此算法
     * @throws InvalidKeySpecException  公钥非法
     * @throws IOException              公钥数据内容读取错误
     * @throws NullPointerException     公钥数据为空
     */
    public static RSAPublicKey loadPublicKey(byte[] modulus, byte[] publicExponent, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
//      BASE64Decoder base64Decoder = new BASE64Decoder();
//      byte[] buffer = base64Decoder.decodeBuffer(publicKeyStr);
//        KeyFactory keyFactory = KeyFactory.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyFactory keyFac = KeyFactory.getInstance(algorithm);
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
        return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
    }

    public static RSAPublicKey loadPublicKey(byte[] modulus, byte[] publicExponentm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return loadPublicKey(modulus, publicExponentm, ALGORITHM_DEFAULT);
    }

    /**
     * 从字符串中加载私钥
     *
     * @param publicKeyStr 私钥数据字符串
     * @return
     * @throws NoSuchAlgorithmException 无此算法
     * @throws InvalidKeySpecException  私钥非法
     * @throws IOException              私钥数据内容读取错误
     * @throws NullPointerException     私钥数据为空
     */
    public static RSAPrivateKey loadPrivateKey(String publicKeyStr, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
//            BASE64Decoder base64Decoder = new BASE64Decoder();
//            byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(publicKeyStr));
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr 私钥数据字符串
     * @return
     * @throws NoSuchAlgorithmException 无此算法
     * @throws InvalidKeySpecException  私钥非法
     * @throws IOException              私钥数据内容读取错误
     * @throws NullPointerException     私钥数据为空
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return loadPrivateKey(privateKeyStr, ALGORITHM_DEFAULT);
    }

    public static RSAPrivateKey loadRSAPrivateKey(byte[] modulus, byte[] privateExponent, String algorithm) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFac = KeyFactory.getInstance(algorithm, new org.bouncycastle.jce.provider.BouncyCastleProvider());
        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
        return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
    }

    public static RSAPrivateKey loadRSAPrivateKey(byte[] modulus, byte[] privateExponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return loadRSAPrivateKey(modulus, privateExponent, ALGORITHM_DEFAULT);
    }

    /**
     * @param data
     * @param cipher
     * @return
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private static byte[] doFinalData(byte[] data, Cipher cipher) throws BadPaddingException, IllegalBlockSizeException {
        //        int blockSize = cipher.getBlockSize();// 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
//        // 加密块大小为127
//        // byte,加密后为128个byte;因此共有2个加密块，第一个127
//        // byte第二个为1个byte
//        int outputSize = cipher.getOutputSize(data.length);// 获得加密块加密后块大小
//        int leavedSize = data.length % blockSize;
//        int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
//                : data.length / blockSize;
//        byte[] raw = new byte[outputSize * blocksSize];
//        int i = 0;
//        while (data.length - i * blockSize > 0) {
//            if (data.length - i * blockSize > blockSize)
//                cipher.doFinal(data, i * blockSize, blockSize, raw, i
//                        * outputSize);
//            else
//                cipher.doFinal(data, i * blockSize, data.length - i
//                        * blockSize, raw, i * outputSize);
//            // 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到
//            // ByteArrayOutputStream中，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了
//            // OutputSize所以只好用dofinal方法。
//            i++;
//        }
//        return raw;

//        int blockSize = cipher.getBlockSize();
//        ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
//        int j = 0;
//
//        while (raw.length - j * blockSize > 0) {
//            bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
//            j++;
//        }
//        return bout.toByteArray();

        if (!IS_BLOCK) {
            // plainText
            return cipher.doFinal(data);
        }

        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int blockSize = cipher.getBlockSize();

            byte[] decryptedData;
            int dataLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (dataLen - offSet > 0) {
                if (dataLen - offSet > blockSize) {
                    cache = cipher.doFinal(data, offSet, blockSize);
                } else {
                    cache = cipher.doFinal(data, offSet, dataLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * blockSize;
            }
            decryptedData = out.toByteArray();
            return decryptedData;
        } finally {
            IOUtils.close(out);
        }
    }


    /**
     * 公钥加密
     *
     * @param pk   加密的密钥（公钥）
     * @param data 待加密的明文数据
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, PublicKey pk, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        //Cipher负责完成加密或解密工作，基于RSA
//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Cipher cipher = Cipher.getInstance(pk.getAlgorithm(), "BC");
        Cipher cipher = Cipher.getInstance(algorithm != null ? algorithm : pk.getAlgorithm(), new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //根据公钥，对Cipher对象进行初始化
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return doFinalData(data, cipher);
    }

    public static byte[] encrypt(byte[] data, PublicKey pk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        return encrypt(data, pk, null);
    }

    public static String encryptBase64(String data, PublicKey pk, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        return Base64.encodeBase64String(encrypt(data.getBytes(), pk, algorithm));
    }

    public static String encryptBase64(String data, PublicKey pk) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        return encryptBase64(data, pk, null);
    }


    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        RSAPublicKey pk = loadPublicKey(publicKey);
        return encrypt(data, pk);
    }


    /** */
    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key pk = loadPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(pk.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return doFinalData(data, cipher);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @return
     */
    public static String decryptBase64(String data, RSAPrivateKey pbk, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        return new String(decrypt(Base64.decodeBase64(data), pbk, algorithm));
    }

    public static String decryptBase64(String data, RSAPrivateKey pbk) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        return decryptBase64(data, pbk, null);
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param pbk  私钥
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decrypt(byte[] data, RSAPrivateKey pbk, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Cipher cipher = Cipher.getInstance(pbk.getAlgorithm(), "BC");
        Cipher cipher = Cipher.getInstance(algorithm != null ? algorithm : pbk.getAlgorithm(), new org.bouncycastle.jce.provider.BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, pbk);
        return doFinalData(data, cipher);
    }

    public static byte[] decrypt(byte[] data, RSAPrivateKey pbk) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return decrypt(data, pbk, null);
    }


    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        Key pk = loadPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(pk.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return doFinalData(encryptedData, cipher);
    }

    /** */
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        Signature signature = Signature.getInstance(ALGORITHM_SIGNATURE);
        signature.initSign(loadPrivateKey(privateKey));
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        Signature signature = Signature.getInstance(ALGORITHM_SIGNATURE);
        signature.initVerify(loadPublicKey(publicKey));
        signature.update(data);
            return signature.verify(Base64.decodeBase64(sign));
    }


    public static void main(String[] args) throws Exception {
        // 生成public key
        KeyPair keyPair = generateKeyPair();
//        saveKeyPair(keyPair, "c:/RSAKey.txt");

//        KeyPair keyPair = RSAUtils.loadKeyPair(RSA_KEY_STORE_PATH);
        System.out.println("私钥：");
        System.out.println(keyPair.getPrivate());
        System.out.println(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
        System.out.println("===========================================");

        System.out.println("公钥：");
        System.out.println(keyPair.getPublic());
        System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
        System.out.println("===========================================");


        String data = UUID.randomUUID().toString();
//        String value = "G2LrbOcQTMY/B70KF/OBQ/eZ/ACHCe0SDXxO4gdg/751ulL1r0euJfMLERRERinIHj04gyT2p3kHAxFUYPAifXMkP7zSFpOrf4jRDfBxNR3lvwN6PxgJp16BW4Z88Z1pqvHNXHIZtVvjnEueoS0pmcOJOGAzSVbuT25q7zEYSO0=";
        String value = RSAUtils.encryptBase64(data, keyPair.getPublic());

        System.out.println("示例：");
        System.out.println("原始数据：" + data);
        System.out.println("加密数据：" + value);
        System.out.println("解密数据：" + decryptBase64(value, (RSAPrivateKey) keyPair.getPrivate()));

    }
}
