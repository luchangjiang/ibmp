package com.lotstock.eddid.ibmp.controller;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.ibmp.common.base.ResultModel;
import com.lotstock.eddid.ibmp.common.util.CryptoAESUtils;
import com.lotstock.eddid.ibmp.common.util.RSAUtils;
import com.lotstock.eddid.ibmp.param.JSRSAUtils;
import com.lotstock.eddid.ibmp.param.ParamSafeTester;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.Serializable;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeControllerTest {
    @Test
    public void contextLoads() {
    }

    private MockMvc mockMvc; // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。

    @Autowired
    private WebApplicationContext wac; // 注入WebApplicationContext

    @Autowired
    RestTemplate restTemplate;

    @Before // 在测试开始前初始化工作
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void Login() throws Exception{
        String src = "742a40c4e88646b2a0ec661127a5cbe2";

        String publicKeyStr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZI1Kb7InHsnWGm6xwLBjgwynPjOH72HR5em95fYupvliD16tpGBT8OXtrihgaTPb0cR5C/FbE98Zp1IX3MvRoSc2fWS1elaYbGsyOlTNdIBGadOZJfy7ZGQB2zjeAwT2tU2u+KNjDmX0dDMgxRhVLLBSEAzx/8pvwg3DiUrFkDwIDAQAB";
        PublicKey publicKey= JSRSAUtils.strToPublicKey(publicKeyStr);

        TokenParam tokenParam = new TokenParam(RSAUtils.encryptBase64(src, publicKey, "RSA/None/PKCS1Padding"));
        String paramKey = JSONObject.toJSONString(tokenParam);

        MvcResult tokenResult = mockMvc.perform(post("/gateway/param/save_key")
                .contentType(MediaType.APPLICATION_JSON)
                .content(paramKey))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andReturn();// 返回执行请求的结果

        Map mapToken = (Map)JSONObject.parse(tokenResult.getResponse().getContentAsString());
        String token = mapToken.get("result").toString();


        String loginStr = "{\"cmd\": {\"user_no\": \"Q1686080066\",\"password\": \"666666\",\"broker_id\": \"9999\",\"is_modify_passwd\": \"0\",\"new_passwd\": \"\"},\"version\": \"1.0\"}";
        String loginEncrypt = CryptoAESUtils.encryptString(src.substring(0, 16), src.substring(16), "AES/CBC/NoPadding", loginStr, false);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Content-Type", "application/json");
        requestHeaders.add("token", token);

        ReqParam reqParam = new ReqParam(loginEncrypt);

        HttpEntity<ReqParam> requestEntity = new HttpEntity<ReqParam>(reqParam, requestHeaders);
        ResponseEntity<String> response = restTemplate.exchange("http://middleware-ibmp/trade_gateway/login", HttpMethod.POST, requestEntity, String.class);
        Map loginMap = (Map)JSONObject.parse(response.getBody().toString());
        String status = loginMap.get("status").toString();
        if ("0".equals(status)) {
            System.out.println("\r\n");
            System.out.println("\r\n");

            System.out.println(loginMap.get("data").toString());

            System.out.println("\r\n");
            System.out.println("\r\n");
            Assert.assertTrue(true);
        } else {
            System.out.println(loginMap.get("msg").toString());
        }


        /*MvcResult result = mockMvc.perform(post("http://middleware-ibmp/trade_gateway/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", token)
                .content(JSONObject.toJSONString(reqParam)))
                .andExpect(status().isOk())// 模拟向testRest发送get请求
                .andReturn();// 返回执行请求的结果

        System.out.println(result);*/
        return;
    }

    public static KeyPair getKeyPair() throws Exception{
        String fileName = "C:\\tmp\\RSAKey.txt";
        File file = new File(fileName);
        if(!file.exists()){
            file.createNewFile();
            KeyPair kp = RSAUtils.generateKeyPair();
            RSAUtils.saveKeyPair(kp, fileName);
        }

        return RSAUtils.loadKeyPair(fileName);
    }

    @Data
    @AllArgsConstructor
    public class TokenParam {
        String paramKey;
    }


    @Data
    @AllArgsConstructor
    public class ReqParam {
        String d;
    }

    @Data
    public class R implements Serializable {
        private int en;
        private String msg;
        private int status;
        private Object data;
    }
}