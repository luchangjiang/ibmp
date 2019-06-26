package com.lotstock.eddid.ibmp.param;

import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.common.base.BaseFilterProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;


@ConfigurationProperties(prefix = Constants.CONFIG_PREFIX + ".param-safe")
@Data
public class ParamProperties extends BaseFilterProperties {

    /**
     * 过滤器顺序
     */
    private Integer filterOrder;
    /**
     * 参数密匙解密私密
     */
    private String privateKey;
    private String tokenName;
    private Long tokenExpireTime = 24 * 60L * 60L;
    private String cacheKeyPattern = "";
    private String rsaKey;
    private List<String> pathPatterns;

    /**
     * 服务方法映射
     */
    private Map<String, List<String>> serviceMethods;

	public Integer getFilterOrder() {
		return filterOrder;
	}

	public void setFilterOrder(Integer filterOrder) {
		this.filterOrder = filterOrder;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public Long getTokenExpireTime() {
		return tokenExpireTime;
	}

	public void setTokenExpireTime(Long tokenExpireTime) {
		this.tokenExpireTime = tokenExpireTime;
	}

	public String getCacheKeyPattern() {
		return cacheKeyPattern;
	}

	public void setCacheKeyPattern(String cacheKeyPattern) {
		this.cacheKeyPattern = cacheKeyPattern;
	}

	public String getRsaKey() {
		return rsaKey;
	}

	public void setRsaKey(String rsaKey) {
		this.rsaKey = rsaKey;
	}

	public Map<String, List<String>> getServiceMethods() {
		return serviceMethods;
	}

	public void setServiceMethods(Map<String, List<String>> serviceMethods) {
		this.serviceMethods = serviceMethods;
	}

	public List<String> getPathPatterns() {
		return pathPatterns;
	}

	public void setPathPatterns(List<String> pathPatterns) {
		this.pathPatterns = pathPatterns;
	}

}
