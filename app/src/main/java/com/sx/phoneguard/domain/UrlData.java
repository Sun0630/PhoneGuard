package com.sx.phoneguard.domain;

/**
 * Created by ad on 2016/2/11.
 */
public class UrlData {
    /**
     * 解析json数据
     *      versionCode
     *      desc
     *      downloadUrl
     */

    public int versionCode;
    public String desc;
    public String downloadUrl;

    @Override
    public String toString() {
        return "UrlData{" +
                "desc='" + desc + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
