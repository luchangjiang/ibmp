package com.lotstock.eddid.ibmp.cors;

import com.lotstock.eddid.ibmp.constant.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@ConfigurationProperties(prefix = Constants.CONFIG_PREFIX + ".cors")
@Data
public class CorsProperteis extends CorsConfiguration {

    /**
     * 拦截路径
     */
    private List<String> pathPatterns;

	public List<String> getPathPatterns() {
		return pathPatterns;
	}

	public void setPathPatterns(List<String> pathPatterns) {
		this.pathPatterns = pathPatterns;
	}


}
