package cn.edu.gdmec.android.locatr;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2017/11/22.
 *
 Java 资源大全
 Java资源大全中文版
 包括：构建工具、字节码操作、集群管理、代码分析、编译器生成工具、
 外部配置工具、持续集成、数据结构、数据库、时间日期工具库、依赖注入、
 开发流程增强工具、分布式应用、分布式数据库、文档处理工具、函数式编程、
 游戏开发、GUI、高性能计算、IDE、图像处理、JSON、JVM与JDK、日志、机器学习、
 消息传递、应用监控工具、自然语言处理等。
 Python 资源大全

 Python 资源大全中文版
 包括：环境管理、包管理、构建工具、分发、
 Web框架、网络爬虫、模板引擎、数据库、数据可视化、
 图像处理、文本处理、自然语言处理、配置、命令行工具、
 地理位置、CMS、缓存、数据验证、反垃圾、GUI、游戏开发等。
 JavaScript 资源大全

 JavaScript 资源大全中文版，
 内容包括：包管理器、打包工具、加载器、
 测试框架、运行器、QA、MVC框架和库、
 模板引擎、数据可视化、编辑器、函数式编程、
 响应式编程、数据结构、存储、国际化和本地化、
 日志、正则表达式、视觉检测、代码高亮、加载状态、
 验证、幻灯片、滑块控件、表单组件、框架、地图、
 视频/音频、动画、图片处理等。
 CSS 资源大全

 CSS 资源大全中文版，
 内容包括：CSS预处理器、框架、
 CSS结构、大型网站的 CSS 开发、
 代码风格指南、命名习惯等
 */

public class FlickrFetchr {

    private static final String TAG = "FlickFetchr";

    private static final String API_KEY = "yourApiKeyHere";
    //410
    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse ( "https://api.flickr.com/services/rest/" )
            .buildUpon ()
            .appendQueryParameter ( "api_key", API_KEY )
            .appendQueryParameter ( "format", "json" )
            .appendQueryParameter ( "nojsocallback", "1" )
            .appendQueryParameter ( "extras", "url_s,geo" )
            .build();

    public byte[] getUrlBytes(String urlSpec) throws IOException{
        URL url = new URL ( urlSpec );
        HttpURLConnection connection = (HttpURLConnection)url.openConnection ();

        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream (  );
            InputStream in = connection.getInputStream ();

            if (connection.getResponseCode () != HttpURLConnection.HTTP_OK){
                throw new IOException ( connection.getResponseMessage () +
                ": with " +
                urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read (buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close ();
            return out.toByteArray ();
        }finally {
            connection.disconnect ();
        }
    }
    public String getUrlString(String urlSpec) throws IOException{
        return new String ( getUrlBytes ( urlSpec ) );
    }
    //412
    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems ( url );
    }
    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD, query);
        return downloadGalleryItems ( url );
    }

    //添加
    public List<GalleryItem> searchPhotos(Location location){
        String url = buildUrl ( location );
        return downloadGalleryItems ( url );
    }

/*    public List<GalleryItem> fetchItems(){*/
//url
    private List<GalleryItem> downloadGalleryItems(String url){

        List<GalleryItem> items = new ArrayList<> (  );

        try {
           /* String url=Uri.parse ( "https://api.flickr.com/services/rest/" )
                    .buildUpon ()
                    .appendQueryParameter ( "method", "flicker.photos.getRecent" )
                    .appendQueryParameter ( "api_key", API_KEY )
                    .appendQueryParameter ( "format", "json" )
                    .appendQueryParameter ( "nojsoncallback", "1" )
                    .appendQueryParameter ( "extras", "url_s" )
                    .build ().toString ();*/
            String jsonString=getUrlString ( url );

            Log.i ( TAG, "Received JSON: " + jsonString );

            JSONObject jsonBody = new JSONObject ( jsonString );

            parseItems ( items, jsonBody );

        }catch (JSONException je){
            Log.e ( TAG, "Failed to parse JSON", je );

        }catch (IOException ioe){
            Log.e ( TAG, "Failed to fetch items", ioe );
        }
        return items;
    }

    private String buildUrl(String method, String query){
        Uri.Builder uriBuilder = ENDPOINT.buildUpon ()
                .appendQueryParameter ( "method", method );
        if (method.equals ( SEARCH_METHOD )){
            uriBuilder.appendQueryParameter ( "text", query );
        }
        return uriBuilder.build ().toString ();
    }
//添加
    private String buildUrl(Location location){
        return ENDPOINT.buildUpon ()
                .appendQueryParameter ( "method", SEARCH_METHOD )
                .appendQueryParameter ( "lat", "" + location.getLatitude () )
                .appendQueryParameter ( "lon", "" + location.getLongitude () )
                .build ().toString ();
    }


    private void parseItems(List<GalleryItem> items, JSONObject jsonBody)
        throws IOException, JSONException{

        JSONObject photosJsonObject = jsonBody.getJSONObject ( "photos" );
        JSONArray photoJsonArray = photosJsonObject.getJSONArray ( "photo" );

        for (int i = 0; i < photoJsonArray.length (); i++){
            JSONObject photoJsonObject = photoJsonArray.getJSONObject ( i );

            GalleryItem item = new GalleryItem ();
            item.setId(photoJsonObject.getString ( "id" ));
            item.setCaption(photoJsonObject.getString ( "title" ));

            if (!photoJsonObject.has ( "url_s" )){
                continue;
            }

            item.setUrl(photoJsonObject.getString ( "url_s" ));
            item.setLat ( photoJsonObject.getDouble ( "latitude" ) );
            item.setLon ( photoJsonObject.getDouble ( "longitude" ) );
            items.add ( item );
        }
    }
}
