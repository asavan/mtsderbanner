package mtstest;

import java.util.Map;


public class SmartfonInfo {
    private String articul;
    private String mainPhoto;
    private String originalUrl;
    private Map<String, String> properties;

    public SmartfonInfo() {
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }


    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

    @Override
    public String toString() {
        return "SmartfonInfo{" +
                "articul='" + articul + '\'' +
                ", mainPhoto='" + mainPhoto + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", properties=" + properties +
                '}';
    }
}
