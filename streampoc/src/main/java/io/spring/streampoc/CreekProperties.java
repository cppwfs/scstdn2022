package io.spring.streampoc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "io.spring.creek")
public class CreekProperties {
    
    private String  sites;

    private int threshold = 4;

    private float warningPercent = .2f;

    public String getSites() {
        return sites;
    }

    public float getWarningPercent() {
		return warningPercent;
	}

	public void setWarningPercent(float warningPercent) {
		this.warningPercent = warningPercent;
	}

	public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setSites(String sites) {
        this.sites = sites;
    }


}
