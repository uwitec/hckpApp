package com.haven.hckp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.haven.hckp.AppContext;
import com.haven.hckp.AppManager;
import com.haven.hckp.R;
import com.haven.hckp.adapter.DispathDetailAdapter;
import com.haven.hckp.adapter.StatusExpandAdapter;
import com.haven.hckp.api.ApiClient;
import com.haven.hckp.bean.OneStatusEntity;
import com.haven.hckp.bean.TwoStatusEntity;
import com.haven.hckp.bean.URLs;
import com.haven.hckp.common.StringUtils;
import com.haven.hckp.common.UIHelper;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeDetailActivity extends BaseActivity {

    private AppContext appContext;

    @ViewInject(R.id.title_tv)
    private TextView mTitleTv;

    @ViewInject(R.id.back_img)
    private ImageView backBtn;

    @ViewInject(R.id.tp_di_sn)
    private TextView diSn;

    @ViewInject(R.id.tp_di_startdate)
    private TextView diStartdate;

    @ViewInject(R.id.tp_di_enddate)
    private TextView diEnddate;

    @ViewInject(R.id.tp_di_status)
    private TextView diStatus;

    @ViewInject(R.id.tp_di_remark)
    private TextView diRemark;

    @ViewInject(R.id.lv)
    private ListView lv;

    @ViewInject(R.id.expandlist)
    private ExpandableListView expandlistView;

    @ViewInject(R.id.button)
    private BootstrapButton button;

    private Intent intent;
    private Bundle bundle;
    private List<OneStatusEntity> oneList;
    private StatusExpandAdapter statusAdapter;

    private String orderStatus = null;
    private String diId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_detail);
        ViewUtils.inject(this);

        appContext = (AppContext) getApplicationContext();

        intent = this.getIntent();
        bundle = intent.getExtras();

        mTitleTv.setText(R.string.home_detail);
        //显示返回按钮
        backBtn.setVisibility(View.VISIBLE);
        initDataView();
    }

    @OnClick({R.id.button, R.id.back_img})
    public void buttonClick(View v) {
        if (v.getId() == R.id.back_img) {
            AppManager.getAppManager().finishActivity();
        }
        if (v.getId() == R.id.button) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确定更新状态？")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            updateOrderStatus();
                        }
                    })
                    .showCancelButton(true)
                    .setCancelText("取消")
                    .setCancelClickListener(null)
                    .show();

        }
    }

    private void updateOrderStatus() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        int status = StringUtils.toInt(orderStatus, 0);
        params.put("status", StringUtils.toString(++status));
        params.put("di_id", diId);
        String newUrl = ApiClient._MakeURL(URLs.DISPARH_SET_STATUS, params, (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE));
        HttpUtils http = new HttpUtils();
        final ProgressDialog pd = ProgressDialog.show(this, null, "请稍后...");
        http.send(HttpRequest.HttpMethod.POST, newUrl, null, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                pd.dismiss();
                JSONObject obj = JSON.parseObject(objectResponseInfo.result);
                String code = obj.get("code").toString();
                if (code.equals("1")) {
                    UIHelper.ToastMessage(appContext, obj.get("msg").toString());
                    finish();
                } else {
                    UIHelper.ToastMessage(appContext, obj.get("msg").toString());
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                pd.dismiss();
            }
        });
    }

    private void initDataView() {
        String newsId = bundle.getString("di_id");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("di_id", newsId);
        params.put("r", StringUtils.randomNum());
        String newUrl = ApiClient._MakeURL(URLs.DISPARH_DETAIL, params, (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE));
        HttpUtils http = new HttpUtils();
        final ProgressDialog pd = ProgressDialog.show(this, null, "请稍后...");

        http.send(HttpRequest.HttpMethod.GET,
                newUrl,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        pd.dismiss();
                        LogUtils.i(objectResponseInfo.result);
                        JSONObject obj = JSON.parseObject(objectResponseInfo.result);
                        String code = obj.get("code").toString();
                        if (code.equals("1")) {
                            renderBaseView((Map<String, Object>) obj.get("data"));
                            renderListView((Map<String, Object>) obj.get("data"));
                            //訂單狀態
                            putTimeLineInitData();
                        } else {
                            UIHelper.ToastMessage(appContext, obj.get("msg").toString());
                            AppManager.getAppManager().finishActivity();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        pd.dismiss();
                    }
                });
    }


    private void putTimeLineInitData() {

        String[] strArray = new String[]{"订单处理", "配送"};
        String[] str1 = new String[]{"订单下发", "未报价", "报价处理", "订单接收"};
        String[] str2 = new String[]{"准备配送", "配送中", "配送完成", "货主签收", "完成"};
        String[] timeStr1;
        String[] timeStr2;
        switch (StringUtils.toInt(orderStatus)) {
            case 1:
                button.setText("下单");
                timeStr1 = new String[]{"1111", "", "", ""};
                timeStr2 = new String[]{"", "", "", "", ""};
                break;
            case 2:
                button.setText("接收运单");
                timeStr1 = new String[]{"1111", "", "", ""};
                timeStr2 = new String[]{"", "", "", "", ""};
                break;
            case 3:
                button.setText("开始运输");
                timeStr1 = new String[]{"1111", "111", "", ""};
                timeStr2 = new String[]{"", "", "", "", ""};
                break;
            case 4:
                button.setText("完成");
                timeStr1 = new String[]{"1111", "111", "111", "111"};
                timeStr2 = new String[]{"111", "", "", "", ""};
                break;
            default:
                button.setClickable(false);
                button.setText("结束");
                timeStr1 = new String[]{"1111", "111", "111", "111"};
                timeStr2 = new String[]{"11", "11", "11", "11", "111"};
                break;
        }

        oneList = new ArrayList<OneStatusEntity>();
        for (int i = 0; i < strArray.length; i++) {
            OneStatusEntity one = new OneStatusEntity();
            one.setStatusName(strArray[i]);
            List<TwoStatusEntity> twoList = new ArrayList<TwoStatusEntity>();
            String[] order = str1;
            String[] time = timeStr1;
            switch (i) {
                case 0:
                    order = str1;
                    time = timeStr1;
                    break;
                case 1:
                    order = str2;
                    time = timeStr2;
                    break;
            }

            for (int j = 0; j < order.length; j++) {
                TwoStatusEntity two = new TwoStatusEntity();
                two.setStatusName(order[j]);
                if (time[j].equals("")) {
                    two.setCompleteTime("待执行");
                    two.setIsfinished(false);
                } else {
                    two.setCompleteTime("完成");
                    two.setIsfinished(true);
                }

                twoList.add(two);
            }
            one.setTwoList(twoList);
            oneList.add(one);
        }

        statusAdapter = new StatusExpandAdapter(appContext, oneList);
        expandlistView.setAdapter(statusAdapter);
        expandlistView.setGroupIndicator(null); // 去掉默认带的箭头

        // 遍历所有group,将所有项设置成默认展开
        int groupCount = expandlistView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandlistView.expandGroup(i);
        }

    }

    private void renderListView(Map<String, Object> data) {
        List<Map<String, Object>> transportList = (List<Map<String, Object>>) data.get("transport_list");

        if (transportList != null && transportList.size() > 0) {
            lv.setAdapter(new DispathDetailAdapter(appContext, transportList, R.layout.home_detail_list_item));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Map<String, Object> news = null;
                    // 判断是否是TextView
                    if (view instanceof TextView) {
                        news = (Map<String, Object>) view.getTag();
                    } else {
                        TextView tv = (TextView) view
                                .findViewById(R.id.tp_tt_sn);
                        news = (Map<String, Object>) tv.getTag();
                    }
                    if (news == null)
                        return;

                    // 跳转到新闻详情
                    UIHelper.showDispathDetailRedirect(appContext, news);
                }
            });
        }
    }

    private void renderBaseView(Map<String, Object> data) {
        Map<String, Object> dispatchInfo = (Map<String, Object>) data.get("dispatch_info");
        diSn.setText(StringUtils.toString(dispatchInfo.get("tp_di_sn")));
        diStartdate.setText(StringUtils.toString(dispatchInfo.get("tp_di_startdate")));
        diEnddate.setText(StringUtils.toString(dispatchInfo.get("tp_di_enddate")));
        orderStatus = StringUtils.toString(dispatchInfo.get("tp_di_status"));
        String dsipathStatus = getDispathStatus(orderStatus);
        diStatus.setText(dsipathStatus);
        diRemark.setText(StringUtils.toString(dispatchInfo.get("tp_di_remark")));

        diId = StringUtils.toString(dispatchInfo.get("tp_di_id"));

    }


    private String getDispathStatus(String s) {
        switch (StringUtils.toInt(s)) {
            case 1:
                button.setText("下单");
                return "未下单";
            case 2:
                button.setText("接收运单");
                return "已下单";
            case 3:
                button.setText("开始运输");
                return "已接受";
            case 4:
                button.setText("完成");
                return "运输中";
            case 5:
                button.setClickable(false);
                button.setText("结束");
                return "已完成";
            case 6:
                button.setClickable(false);
                button.setText("结束");
                return "已中断";
        }
        return "null";
    }
}
