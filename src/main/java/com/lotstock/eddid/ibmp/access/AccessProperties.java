package com.lotstock.eddid.ibmp.access;

import java.util.List;

import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.common.base.BaseFilterProperties;
import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Constants.CONFIG_PREFIX + ".access")
public class AccessProperties extends BaseFilterProperties {
    /* 用户ID标识 */
    private String userIdName = "userId";
    /* 登录凭证名称 */
    private String tokenName = "token";
    private String userPermissionUrl;
    /* 用户中心服务ID，统一用户服务ID */
    private String userServiceId;
    
    private List<String> pathPatterns;
    
	public String getUserIdName() {
		return userIdName;
	}
	public void setUserIdName(String userIdName) {
		this.userIdName = userIdName;
	}
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	public String getUserPermissionUrl() {
		return userPermissionUrl;
	}
	public void setUserPermissionUrl(String userPermissionUrl) {
		this.userPermissionUrl = userPermissionUrl;
	}
	public String getUserServiceId() {
		return userServiceId;
	}
	public void setUserServiceId(String userServiceId) {
		this.userServiceId = userServiceId;
	}
	public List<String> getPathPatterns() {
		return pathPatterns;
	}
	public void setPathPatterns(List<String> pathPatterns) {
		this.pathPatterns = pathPatterns;
	}

}
