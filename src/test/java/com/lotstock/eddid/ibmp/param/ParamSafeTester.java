package com.lotstock.eddid.ibmp.param;

import com.lotstock.eddid.ibmp.common.util.CryptoAESUtils;
import com.lotstock.eddid.ibmp.common.util.RSAUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;


public class ParamSafeTester {

    public static void main(String[] args) throws Exception {
        String fileName = "C:\\tmp\\RSAKey.txt";
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
            KeyPair kp = RSAUtils.generateKeyPair();
            RSAUtils.saveKeyPair(kp, fileName);
        }

        KeyPair keyPair = RSAUtils.loadKeyPair(fileName);

        System.out.println(keyPair.getPrivate());
        System.out.println(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));

        System.out.println(keyPair.getPublic());
        System.out.println(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));

//        String src = UUID.randomUUID().toString().replaceAll("-", "");
        String src = "742a40c4e88646b2a0ec661127a5cbe2";
        String value = RSAUtils.encryptBase64(src, keyPair.getPublic(), "RSA/None/PKCS1Padding");
//        String value = "G2LrbOcQTMY/B70KF/OBQ/eZ/ACHCe0SDXxO4gdg/751ulL1r0euJfMLERRERinIHj04gyT2p3kHAxFUYPAifXMkP7zSFpOrf4jRDfBxNR3lvwN6PxgJp16BW4Z88Z1pqvHNXHIZtVvjnEueoS0pmcOJOGAzSVbuT25q7zEYSO0=";

        System.out.println("====================================================");
        System.out.println("参数随机KEY(加密前）:" + src);
        System.out.println("参数随机KEY(RSA加密后):" + value);
        // 解密
        System.out.println("========================参数KEY解密============================");

        System.out.println("参数随机KEY(RSA解密后):" + RSAUtils.decryptBase64(value, (RSAPrivateKey)keyPair.getPrivate(), "RSA/None/PKCS1Padding"));
        System.out.println("====================================================");

//        String param = "{\"mobile\":\"18772895081\",\"pwd\":\"e10adc3949ba59abbe56e057f20f883e\",\"deviceToken\":\"2162166161\",\"orgCode\":\"0001\",\"channelType\":\"pc\",\"versionNumber\":\"4.0.0\"}";

        String param = "{\"cmd\": {\"user_no\": \"Q1686080066\",\"password\": \"666666\",\"broker_id\": \"9999\",\"is_modify_passwd\": \"0\",\"new_passwd\": \"\"},\"version\": \"1.0\"}";
        String result = CryptoAESUtils.encryptString(
                src.substring(0, 16)
                , src.substring(16),
                "AES/CBC/NoPadding"
                , param,
                false);
        System.out.println("参数(AES加密前）:" + param);
        System.out.println("参数(AES加密后）:" + result);
        param = CryptoAESUtils.decryptString(
                src.substring(0, 16)
                , src.substring(16),
                "AES/CBC/NoPadding"
                , result);
        System.out.println("参数(AES解密后）:" + param);



    }

//    private static void Login(String uri, String token, String src) throws Exception{
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.add("Accept", "*/*");
//        requestHeaders.add("token", token);
//        requestHeaders.add("Cache-Control", "no-cache");
//        requestHeaders.add("Connection", "keep-alive");
//        requestHeaders.add("Content-Type", "application/json");
//        RestTemplate template = new RestTemplate();
//
//        String loginStr = "{\"cmd\": {\"user_no\": \"Q1686080066\",\"password\": \"666666\",\"broker_id\": \"9999\",\"is_modify_passwd\": \"0\",\"new_passwd\": \"\"},\"version\": \"1.0\"}";
//        String loginEncrypt = CryptoAESUtils.encryptString(src.substring(0, 16), src.substring(16), "AES/CBC/NoPadding", loginStr, false);
//
//        ReqParam reqParam = new ReqParam();
//        HttpEntity<ReqParam> requestEntity = new HttpEntity<ReqParam>(reqParam, requestHeaders);
//        ResponseEntity<CallbackResult> response = template.exchange(uri + "/trade_gateway/login", HttpMethod.POST, requestEntity, CallbackResult.class);
//        CallbackResult callbackResult = response.getBody();
//        if (callbackResult.getStatus() == 0) {
//            System.out.println(callbackResult.getData().toString());
//        } else {
//            System.out.println(callbackResult.getMsg());
//        }
//    }

    @Data
    @NoArgsConstructor
    public class ReqParam {
        String d;
    }


}
