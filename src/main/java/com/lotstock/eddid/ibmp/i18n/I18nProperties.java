package com.lotstock.eddid.ibmp.i18n;

import com.lotstock.eddid.ibmp.constant.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Constants.CONFIG_PREFIX + ".i18n")
public class I18nProperties {

    private String resolveType;

	public String getResolveType() {
		return resolveType;
	}

	public void setResolveType(String resolveType) {
		this.resolveType = resolveType;
	}
    

}
