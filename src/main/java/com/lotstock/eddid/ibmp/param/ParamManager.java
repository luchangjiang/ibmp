package com.lotstock.eddid.ibmp.param;

import com.lotstock.eddid.ibmp.common.base.BaseResultEnum;
import com.lotstock.eddid.ibmp.common.base.ResultException;
import com.lotstock.eddid.ibmp.common.util.CryptoAESUtils;
import com.lotstock.eddid.ibmp.common.util.WebServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;

@Slf4j
public class ParamManager {

    private StringRedisTemplate stringRedisTemplate;
    //    @Autowired
//    private RedisClient redisClient;
    private ParamProperties paramProperties;
    private RSAPrivateKey privateKey;
    private KeyPair keyPair;

    public ParamManager(StringRedisTemplate stringRedisTemplate, ParamProperties authProperties) throws Exception {
        this.stringRedisTemplate = stringRedisTemplate;
        this.paramProperties = authProperties;


        if (authProperties.getPrivateKey() != null) {
            this.privateKey = (RSAPrivateKey) JSRSAUtils.generatePrivateKey(authProperties.getPrivateKey());
        } else {
            String keyPath = authProperties.getRsaKey();
            if (keyPath.startsWith("classpath:")) {
                this.keyPair = JSRSAUtils.getKeyPair(ParamManager.class.getClassLoader().getResourceAsStream(keyPath.substring(10)));
            } else {
                this.keyPair = JSRSAUtils.getKeyPair(keyPath);
            }
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        }
    }

    private String decodeParamsKey(String paramKey) throws Exception {
        System.out.println(privateKey);
        return JSRSAUtils.decryptBase64(paramKey, privateKey);
    }

    /**
     * 保存参数加密KEY，并返回会话ID到Cookie和Header
     *
     * @param request
     * @param response
     * @param paramKeyString
     * @return
     */
    public String saveParamKey(HttpServletRequest request, HttpServletResponse response, String paramKeyString) throws Exception {
        System.out.println(paramKeyString);
//        System.out.println(URLDecoder.decode(paramKeyString, "UTF-8"));
        String paramKey = decodeParamsKey(paramKeyString);
        System.out.println(paramKey);
        String token = getToken(request);
        if ("".equals(token) || paramKey == null) {
            return null;
        }

        // 将会话ID保存到Redis中
        stringRedisTemplate.execute(new RedisCallback<Void>() {
            @Override
            public Void doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(getTokenCacheKey(token), paramProperties.getTokenExpireTime(), paramKey.getBytes());
                return null;
            }
        });

        // 添加返回
        WebServletUtils.addCookie(response, paramProperties.getTokenName(), token, 3600, request.getContextPath());
        response.setHeader(paramProperties.getTokenName(), token);
        return token;
    }


    /**
     * 获取、生成会话ID
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request, boolean isCreate) {
        String token = request.getHeader(paramProperties.getTokenName());
        if (token == null) {
            token = WebServletUtils.getCookieValue(request, paramProperties.getTokenName());
        }
        if (isCreate && (token == null || "".equals(token) || "null".equals(token))) {
            token = UUID.randomUUID().toString().replace("-", "");
        }
        return token;
    }

    private String getToken(HttpServletRequest request) {
        return getToken(request, true);
    }

    private byte[] getTokenCacheKey(String token) {
        return String.format(paramProperties.getCacheKeyPattern(), token).getBytes();
    }


    public String getDecodeParamString(HttpServletRequest request, String paramString) throws Exception {
        if (StringUtils.isBlank(paramString)) {
            return paramString;
        }
        String token = getToken(request, false);
        if (token == null || "".equals(token.trim())) {
            throw new ResultException(BaseResultEnum.PARAM_TOKEN_NULL.getResultModel());
        }
        byte[] paramKey = stringRedisTemplate.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = getTokenCacheKey(token);
                byte[] val = connection.get(key);
                if (val != null) {
                    connection.expire(key, paramProperties.getTokenExpireTime());
                }
                return val;
            }
        });
        if (paramKey == null) {
            throw new ResultException(BaseResultEnum.PARAM_TOKEN_EXPIRE.getResultModel());
        }

        String paramKeyString = new String(paramKey);
        int keyLen = paramKeyString.length();
        String strKey;
        String ivKey;
        if (keyLen == 0) {
            throw new ResultException(BaseResultEnum.PARAM_TOKEN_EXPIRE.getResultModel());
        } else if (keyLen <= 16) {
            strKey = paramKeyString.substring(0, keyLen);
            ivKey = null;
        } else {
            strKey = paramKeyString.substring(0, 16);
            ivKey = paramKeyString.substring(16, keyLen);
        }
        try {
            return CryptoAESUtils.decryptString(strKey, ivKey, "AES/CBC/NoPadding", paramString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResultException(e, BaseResultEnum.PARAM_TOKEN_FAIL.getResultModel());
        }
    }


}
