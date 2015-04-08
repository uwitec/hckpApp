package com.haven.hckp.bean;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haven.hckp.AppException;
import com.haven.hckp.common.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 新闻列表实体类
 */
public class DispathList extends Entity {

    public final static int CATALOG_ALL = 1;

    private int catalog;
    private int pageSize;
    private int newsCount;
    private List<Dispath> newslist = new ArrayList<Dispath>();

    public int getCatalog() {
        return catalog;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getNewsCount() {
        return newsCount;
    }

    public List<Dispath> getNewslist() {
        return newslist;
    }

    public static DispathList parse(InputStream obj) throws IOException, AppException {
        Log.i("haven", "cui-->NewsList");
        DispathList newslist = new DispathList();
        String str = StringUtils.inputStreamToStr(obj);
        Log.i("havenCui", "cui-->" + str);
        JSONObject jsonStr = JSON.parseObject(str);
        String code = jsonStr.getString("code");
        String msg = jsonStr.getString("msg");
        Notice notice = new Notice();
        notice.setMsg(code);
        notice.setMsg(msg);
        newslist.setNotice(notice);
        List<Map<String, Object>> data = (List<Map<String, Object>>) jsonStr.get("data");
        if (data != null) {
            Dispath news = null;
            for (Map<String, Object> d : data) {
                news = new Dispath();
                news.setTp_di_id(d.get("tp_di_id").toString());
                news.setTp_di_sn(d.get("tp_di_sn").toString());
                news.setTp_tc_name(d.get("tp_tc_name").toString());
                news.setTp_di_enddate(d.get("tp_di_enddate").toString());
                newslist.newslist.add(news);
            }
        }
        obj.close();
        return newslist;
    }
}