package com.dezhoutuu.zhuan.Banner;


public class BannerEntity {
    private String bg;
    private String img;

    public String getBg() {
        return bg == null ? "" : bg;
    }

    public void setBg(String bg) {
        this.bg = bg;
    }

    public String getImg() {
        return img == null ? "" : img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
