package com.instway.app.bean;

import com.instway.app.common.StringUtils;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 接口URL实体类
 */
public class URLs implements Serializable {

	public final static String KEY = "HCKP2015";

	//public final static String HOST = "182.92.219.85";
	public final static String HOST = "www.instway.com";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";

	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	public final static String NEWS_LIST = URL_API_HOST+"index.php?apikey=app_driver&entrance=offerPrice";
	public final static String NEWS_DETAIL = URL_API_HOST+"index.php?apikey=app_driver&entrance=inquiryDetail";
    public final static String NEWS_DETAIL_POST = URL_API_HOST+"index.php?apikey=app_driver&entrance=InInquiryPrice";

	public final static String CAR_LIST = URL_API_HOST+"index.php?apikey=app_driver&entrance=MyCar";
	public final static String CREATE_CAR = URL_API_HOST+"index.php?apikey=app_driver&entrance=AddCar";
	public final static String EDIT_CAR = URL_API_HOST+"index.php?apikey=app_driver&entrance=EditDriverCar";
	public final static String DEL_CAR = URL_API_HOST+"index.php?apikey=app_driver&entrance=DeleteDriverCar";

	public final static String TEAM_LIST = URL_API_HOST+"index.php?apikey=app_driver&entrance=myMotorcade";
	public final static String TEAM_LIST_SEARCH = URL_API_HOST+"index.php?apikey=app_driver&entrance=GetTcompany";
	public final static String TEAM_ADD_POST= URL_API_HOST+"index.php?apikey=app_driver&entrance=addMyMotorcade";
	public final static String TEAM_DEL_POST= URL_API_HOST+"index.php?apikey=app_driver&entrance=DelMyMotorcade";

    public final static String LOGIN_POST = URL_API_HOST+"index.php?apikey=app_login";
    public final static String REGISTER_POST = URL_API_HOST+"index.php?apikey=app_regist";
    public final static String USER_DETAIL = URL_API_HOST+"index.php?apikey=user_get";
    public final static String USER_EDIT = URL_API_HOST+"index.php?apikey=driver_edit";

	public final static String UPLOAD_PIC_ID = URL_API_HOST+"index.php?apikey=app_driver&entrance=SaveDriverPic";
	public final static String UPLOAD_HEADPIC_ID = URL_API_HOST+"index.php?apikey=app_driver&entrance=SaveUserHeadPic";
	public final static String UPLOAD_CAR_ID = URL_API_HOST+"index.php?apikey=app_driver&entrance=SaveCarPic";
	public final static String UPLOAD_GET_GOODS = URL_API_HOST+"index.php?apikey=app_driver&entrance=SendTransportPic";

    public final static String LOCATION_DRIVER = URL_API_HOST+"index.php?apikey=app_driver&entrance=SaveTrack";

	public final static String ShippingTransport_POST = URL_API_HOST+"index.php?apikey=app_driver&entrance=ShippingTransport";
	public final static String SendTransport_POST = URL_API_HOST+"index.php?apikey=app_driver&entrance=SendTransport";

	public final static String TRANSDETAIL = URL_API_HOST+"index.php?apikey=app_driver&entrance=GetTransportDetail";

	public final static String SMS_POST = URL_API_HOST+"index.php?apikey=app_sms";

	public final static String DISPARH_LIST = URL_API_HOST+"index.php?apikey=app_driver&entrance=myDispatch";
	public final static String DISPARH_DETAIL = URL_API_HOST+"index.php?apikey=app_driver&entrance=DispatchInfo";
	public final static String DISPARH_SET_STATUS = URL_API_HOST+"index.php?apikey=app_driver&entrance=setDispatchStatus";

	public final static String UPDATE_VERSION = URL_API_HOST+"MobileAppVersion.xml";

	private final static String URL_HOST = "182.92.219.85";
	private final static String URL_WWW_HOST = "www."+URL_HOST;
	private final static String URL_MY_HOST = "my."+URL_HOST;

	private final static String URL_TYPE_NEWS = URL_WWW_HOST + URL_SPLITTER + "news" + URL_SPLITTER;
	private final static String URL_TYPE_SOFTWARE = URL_WWW_HOST + URL_SPLITTER + "p" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION = URL_WWW_HOST + URL_SPLITTER + "question" + URL_SPLITTER;
	private final static String URL_TYPE_BLOG = URL_SPLITTER + "blog" + URL_SPLITTER;
	private final static String URL_TYPE_TWEET = URL_SPLITTER + "tweet" + URL_SPLITTER;
	private final static String URL_TYPE_ZONE = URL_MY_HOST + URL_SPLITTER + "u" + URL_SPLITTER;
	private final static String URL_TYPE_QUESTION_TAG = URL_TYPE_QUESTION + "tag" + URL_SPLITTER;

	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_NEWS = 0x001;
	public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public final static int URL_OBJ_TYPE_QUESTION = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_BLOG = 0x005;
	public final static int URL_OBJ_TYPE_TWEET = 0x006;
	public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;

	private int objId;
	private String objKey = "";
	private int objType;

	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getObjKey() {
		return objKey;
	}
	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}
	public int getObjType() {
		return objType;
	}
	public void setObjType(int objType) {
		this.objType = objType;
	}

	/**
	 * 转化URL为URLs实体
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	public final static URLs parseURL(String path) {
		if(StringUtils.isEmpty(path))return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		try {
			URL url = new URL(path);
			//站内链接
			if(url.getHost().contains(URL_HOST)){
				urls = new URLs();
				//www
				if(path.contains(URL_WWW_HOST )){
					//新闻  www.oschina.net/news/27259/mobile-internet-market-is-small
					if(path.contains(URL_TYPE_NEWS)){
						objId = parseObjId(path, URL_TYPE_NEWS);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_NEWS);
					}
					//软件  www.oschina.net/p/jx
					else if(path.contains(URL_TYPE_SOFTWARE)){
						urls.setObjKey(parseObjKey(path, URL_TYPE_SOFTWARE));
						urls.setObjType(URL_OBJ_TYPE_SOFTWARE);
					}
					//问答
					else if(path.contains(URL_TYPE_QUESTION)){
						//问答-标签  http://www.oschina.net/question/tag/python
						if(path.contains(URL_TYPE_QUESTION_TAG)){
							urls.setObjKey(parseObjKey(path, URL_TYPE_QUESTION_TAG));
							urls.setObjType(URL_OBJ_TYPE_QUESTION_TAG);
						}
						//问答  www.oschina.net/question/12_45738
						else{
							objId = parseObjId(path, URL_TYPE_QUESTION);
							String[] _tmp = objId.split(URL_UNDERLINE);
							urls.setObjId(StringUtils.toInt(_tmp[1]));
							urls.setObjType(URL_OBJ_TYPE_QUESTION);
						}
					}
					//other
					else{
						urls.setObjKey(path);
						urls.setObjType(URL_OBJ_TYPE_OTHER);
					}
				}
				//my
				else if(path.contains(URL_MY_HOST)){
					//博客  my.oschina.net/szpengvictor/blog/50879
					if(path.contains(URL_TYPE_BLOG)){
						objId = parseObjId(path, URL_TYPE_BLOG);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_BLOG);
					}
					//动弹  my.oschina.net/dong706/tweet/612947
					else if(path.contains(URL_TYPE_TWEET)){
						objId = parseObjId(path, URL_TYPE_TWEET);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_TWEET);
					}
					//个人专页  my.oschina.net/u/12
					else if(path.contains(URL_TYPE_ZONE)){
						objId = parseObjId(path, URL_TYPE_ZONE);
						urls.setObjId(StringUtils.toInt(objId));
						urls.setObjType(URL_OBJ_TYPE_ZONE);
					}
					else{
						//另一种个人专页  my.oschina.net/dong706
						int p = path.indexOf(URL_MY_HOST+URL_SPLITTER) + (URL_MY_HOST+URL_SPLITTER).length();
						String str = path.substring(p);
						if(!str.contains(URL_SPLITTER)){
							urls.setObjKey(str);
							urls.setObjType(URL_OBJ_TYPE_ZONE);
						}
						//other
						else{
							urls.setObjKey(path);
							urls.setObjType(URL_OBJ_TYPE_OTHER);
						}
					}
				}
				//other
				else{
					urls.setObjKey(path);
					urls.setObjType(URL_OBJ_TYPE_OTHER);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}

	/**
	 * 解析url获得objId
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type){
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains(URL_SPLITTER)){
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		}else{
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objKey
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type){
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if(str.contains("?")){
			tmp = str.split("?");
			objKey = tmp[0];
		}else{
			objKey = str;
		}
		return objKey;
	}

	/**
	 * 对URL进行格式处理
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if(path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}
}
