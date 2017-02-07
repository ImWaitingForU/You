package com.chan.you.bean;

/**
 * Created by chan on 2017/2/7.
 * <p>
 * 首页菜单
 */

public class MenuBean {

    /**
     * 菜单标题
     */
    private String title;

    /**
     * 图片资源路径
     */
    private int imgId;

    public MenuBean (String title, int imgId) {
        this.title = title;
        this.imgId = imgId;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public int getImgId () {
        return imgId;
    }

    public void setImgId (int imgId) {
        this.imgId = imgId;
    }
}
