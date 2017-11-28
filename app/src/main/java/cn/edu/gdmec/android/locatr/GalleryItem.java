package cn.edu.gdmec.android.locatr;

/**
 * Created by Jack on 2017/11/22.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    private double mLat;
    private double mLon;
    public double getLat(){
        return mLat;
    }
    public void setLat(double lat){
        mLat = lat;
    }
    public double getLon(){
        return mLon;
    }
    public void setLon(double lon){
        mLon = lon;
    }

    @Override
    public String toString() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption=caption;
    }

    public void setId(String id) {
        mId=id;
    }

    public void setUrl(String url) {
        mUrl=url;
    }

    public String getUrl() {
        return mUrl;
    }
}
