package com.lotstock.eddid.ibmp.param;

import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class JSRSAUtils {

    public static KeyPair generateKeyPair() {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
            generator.initialize(1024, random);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair getKeyPair(String keyPath) throws Exception {
        return getKeyPair(new FileInputStream(keyPath));
    }

    public static KeyPair getKeyPair(InputStream in) throws Exception {
        ObjectInputStream oos = new ObjectInputStream(in);
        KeyPair kp = (KeyPair) oos.readObject();
        oos.close();
        in.close();
        return kp;
    }

    /********************************
     *
     * @Description  将数据库中读取的str公钥转为 PublicKey
     * @MethodName   test
     * @param        publicKeyString
     * @return       java.security.PublicKey
     * @Author       fancw
     * @Date         2019/1/3  9:34
     *
     *******************************/
    public static PublicKey strToPublicKey(String publicKeyString) {
        PublicKey publicKey = null;
        try {
            java.security.spec.X509EncodedKeySpec bobPubKeySpec = new java.security.spec.X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(publicKeyString));
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            publicKey = keyFactory.generatePublic(bobPubKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicKey;
    }


    public static void saveKeyPair(KeyPair kp, String keyPath) throws Exception {
        FileOutputStream fos = new FileOutputStream(keyPath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        // 生成密钥
        oos.writeObject(kp);
        oos.close();
        fos.close();
    }

    /**
     * 生成public key
     *
     * @return
     */
    public static String generateBase64PublicKey(KeyPair keyPair) {
        RSAPublicKey key = (RSAPublicKey) keyPair.getPublic();
        return new String(Base64.encodeBase64(key.getEncoded()));
    }

    /**
     * 生成public key
     *
     * @return
     */
    public static String generateBase64PrivateKey(KeyPair keyPair) {
        RSAPrivateKey key = (RSAPrivateKey) keyPair.getPrivate();
        return new String(Base64.encodeBase64(key.getEncoded()));
    }

    /**
     * 生成public key
     *
     * @return
     */
    public static PublicKey generatePublicKey(String encodedKey) {
        Assert.notNull(encodedKey);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            return keyFactory.generatePublic(new PKCS8EncodedKeySpec(Base64.decodeBase64(encodedKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 生成私钥
     *
     * @param encodedKey 密钥编码
     * @return 私钥
     */
    public static PrivateKey generatePrivateKey(String encodedKey) {
        Assert.notNull(encodedKey);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(encodedKey)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    public static String decryptBase64(String data, RSAPrivateKey pbk) {
        return new String(decrypt(Base64.decodeBase64(data), pbk));
    }

    public static byte[] decrypt(byte[] data, RSAPrivateKey pbk) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
//            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, pbk);
            byte[] plainText = cipher.doFinal(data);
            return plainText;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        // 生成public key
        KeyPair keyPair = generateKeyPair();
//        saveKeyPair(keyPair, "c:/RSAKey.txt");

//        KeyPair keyPair = getKeyPair("c:\\RSAKey.txt");
        System.out.println(keyPair.getPrivate());
        System.out.println(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));

        System.out.println(keyPair.getPublic());
        System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));


        String value = "G2LrbOcQTMY/B70KF/OBQ/eZ/ACHCe0SDXxO4gdg/751ulL1r0euJfMLERRERinIHj04gyT2p3kHAxFUYPAifXMkP7zSFpOrf4jRDfBxNR3lvwN6PxgJp16BW4Z88Z1pqvHNXHIZtVvjnEueoS0pmcOJOGAzSVbuT25q7zEYSO0=";
        // 解密
        System.out.println(decryptBase64(URLDecoder.decode("G2LrbOcQTMY%2FB70KF%2FOBQ%2FeZ%2FACHCe0SDXxO4gdg%2F751ulL1r0euJfMLERRERinIHj04gyT2p3kHAxFUYPAifXMkP7zSFpOrf4jRDfBxNR3lvwN6PxgJp16BW4Z88Z1pqvHNXHIZtVvjnEueoS0pmcOJOGAzSVbuT25q7zEYSO0%3D", "UTF-8"), (RSAPrivateKey) keyPair.getPrivate()));

        PrivateKey privateKey = generatePrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKsVHEt8lPaeMFXKBaqLitcQ0euCvMBCwMOAPNyuPI7yTAlz/I/mjx6Erq7YOmnI9EjU9IccqT9vCYOrxOupwOqjI1O9xeyOUJ77VC1VFRxN9I8RP6vVrGNnq4V10E+XtJTwzqXgM9p9TJeRCpXNyTod3BUFSuWrvCcuOBcEcU5vAgMBAAECgYAq++jaBnke5WvbgiCeUuUVWAKGG5j07VClTgj8oUJR4Aq+rUedmB4JWujQwT8b6QOHcQnnEIpth6ZqULPvSf1QDVjEfeHQ95r4P8IDwUExBrG1sA3ykXYzMfbBg6/cdd0sWfhUuai//+V8mLl7WTvXYKVZvFJLzIwXF+aDuBNAgQJBANuCAnkuaQComSM+em3kz6HQjxn135DyAskAZjtFhZfe1BLATGE5kgvJI+69R2YQ+HmObS2J+y96LS7+BB1tAE8CQQDHhiyw25D2yZNvwk+xOaeQJO1ANdNd/u4oqoIaFt3vZElikdY/Lh9jh2ySgTuU+YOV3T5YPCwU1uyYb1gr6CfhAkEAuRAaL7qeEk0SlvE03PO8zer8yuFNdHFlQbtRdYU+hXT7DTgM7SdRZxhUcBIqYPXL1wfqKiubl0KFV4oXRAvC5wJBAKzLOacM8FWaKzKNkKDGcqR2JnYGZ4AvCKh1Smk3xorNSnSwImDjG9IBdXjHrQcOs/O1AIo/ylld7NbzDuQvkWECQCxw3JolcCeIJCmkGViReC1TEDmTCORLNBhBSpmN2q79K7HahGT8LAdg9AjN47X7wUQZC/tmJDdOrzlwPJ7QDNc=");
        System.out.println(Base64.encodeBase64String(privateKey.getEncoded()));

        /*PrivateKey privateKey = generatePrivateKey("MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANSz9lJGafPB6D2H7t/7ZPFV5J3DrueIFOPgf5RLjpYbyYkEEdssCKEgnTzEEIvS0FSM28GdOAlBGKvju/brpss+xA1I2eDxprdL6EAX67SgL/6ODDehqTnARaRjRz+ADlNttJTE76yYaQ7qotvAXKURsHLOTFw9cbJUU2Q/fpxvAgMBAAECgYAXEVNX9en8tR1eOvVFlkqKv9F06Kl507DMS+caKp2Smv3e3bGgDuT6sIJM+kh/GYRdfw9ALrBKlKQ71A1pCIKEmrKvIdm6cWN4tDQitZsI0XX8RShgb9geIUYB5C+JkA02kpBsfAgDY+XXAOcZvO/XMttG5seqLCx5IzVmtIQXrQJBAOuAxD1vK1wd1/42zYSnohAqMzBvD2Ka8cKow9xWskPf/HP5RZeA+UwBJM5vgW+aMzMMn16ddy6M2r6fx+rRgfMCQQDnNzB1+2JcEhAHqU+AtSw1MQtGZRLFffmQlJrcXA6ufj3qMJ59yhBIauhWJnifnmdTxbW4GHdQ9tcOU1Ghvp6VAkAc2DGxAJvD9ZfN+DJl7A5KgpCwfvnYbvfrirYql6fP7nBCWCaw7sgaCg3TBw9Idw+MUiB2+zxsxC3KqVFYLn8HAkEAgJ/ZG6ku/J0nwbtUePtQjn6sEQslOCxuaxngVHn5yOR9uQw2/y8rg73FZjecnt9WB+pSvwkNR0MqO9Pp85rBSQJBAKAjIsUxafhySWKFBXAGXQMGCiV7nRqqHD41jVVRH5SES2qYAmNmk5dEUTHZuI2XQCGuC/F+cyk/CoF0mL9gHkU=");
        String paramKey = "ybAa+aIgzBOa1pL4+CZpqnfZkNS1BL9GXunh9GdrSvkLxFYOSI/EofNVKPpknyTQ86mLm5Ql4wWIHzpJYMdBmIxjsOcxjuhCUtCdmMjL5CpLwoTmGq2Lu/k3Zf4b6dvqZLri2RBgp1Om1gNL814tPO0HITLvDtuWvRSeSYvN54c=";
        String s = JSRSAUtils.decryptBase64(paramKey, (RSAPrivateKey) privateKey);
        System.out.println("args = [" + s + "]");*/
    }

}
