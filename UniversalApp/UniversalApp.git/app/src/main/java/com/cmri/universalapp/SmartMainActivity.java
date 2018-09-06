package com.cmri.universalapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cmri.universalapp.applink.AppLinkManager;
import com.cmri.universalapp.base.AppConstant;
import com.cmri.universalapp.base.UniversalApp;
import com.cmri.universalapp.base.http2.HttpConstant;
import com.cmri.universalapp.base.http2extension.BaseRequestTag;
import com.cmri.universalapp.base.http2extension.ResultCode;
import com.cmri.universalapp.base.http2extension.Status;
import com.cmri.universalapp.base.view.BaseContainerActivity;
import com.cmri.universalapp.base.view.DotBottomButton;
import com.cmri.universalapp.base.view.RoundImageView;
import com.cmri.universalapp.device.ability.health.domain.TimingManager;
import com.cmri.universalapp.device.gateway.device.view.devicelist.GatewayFragment;
import com.cmri.universalapp.device.gateway.gateway.domain.GatewayManager;
import com.cmri.universalapp.device.gateway.gateway.model.GateWayModel;
import com.cmri.universalapp.family.charge.domain.ChargeManager;
import com.cmri.universalapp.family.charge.domain.IChargeUseCase;
import com.cmri.universalapp.family.charge.model.ChargeEventRepertory;
import com.cmri.universalapp.family.charge.model.FluxModel;
import com.cmri.universalapp.family.charge.view.bill.AccountActivity;
import com.cmri.universalapp.family.charge.view.flux.FluxActivity;
import com.cmri.universalapp.family.charge.view.flux.adapter.FluxAdapter;
import com.cmri.universalapp.family.group.model.FamilyEventRepertory;
import com.cmri.universalapp.family.home.FamilyFragment;
import com.cmri.universalapp.family.home.NewMsgUserCase;
import com.cmri.universalapp.family.member.MemberModuleInterface;
import com.cmri.universalapp.family.member.model.FamilyMemberEventRepertory;
import com.cmri.universalapp.family.member.model.FamilyVerifyModel;
import com.cmri.universalapp.family.member.view.manager.IManagerPresenter;
import com.cmri.universalapp.family.member.view.manager.MemberManagerActivity;
import com.cmri.universalapp.family.motivation.domain.MotivationManager;
import com.cmri.universalapp.family.motivation.model.MotivationEventRepertory;
import com.cmri.universalapp.family.motivation.model.MotivationInfo;
import com.cmri.universalapp.family.motivation.model.MotivationInfoModel;
import com.cmri.universalapp.family.motivation.view.MotivationActivity;
import com.cmri.universalapp.im.event.IMNewMsgEvent;
import com.cmri.universalapp.index.ChooseCityActivity;
import com.cmri.universalapp.index.CustomerServiceDesUtil;
import com.cmri.universalapp.index.model.TabModel;
import com.cmri.universalapp.index.view.CustomFragment;
import com.cmri.universalapp.index.view.IndexFragment;
import com.cmri.universalapp.index.view.IndexWebViewActivity;
import com.cmri.universalapp.indexinterface.IndexModuleInterface;
import com.cmri.universalapp.login.action.AppUpdateManager;
import com.cmri.universalapp.login.model.PersonalInfo;
import com.cmri.universalapp.mainmodule.IMainView;
import com.cmri.universalapp.mainmodule.MainConstant;
import com.cmri.universalapp.resourcestore.CommonResource;
import com.cmri.universalapp.setting.AboutUsActivity;
import com.cmri.universalapp.setting.HeadChangeEvent;
import com.cmri.universalapp.setting.PersonalInfoActivity;
import com.cmri.universalapp.setting.SettingActivity;
import com.cmri.universalapp.setting.betainvitation.InvitationActivity;
import com.cmri.universalapp.setting.model.NicknameChangeEvent;
import com.cmri.universalapp.smarthome.MyDeviceFragment;
import com.cmri.universalapp.util.CommonUtil;
import com.cmri.universalapp.util.MyLogger;
import com.cmri.universalapp.util.NetworkUtil;
import com.cmri.universalapp.util.PackageUtil;
import com.cmri.universalapp.util.QRCodeUtils;
import com.cmri.universalapp.util.ResourceUtil;
import com.cmri.universalapp.util.ToastUtils;
import com.cmri.universalapp.util.TraceUtil;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import static com.cmri.universalapp.family.member.domain.IFamilyAdminUseCase.AgreeSource.APP_SHARE_LINK;


/**
 *
 */
public class SmartMainActivity extends BaseContainerActivity implements DefaultHardwareBackBtnHandler, IMainView {
    public static final String POSITION = MainConstant.MAIN_POSITION;
    public static final String SELECT_GATEWAY_MAC = MainConstant.MAIN_SELECT_GATEWAY_MAC;
    public static final int POSITION_FAMILY = MainConstant.MAIN_POSITION_FAMILY;
    public static final int POSITION_GATEWAY = MainConstant.MAIN_POSITION_GATEWAY;
    public static final int POSITION_DEVICE = MainConstant.MAIN_POSITION_DEVICE;
    public static int REQUEST_CODE_CHOOSE_CITY = 2;

    private static MyLogger sMyLogger = MyLogger.getLogger(SmartMainActivity.class.getSimpleName());
    public DrawerLayout mDrawerLayout;
    public NavigationView mNavigationView;
    public RelativeLayout mNavHeader;
    private LinearLayout mNavMemberManage;
    private Dialog dlgCode;
    private int currentTabIndex;
    private boolean isMenuOpen;
    private IChargeUseCase mChargeUseCase;
    private RelativeLayout mLocationRl;
    private TextView navUserLocationTv;
    private ImageView locationArrowIv;
    public static Integer[] member_back_default = {
            R.drawable.shape_head_oval_solid_red,
            R.drawable.shape_head_oval_solid_blue,
            R.drawable.shape_head_oval_solid_yellow};
    private String currentCity;
    private TabModel currentTabModel;
    private static boolean mIsAlive;
    private boolean changingCity;
    private DotBottomButton familyTabButton;
    /*******************************亲密豆相关代码************************************/
    private MotivationInfo mMotivationInfo = MotivationInfo.getInstance();
    private TextView tvSign;
    private TextView tvBean;

    /*
    *
    * */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void setCurrentTabModel() {
        String allModelString = CommonResource.getInstance().getSp().getString(TabModel.SP_KEY, "");
        List<TabModel> allModels = null;
        if (!TextUtils.isEmpty(allModelString)) {
            try {
                allModels = JSON.parseArray(allModelString, TabModel.class);
                for (TabModel model : allModels) {
                    if (PersonalInfo.getInstance().getCityCode().equals(model.getCityCode())) {
                        currentTabModel = model;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMemberManager() {
        if (TextUtils.isEmpty(PersonalInfo.getInstance().getFamilyId())) {
            mNavMemberManage.setVisibility(View.GONE);
        } else {
            mNavMemberManage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sMyLogger.d("onCreate");
        mIsAlive = true;
        setCurrentTabModel();
        IndexModuleInterface.getInstance().getTabPage(true);
        super.onCreate(savedInstanceState);

        //IVoipManager.getInstance().askForPermission(this, 1000);

        int position = getIntent().getIntExtra(POSITION, -1);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.getPath();
            Log.d("SmartMainActivity", "path --> " + path);
        }
        if (position != -1) {
            selectTab(position);
        }
        getIntent().removeExtra(POSITION);

        //加入侧边栏
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavHeader = (RelativeLayout) mNavigationView.inflateHeaderView(R.layout.nav_header);

        /*******************************亲密豆相关代码************************************/
        tvSign = (TextView) mNavHeader.findViewById(R.id.tx_sign);
        tvBean = (TextView) mNavHeader.findViewById(R.id.nav_item_integral_value);
        LinearLayout llSign = (LinearLayout) mNavHeader.findViewById(R.id.ll_sign);
        LinearLayout llBean = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_0);

        llSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, MotivationActivity.class));
            }
        });

        llBean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, MotivationActivity.class));
            }
        });
        /********************************************************************************/
//        TextView jifenText = (TextView) mNavHeader.findViewById(R.id.jifen_text);
//        Resources resources = jifenText.getResources();
//        jifenText.setText(String.format(resources.getString(R.string.family_score), "0"));
//        LinearLayout navFreeTellManage = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_0);
//        navFreeTellManage.setVisibility(View.GONE);
//        navFreeTellManage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.closeDrawers();
//                startActivity(new Intent(SmartMainActivity.this, FreeTellActivity.class));
//            }
//        });
        mLocationRl = (RelativeLayout) mNavHeader.findViewById(R.id.rl_location);
        navUserLocationTv = (TextView) mNavHeader.findViewById(R.id.nav_user_location);
        locationArrowIv = (ImageView) mNavHeader.findViewById(R.id.nav_location_arrow_right);
        LinearLayout navPhoneFare = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_fare);

        navPhoneFare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, AccountActivity.class));
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        LinearLayout navPhoneFlow = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_flow);
        navPhoneFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, FluxActivity.class));
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        RelativeLayout navLinearListFareAndflow = (RelativeLayout) mNavHeader.findViewById(R.id.nav_linear_list0);

        if (isCharge()) {
            navLinearListFareAndflow.setVisibility(View.VISIBLE);
        } else {
            navLinearListFareAndflow.setVisibility(View.GONE);
        }

        mNavMemberManage = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_1);
        mNavMemberManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                TraceUtil.onEvent(SmartMainActivity.this, "Sidebar_Familiy");
//                startActivity(new Intent(SmartMainActivity.this, MemberManagerActivity.class));
                startActivityForResult(new Intent(SmartMainActivity.this, MemberManagerActivity.class), IManagerPresenter.DELETE_OR_EXIT_FAMILY);
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        updateMemberManager();
        LinearLayout navActivityCenter = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_activity_center);
        navActivityCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(SmartMainActivity.this, IndexWebViewActivity.class);
                intent.putExtra(AppConstant.EXTRA_CONTENT_NAME, getResources().getString(R.string.activity_center));
                String url;
                if(BuildConfig.VERSION.equals("dev")){
                    url = HttpConstant.HTTPPROTOCOL + "://" + HttpConstant.SERVER + ":" + HttpConstant.PORT_USER_PROTOCLOL + "/html5Case/html/myCoupon.html?passId=${passId}&JSESSIONID=${JSESSIONID}";
                }else{
                    url = HttpConstant.HTTPSPROTOCOL + "://" + HttpConstant.SERVER + ":" + HttpConstant.PORT_DEFAULT + "/html5Case2/html/myCoupon.html?passId=${passId}&JSESSIONID=${JSESSIONID}";
                }
                intent.putExtra(AppConstant.EXTRA_URL, url);
                startActivity(intent);
                TraceUtil.onEvent(SmartMainActivity.this, "Sidebar_Coupon");
                overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
            }
        });
        LinearLayout navMemberSetting = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_2);
        navMemberSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, SettingActivity.class));
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        LinearLayout navAbout = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_3);
        navAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, AboutUsActivity.class));
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        LinearLayout navFeedback = (LinearLayout) mNavHeader.findViewById(R.id.nav_linear_list_4);
        navFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                String url = "";
                String title = "";
                url = getCustomerServiceUrl();
                title = getResources().getString(com.cmri.universalapp.index.R.string.customer_server);
                Intent customerCenter = new Intent(SmartMainActivity.this, IndexWebViewActivity.class);
                customerCenter.putExtra("title", title);
                customerCenter.putExtra("url", url);
                startActivity(customerCenter);
//                startActivity(new Intent(SmartMainActivity.this, FeedbackActivity.class));
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
        RelativeLayout navInvite = (RelativeLayout) mNavHeader.findViewById(R.id.nav_relative_list_5);
        String version = PackageUtil.getPackageVersionName(UniversalApp.getInstance(), UniversalApp.getInstance().getPackageName());
        if (BuildConfig.GREY_FLAG != null && BuildConfig.GREY_FLAG.equalsIgnoreCase("Beta") && version.toLowerCase().contains(BuildConfig.GREY_FLAG.toLowerCase())) {
            navInvite.setVisibility(View.VISIBLE);
        } else {
            navInvite.setVisibility(View.GONE);
        }
        navInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, InvitationActivity.class));
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isMenuOpen = true;
                requestApplyList();
                updateMemberManager();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isMenuOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //设置透明状态栏
//        StatusBarUtil.setColorForDrawerLayout(this, mDrawerLayout, ContextCompat.getColor(this, R.color.bgcor1), StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
        //设置侧边栏个人数据
        initPersonalInfo();
        EventBus.getDefault().register(this);

        SharedPreferences sp = UniversalApp.getContext().getSharedPreferences(UniversalApp.SP_TAG, Context.MODE_PRIVATE);
        sMyLogger.e(" SamrtMainActivity 检测升级");

        if (sp.getBoolean("isFist", true)) {
            new AppUpdateManager(this).checkVersion(PersonalInfo.getInstance().getPhoneNo(),
                    PersonalInfo.getInstance().getProvinceCode(), new AppUpdateManager.UpdateListener() {
                        @Override
                        public void onSuccess() {
                            sMyLogger.e("AppUpdateManager onSuccess");
                        }

                        @Override
                        public void onFailed() {
                            sMyLogger.e("AppUpdateManager onFailed");
                        }

                        @Override
                        public void onCancel() {
                            sMyLogger.e("AppUpdateManager onCancel");
                        }

                        @Override
                        public void onExit() {
                            sMyLogger.e("AppUpdateManager onExit");
                        }

                    });
            sp.edit().putBoolean("isFirst", false).commit();
        } else {

        }
        requestMedalList();
    }

    private String getCustomerServiceUrl() {
        String url = HttpConstant.HTTPPROTOCOL + "://" + "211.138.24.182" + ":" + "20980" + "/ngmmgw/channelapi/web/index/";
//        http://211.138.20.171:8086"+"/ngmmgw/channelapi/web/index/phone=19999999999&source=880007&clientId=000000&fromOrgId=571
        String urlString = url + "phone=" + PersonalInfo.getInstance().getPhoneNo() + "&source=880007&clientId=00085&fromOrgId=571";
        sMyLogger.e("encodedUrl --> " + urlString);
        String encodedUrl = url + CustomerServiceDesUtil.encode("phone=" + PersonalInfo.getInstance().getPhoneNo() + "&source=880007&clientId=00085&fromOrgId=571");
        sMyLogger.e("encodedUrl --> " + encodedUrl);
        return encodedUrl;
    }

    private boolean isCharge() {
        String province = PersonalInfo.getInstance().getProvince();
        for (int i = 0; i < IChargeUseCase.WHITE_LIST.length; i++) {
            if (IChargeUseCase.WHITE_LIST[i].equals(province)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestApplyList();
//        mFluxPresenter.onStart();
        onFluxAndRemainStart();
        AppLinkManager.getInstance().handleLink(this);
        updateFamilyState();
//        freshMotivation();
        getMotivationInfo();
    }

    /**
     * 获取勋章列表
     */
    public void requestMedalList() {
        TimingManager manager = TimingManager.getInstance(EventBus.getDefault());
        manager.getRemoteMedalList(PersonalInfo.getInstance().getPassId(), GatewayManager.getInstance(EventBus.getDefault()).getCurrentGateway().getDid(), "green", "green_100");
    }

    private void onFluxAndRemainStart() {
        mChargeUseCase = ChargeManager.getInstance(EventBus.getDefault());
        mChargeUseCase.fluxFree();
        mChargeUseCase.accountRemain();
    }


    /**
     * onEvent
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChargeEventRepertory.AccountHttpEvent event) {
        if (event.getTag() == null) {
            sMyLogger.e("SignHttpEvent -> tag is null.");
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            try {
                DecimalFormat format = new DecimalFormat();
                format.applyPattern("0.00");
                String accountRemain = "0";
                Double accountD = Double.parseDouble(event.getData());
                accountRemain = format.format(accountD / 100D);
                TextView phoneFare = (TextView) mNavHeader.findViewById(R.id.nav_item_phone_fare_num_tv);
                phoneFare.setText(accountRemain + "元");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }


    /**
     * 本地家庭变更事件。
     * data = true,标示家庭从无到有。data = false 标示事件从有到无。
     * 本事件如果是根据push event 生成，则tag = null.此时本事件依然有效。
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FamilyEventRepertory.FamilyChangeLocalEvent event) {
        sMyLogger.d("SmartMainActivity FamilyChangeLocalEvent");
        BaseRequestTag tag = event.getTag();
//        if (tag == null) {
//            return;
//        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            Boolean data = event.getData();
            if (data) {
                mNavMemberManage.setVisibility(View.VISIBLE);
            } else {
                mNavMemberManage.setVisibility(View.GONE);
            }
        } else {
        }

    }

    /**
     * 删除家庭Http事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FamilyEventRepertory.FamilyDeleteHttpEvent event) {
        sMyLogger.d("SmartMainActivity FamilyDeleteHttpEvent");
        BaseRequestTag tag = event.getTag();
        if (tag == null) {
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            mNavMemberManage.setVisibility(View.GONE);
        } else {
            mNavMemberManage.setVisibility(View.VISIBLE);
        }
    }


    /**
     * onEvent
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FamilyEventRepertory.FamilyCreateHttpEvent event) {
        sMyLogger.d("SmartMainActivity FamilyCreateHttpEvent");
        BaseRequestTag tag = event.getTag();
        if (tag == null) {
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            mNavMemberManage.setVisibility(View.VISIBLE);
        } else {
            mNavMemberManage.setVisibility(View.GONE);
        }

    }


    /**
     * onEvent
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChargeEventRepertory.FluxHttpEvent event) {
        if (event.getTag() == null) {
            sMyLogger.e("SignHttpEvent -> tag is null.");
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            FluxModel fluxModel = (FluxModel) event.getData();
            DecimalFormat format = new DecimalFormat();
            String remain = FluxAdapter.calLimit(Double.parseDouble(fluxModel.getRemain()), fluxModel.getType(), format);
            String[] reminArray = remain.split(";");
            TextView fluxText = (TextView) mNavHeader.findViewById(R.id.nav_item_phone_flow_num_tv);
            if (reminArray.length > 1) {
                String remainAll = reminArray[0] + reminArray[1].replace("B", "");
                if (!TextUtils.isEmpty(remainAll)) {
                    fluxText.setText(remainAll);
                }
            }
        } else {
//            adapterInfoData.updateInfoModel(charge.getFluxModel());
//            view.updateFluxInfo(adapterInfoData.getFluxInfoPosition());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sMyLogger.d("onDestroy().");
        EventBus.getDefault().unregister(this);
        mIsAlive = false;
    }


    /*
    *
    * */
    @Override
    public void changeCitySuccess() {
        currentCity = PersonalInfo.getInstance().getCity();
        navUserLocationTv.setText(currentCity);
        Toast.makeText(getApplicationContext(), String.format(getResources().getString(R.string.switch_location_to), currentCity), Toast.LENGTH_SHORT).show();
        currentTabModel = null;
        setCurrentTabModel();
        int currentTabCount = currentTabModel == null ? 0 : 1;
        refreshViewPager(currentTabModel);
        if (currentTabIndex == 4 && currentTabCount == 0) {
            currentTabIndex = 0;
        }
        changeBottomTab(currentTabIndex);
        selectTab(currentTabIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sMyLogger.d("onActivityResult --> " + requestCode + " , " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == IManagerPresenter.DELETE_OR_EXIT_FAMILY) {
            mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    selectTab(POSITION_FAMILY);
                }
            });
        } else if (requestCode == REQUEST_CODE_CHOOSE_CITY) {
            if (data != null) {
                String cityProcode = data.getStringExtra(UniversalApp.SP_TAG_CITY_PROCODE);
                if (!TextUtils.isEmpty(cityProcode) && cityProcode.split("#").length > 2
                        && !cityProcode.split("#")[0].equals(currentCity)) {
                    if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    changingCity = true;
                    fragments[0].onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        sMyLogger.d("onNewIntent");
        int position = intent.getIntExtra(POSITION, -1);
        Uri uri = intent.getData();
        if (uri != null) {
            String path = uri.getPath();
            Log.d("SmartMainActivity", "onNewIntent path --> " + path);

            List<String> segments = uri.getPathSegments();
            if (segments.size() == 3 && "main".equals(segments.get(0))
                    && "switch".equals(segments.get(1))) {
                try {
                    position = Integer.parseInt(segments.get(2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (position != -1) {
            selectTab(position);
            getIntent().removeExtra(POSITION);
        }
        //如果是网关页面需要判断是否需要切换网关
        if (position == POSITION_GATEWAY) {
            String selectGatewayMac = intent.getStringExtra(SELECT_GATEWAY_MAC);
            if (!TextUtils.isEmpty(selectGatewayMac)) {
                GatewayManager manager = GatewayManager.getInstance(EventBus.getDefault());
                if (manager.getCurrentGateway().getDid().equals(selectGatewayMac)) {//相同网关不用做切换
                    return;
                }
                //切换到指定网关
                GateWayModel model = manager.findGatewayById(selectGatewayMac);
                if (model != null) {
                    manager.switchGateway(model);
                }
            }
        }
    }

    /*
*
* */
    @Override
    public int getContentViewId() {
        return R.layout.activity_continer_base;
    }

    /*
*
* */
    @Override
    public boolean isTitleBarEnable() {
        return true;
    }

    /*
*
* */
    @Override
    public boolean isPageScrollable() {
        return false;
    }

    private Drawable loadImageFromNetwork(String address) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(address).openStream(), "image.jpg");
        } catch (IOException e) {
        }
        return drawable;
    }

    /*
*
* */
    @Override
    public DotBottomButton[] getBottomTabs(@NonNull ViewGroup bottomBarContainer) {
        bottomBarContainer.removeAllViews();
        int layoutId = R.layout.layout_main_bottom_bar_dot;
        ViewGroup bottomViewGroup = (ViewGroup) getLayoutInflater().inflate(layoutId, bottomBarContainer, true);

        final DotBottomButton customDotBottomButton = (DotBottomButton) bottomViewGroup.findViewById(R.id.check_box_bottom_tab_custom);
        int size = bottomViewGroup.getChildCount();
        if (currentTabModel == null) {
            customDotBottomButton.setVisibility(View.GONE);
        } else {
            customDotBottomButton.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Drawable>() {

                @Override
                protected Drawable doInBackground(Void... params) {
                    StateListDrawable drawable = new StateListDrawable();
                    Drawable selected = loadImageFromNetwork(currentTabModel.getSelectedImg());
                    Drawable unSelected = loadImageFromNetwork(currentTabModel.getUnSelectedImg());
                    drawable.addState(new int[]{android.R.attr.state_enabled}, selected);
                    drawable.addState(new int[]{}, unSelected);
                    return drawable;
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    super.onPostExecute(drawable);
                    customDotBottomButton.setIcon(drawable);
                }

            }.execute();
            customDotBottomButton.setTitle(currentTabModel.getTabName());
        }
        //甘肃手机号显示生活TAB页
//        if (PersonalInfo.getInstance().isShowSmartHardware()) {
//        bottomViewGroup.findViewById(R.id.la).setVisibility(View.VISIBLE);
//        } else {
//            非甘肃号码tab生活不现实
//            bottomViewGroup.findViewById(R.id.check_box_bottom_tab_find).setVisibility(View.GONE);
//            size--;
//        }
        DotBottomButton[] buttons = new DotBottomButton[size];
        for (int i = 0; i < size; i++) {
            switch (i) {
                case 4:
                    buttons[4] = (DotBottomButton) findViewById(R.id.check_box_bottom_tab_custom);//省份自定义
                    break;
                case 2:
                    buttons[2] = (DotBottomButton) findViewById(R.id.check_box_bottom_tab_gateway);//网关按钮
                    break;
                case 3:
                    buttons[3] = (DotBottomButton) findViewById(R.id.check_box_bottom_tab_device);//设备按钮
                    break;
                case 1:
                    buttons[1] = (DotBottomButton) findViewById(R.id.check_box_bottom_tab_family);//畅聊按钮
                    familyTabButton = buttons[1];
                    break;
                case 0:
                    buttons[0] = (DotBottomButton) findViewById(R.id.check_box_bottom_tab_find);//首页按钮
                    break;
                default:
                    break;
            }
        }
        return buttons;
    }

    /*
    *
    * */
    @Override
    public BaseStubFragment[] getFragment(int position) {
        sMyLogger.d("BaseStubFragment --> position = " + position);
        switch (position) {
            case 4:
                if (currentTabModel != null) {
                    return new BaseStubFragment[]{CustomFragment.getInstance(currentTabModel.getH5Url(), currentTabModel.getH5Title(), currentTabModel.getDisclaimer())};
                } else {
                    return new BaseStubFragment[]{CustomFragment.getInstance("", "", "")};
                }
            case 2:
                return new BaseStubFragment[]{GatewayFragment.getInstance()};
            case 3:
                return new BaseStubFragment[]{MyDeviceFragment.getInstance()};
            case 1:
                return new BaseStubFragment[]{new FamilyFragment()};
            case 0:
                return new BaseStubFragment[]{new IndexFragment()};
            default:
                BaseStubFragment[] fragments;
                fragments = new BaseStubFragment[5];
                fragments[0] = new IndexFragment();
                fragments[1] = new FamilyFragment();
                fragments[2] = new GatewayFragment();
                fragments[3] = new MyDeviceFragment();
                if (currentTabModel != null) {
                    fragments[4] = CustomFragment.getInstance(currentTabModel.getH5Url(), currentTabModel.getH5Title(), currentTabModel.getDisclaimer());
                } else {
                    fragments[4] = CustomFragment.getInstance("", "", "");
                }
                return fragments;
        }
    }

    /**
     * 设置成员头像
     *
     * @param headUrl     url
     * @param displayName displayName
     * @param phoneNum    PhoneNum
     */
    private void updateHead(String headUrl, String displayName, String phoneNum) {
        TextView navUserHeadDisplayName = (TextView) mNavHeader.findViewById(R.id.nav_user_head_display_name);
        RoundImageView homeMainHeadMiddle = (RoundImageView) mNavHeader.findViewById(R.id.nav_user_head_img);
        RoundImageView navUserHeadImgDefault = (RoundImageView) mNavHeader.findViewById(R.id.nav_user_head_img_default);
        if (!TextUtils.isEmpty(headUrl)) {
            sMyLogger.d("PersonalInfo.getInstance().getHeadUrl():" + headUrl);
            navUserHeadImgDefault.setVisibility(View.GONE);
            navUserHeadDisplayName.setVisibility(View.GONE);
            homeMainHeadMiddle.setVisibility(View.VISIBLE);
            Glide.with(this).load(headUrl)
                    .placeholder(R.mipmap.common_morentouxiang)
                    .error(R.mipmap.common_morentouxiang)
                    .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(homeMainHeadMiddle);
        } else {
            navUserHeadImgDefault.setVisibility(View.VISIBLE);
            navUserHeadDisplayName.setVisibility(View.VISIBLE);
            homeMainHeadMiddle.setVisibility(View.GONE);
            navUserHeadDisplayName.setText(displayName);

            int pos = 0;
            try {
                if (!TextUtils.isEmpty(phoneNum)) {
                    char[] charArr = phoneNum.toCharArray();
                    for (int i = 0; i < charArr.length; i++) {
                        pos += Integer.parseInt(String.valueOf(charArr[i]));
                    }
                }
                sMyLogger.d("personalPassID.get PhoneNum:" + phoneNum + " pos:" + pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pos = pos % 3;
            sMyLogger.d("finla pos:" + pos);
            navUserHeadImgDefault.setBackgroundResource(member_back_default[pos]);
        }
    }

    /**
     *
     */
    public void initPersonalInfo() {
        if (!TextUtils.isEmpty(PersonalInfo.getInstance().getNickname())) {
            ((TextView) mNavHeader.findViewById(R.id.nav_user_name)).setText(PersonalInfo.getInstance().getNickname());
        }
        if (!TextUtils.isEmpty(PersonalInfo.getInstance().getPhoneNo())) {
            ((TextView) mNavHeader.findViewById(R.id.nav_user_phoneNum)).setText(PersonalInfo.getInstance().getPhoneNo());
        }
        String cityProvcodeJson = getSharedPreferences(AppConstant.SP_TAG,
                Context.MODE_PRIVATE).getString(PersonalInfo.getInstance().getPassId() + "_" + AppConstant.SP_TAG_CITY_PROCODE, "");
        if (!TextUtils.isEmpty(cityProvcodeJson)) {
            String[] data = cityProvcodeJson.split("#");
            if (data.length > 1) {
                PersonalInfo.getInstance().setCity(data[0]);
                PersonalInfo.getInstance().setProvinceCode(data[1]);
                if (data.length > 2) {
                    PersonalInfo.getInstance().setCityCode(data[2]);
                }
            }
        }
        if (TextUtils.isEmpty(PersonalInfo.getInstance().getCity())) {
            currentCity = getString(R.string.default_city);
        } else {
            currentCity = PersonalInfo.getInstance().getCity();
        }
        navUserLocationTv.setText(currentCity);
        View.OnClickListener chooseCityListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                Intent chooseCity = new Intent(getApplicationContext(), ChooseCityActivity.class);
                startActivityForResult(chooseCity, REQUEST_CODE_CHOOSE_CITY);
            }
        };
        mLocationRl.setOnClickListener(chooseCityListener);
        //navUserLocationTv.setOnClickListener(chooseCityListener);
        //locationArrowIv.setOnClickListener(chooseCityListener);
        PersonalInfo personalInfo = PersonalInfo.getInstance();
        updateHead(personalInfo.getHeadUrl(), personalInfo.getDisplayName(), personalInfo.getPhoneNo());

        LinearLayout navHead = (LinearLayout) mNavHeader.findViewById(R.id.nav_head);
        navHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                startActivity(new Intent(SmartMainActivity.this, PersonalInfoActivity.class));
                TraceUtil.onEvent(SmartMainActivity.this, "Sidebar_Avatar");
//                overridePendingTransition(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
            }
        });
//        ImageView nav_user_erweima = (ImageView) mNavHeader.findViewById(R.id.nav_user_erweima);
//        nav_user_erweima.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showFamilyCode();
//            }
//        });

    }

    /*
    *
    * */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void showFamilyCode() {
        if (dlgCode == null) {
            dlgCode = new Dialog(this, R.style.dialog_noframe);
            dlgCode.setContentView(R.layout.dlg_family_code);
            dlgCode.setCancelable(false);

            TextView tvFamilyName = (TextView) dlgCode.findViewById(R.id.tvFamilyName);
            tvFamilyName.setText(PersonalInfo.getInstance().getFamilyName());

            ImageView ivCode = (ImageView) dlgCode.findViewById(R.id.ivCode);
            int width = (int) (CommonUtil.getScreenWidth(this) * 0.8);
            String fIdPid = PersonalInfo.getInstance().getFid() + ";" + PersonalInfo.getInstance().getPassId();
            Bitmap bitmap = QRCodeUtils.generateQRCode(fIdPid, width, width);
            ivCode.setImageBitmap(bitmap);

            ImageView dismissBtn = (ImageView) dlgCode.findViewById(R.id.ivCancelDlg);
            dismissBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgCode.dismiss();
                }
            });
        }
        dlgCode.show();
    }


    private void updateFamilyState() {
        if (familyTabButton != null && !familyTabButton.isChecked()) {
            NewMsgUserCase userCase = MemberModuleInterface.getInstance().getNewMsgUserCase();
            familyTabButton.setDotVisiable(userCase.hasFamilyHomeMessage());
        }
    }


    /**
     * onEvent
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMNewMsgEvent event) {
        sMyLogger.d("HasNewPushMsgEvent ");
        updateFamilyState();
    }

    /**
     *
     * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HeadChangeEvent message) {
        sMyLogger.d("onHeadChange ");
        PersonalInfo personalInfo = PersonalInfo.getInstance();
        updateHead(message.headUrl, personalInfo.getDisplayName(), personalInfo.getPhoneNo());
    }

    /**
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NicknameChangeEvent message) {
        if (!TextUtils.isEmpty(message.nickname)) {
            sMyLogger.d("onPersonNickNameChanged message.nickname :" + message.nickname);
            TextView navUserName = (TextView) mNavHeader.findViewById(R.id.nav_user_name);
            navUserName.setText(message.nickname);
        }
        if (TextUtils.isEmpty(PersonalInfo.getInstance().getHeadUrl())) {
            TextView navUserHeadDisplayName = (TextView) mNavHeader.findViewById(R.id.nav_user_head_display_name);
            navUserHeadDisplayName.setText(PersonalInfo.getInstance().getDisplayName());
        }
    }


    @Override
    protected void changeBottomTab(int position) {
        super.changeBottomTab(position);
        currentTabIndex = position;
    }

    private void requestApplyList() {
//        TextView navItemMemberManageApplynum = (TextView) mNavHeader.findViewById(R.id.nav_item_member_manage_applynum);
//        navItemMemberManageApplynum.setText("");
//        String passId = PersonalInfo.getInstance().getPassId();
//        String familyId = PersonalInfo.getInstance().getFamilyId();
//        if (PersonalInfo.ROLE_CREATOR == PersonalInfo.getInstance().getRoleflag()) {
//            IFamilyAdminUseCase useadminCase;
//            useadminCase = FamilyAdminManager.getInstance(EventBus.getDefault(), FamilyMemberRemoteDataSource.getInstance(EventBus.getDefault()));
//            useadminCase.refreshApplyList();
//            useadminCase.applyList(passId, familyId);
//        }
    }


    /**
     * @param message
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FamilyMemberEventRepertory.FamilyMemberApplyListHttpEvent message) {
        sMyLogger.i("FamilyMemberApplyList");
        BaseRequestTag tag = message.getTag();
        if (tag == null) {
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(message.getStatus().code())) {
            int count = 0;
            List<FamilyVerifyModel> familyVerifyModelList = message.getData();
            if (familyVerifyModelList.size() > 0 && PersonalInfo.getInstance().getRoleflag() == PersonalInfo.ROLE_CREATOR) {
                for (FamilyVerifyModel familyVerifyModel : familyVerifyModelList) {
                    if (familyVerifyModel.getApplyMethod() == familyVerifyModel.MEMBER_METHOD_MEMBER
                            && familyVerifyModel.getResult() == familyVerifyModel.MEMBER_RESULT_INVITE_NEW) {
                        count++;
                    }
                }
                sMyLogger.i("FamilyMemberApplyListHttpEvent count:" + count);
                if (count > 0) {
//                    TextView navItemMemberManageApplynum = (TextView) mNavHeader.findViewById(R.id.nav_item_member_manage_applynum);
//                    navItemMemberManageApplynum.setText(String.format(getString(R.string.apply_join_number), count));
                }
            }
        }
    }


    /**
     * 邀请成员
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FamilyMemberEventRepertory.FamilyMemberAgreeHttpEvent event) {
        BaseRequestTag tag = event.getTag();
        if (tag == null
                || APP_SHARE_LINK != event.getData().getOperationSource()) {
            return;
        }

        String code = event.getStatus().code();
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(code)) {
            // 成功
        } else {
            showAgreeApplyError(event.getStatus());
        }
    }


    /*
    *
    * */
    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    /*
    *
    * */
    @Override
    public Context getContext() {
        return this;
    }

    /*
    *
    * */
    @Override
    public void openDrawer(int i) {
        mDrawerLayout.openDrawer(Gravity.LEFT);
        TraceUtil.onEvent(this, "Sidebar");
    }

    /**
     * is alive or not
     *
     * @return
     */
    public static boolean isAlive() {
        return mIsAlive;
    }


    private void showAgreeApplyError(Status status) {
        String msg = status.msg();
        switch (status.code()) {
            case ResultCode.GENERAL_HTTP_TIMEOUT:
                ToastUtils.show(this, com.cmri.universalapp.family.R.string.warn_net_work_error);
                break;
            case ResultCode.GENERAL_HTTP_ERROR:
                ToastUtils.show(this, com.cmri.universalapp.family.R.string.network_data_error);
                break;
            case ResultCode.GENERAL_HTTP_FAILED:
                ToastUtils.show(this, com.cmri.universalapp.family.R.string.network_access_fail);
                break;
            case ResultCode.GENERAL_NETWORK_NO_CONNECTION:
                ToastUtils.show(this, com.cmri.universalapp.family.R.string.network_no_connection);
                break;
            default:
                ToastUtils.show(msg);
                break;
        }
    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(IndexGetTabPageHttpEvent event){
//        if(changingCity){
//            return;
//        }
//        boolean refresh = false;
//         if(event!=null){
//             if(event.getData()!=null&&event.getData().size()>0) {
//                 if (currentTabModel == null){
//                     currentTabModel = event.getData().get(0);
//                     refresh = true;
//                 }else if(!currentTabModel.compare(event.getData().get(0))){
//                     currentTabModel.copy(event.getData().get(0));
//                     refresh = true;
//                 }
//             }else{
//                 if(currentTabModel != null){
//                     currentTabModel=null;
//                     refresh = true;
//                 }
//             }
//         }
//         if(refresh){
//             int currentTabCount = currentTabModel==null?0:1;
//             refreshViewPager(currentTabModel);
//             if(currentTabIndex==4&&currentTabCount==0){
//                 currentTabIndex=0;
//             }
//             changeBottomTab(currentTabIndex);
//             selectTab(currentTabIndex);
//         }
//    }


    /*******************************亲密豆相关代码************************************/
    private void getMotivationInfo() {
        MotivationManager motivationManager = MotivationManager.getInstance(EventBus.getDefault());
        motivationManager.getMotivationInfo();
    }

    /**
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MotivationEventRepertory.MotivationInfoHttpEvent event) {
        BaseRequestTag tag = event.getTag();
        if (tag == null) {
            return;
        }
        if (ResultCode.GENERAL_HTTP_SUCCESS.equals(event.getStatus().code())) {
            MotivationInfoModel model = event.getData();

            mMotivationInfo.setBeanNumbers(model.getBeanNum());
            mMotivationInfo.setSign(model.getSignStatus() == 1);

            if (mMotivationInfo.isSign()) {
                tvSign.setText(getResources().getString(R.string.signed));
                tvSign.setTextColor(ResourceUtil.getColor(getResources(), R.color.family_signed_color));
            } else {
                tvSign.setText(getResources().getString(R.string.goto_sign));
                tvSign.setTextColor(ResourceUtil.getColor(getResources(), R.color.cor6));
            }
            tvBean.setText(mMotivationInfo.getBeanNumbers() + "");
        }
    }

//    /**
//     * 签到后需要刷新数据
//     */
//    private void freshMotivation(){
//        if (mMotivationInfo.isSign()) {
//            tvSign.setText(getResources().getString(R.string.signed));
//            tvSign.setTextColor(ResourceUtil.getColor(getResources(), R.color.family_signed_color));
//        } else {
//            tvSign.setText(getResources().getString(R.string.goto_sign));
//            tvSign.setTextColor(ResourceUtil.getColor(getResources(), R.color.cor6));
//        }
//        tvBean.setText(mMotivationInfo.getBeanNumbers() + "");
//    }
    /*******************************************************************************/
}
