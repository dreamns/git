package com.cmri.universalapp.base.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.cmri.universalapp.BaseStubFragment;
import com.cmri.universalapp.BuildConfig;
import com.cmri.universalapp.R;
import com.cmri.universalapp.base.UniversalApp;
import com.cmri.universalapp.device.gateway.device.view.devicelist.DeviceListPresenter;
import com.cmri.universalapp.index.model.TabModel;
import com.cmri.universalapp.index.view.CustomFragment;
import com.cmri.universalapp.util.MyLogger;
import com.cmri.universalapp.util.TraceUtil;
import com.cmri.universalapp.voipinterface.IVoipManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 基础的带有viewpager的activity。<br>
 * 从本质意义上讲，每个Fragment 的 TitleBar 是属于activity的。只不过是在Fragment中进行自定义、初始化。
 * <p/>
 * Created by hyper-zyn on 2015/10/29.
 */
public class BaseContainerActivity extends BaseFragmentActivity {

    private static MyLogger sMyLogger = MyLogger.getLogger(BaseContainerActivity.class.getSimpleName());
    private DotBottomButton[] bottomTabs;
    public BaseStubFragment[] fragments;
    private boolean[] mAttachState;// 标识当前fragment的attach state
    private Map<Fragment, Integer> fragmentSort;
    private BlockViewPager mMainContainerViewPager;
    private FragmentContainerAdapter mFragmentContainerAdapter;
    private ViewGroup mBottomBarContainer;
    private ViewGroup mTitleBarContainer;
    private TabClickListener mTabClickListener;
    private int mCurrentPosition = -1;// 当前选中tab的位置。
    private boolean isFromRestore = false; // 标示是不是从saveInstance状态还原回来。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sMyLogger.d("onCreate-->");
        restoreFromInstance(savedInstanceState);
        super.onCreate(savedInstanceState);

        if(BuildConfig.UNIT_TEST_FLAG == 0) {
            IVoipManager.getInstance().addActivity(this);
        }

        sortFromRestore();
        setContentView(getContentViewId());
        mBottomBarContainer = (ViewGroup) findViewById(R.id.linear_layout_main_bottom_bar);
        if (isTitleBarEnable()) {
            mTitleBarContainer = (ViewGroup) findViewById(R.id.relative_layout_main_title_bar);
        }
        mMainContainerViewPager = (BlockViewPager) findViewById(R.id.viewpager_main_container);
        setClickListenerToTabs();
        setFragmentState();
        if (isTitleBarEnable() && bottomTabs.length != fragments.length) {
            throw new IllegalArgumentException("The fragment's size is not matching with tab's size which is sub of BottomBar");
        }
        mMainContainerViewPager.setBlock(isPageScrollable());
        mMainContainerViewPager.addOnPageChangeListener(new ViewPagerChangeListener());
        removeFragments();
        mFragmentContainerAdapter = new FragmentContainerAdapter(getSupportFragmentManager(), fragments);
        mMainContainerViewPager.setAdapter(mFragmentContainerAdapter);
        mMainContainerViewPager.setOffscreenPageLimit(5);
        showAtRightPosition(savedInstanceState);
    }
    /**
     *
     */
    public void refreshViewPager(TabModel tabModel){
        setClickListenerToTabs();
         if(fragments.length>4&&tabModel!=null){
             ((CustomFragment)fragments[4]).setTabUrlName(tabModel.getH5Url(),tabModel.getH5Title(),tabModel.getDisclaimer());
             ((CustomFragment)fragments[4]).hasProcessedUrl = false;
             if(((CustomFragment)fragments[4]).isAttached()) {
                 ((CustomFragment) fragments[4]).preLoadUrl();
                 ((CustomFragment) fragments[4]).showDisclaimer();
             }
         }
        mFragmentContainerAdapter.notifyDataSetChanged();
    }
    private void removeFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseStubFragment) {
                transaction.remove(fragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private void restoreFromInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) { // 还原阶段。此时所有的UI没有初始化
            mCurrentPosition = savedInstanceState.getInt("mCurrentPosition");
            isFromRestore = true;
            mAttachState = savedInstanceState.getBooleanArray("mAttachState");
            fragments = new BaseStubFragment[mAttachState.length];
            fragmentSort = new HashMap<>(mAttachState.length + 2);
            sMyLogger.d("restoreFromInstance--> mCurrentPosition = " + mCurrentPosition);
            for (int i = 0; i < mAttachState.length; i++) {
                sMyLogger.d("restoreFromInstance-->position = " + i + " , state = " + mAttachState[i]);
            }
        } else {
            mCurrentPosition = 0; // 不是从还原状态回来。
        }
    }

    /**
     * 给定要显示的布局文件。其中三个元素。ViewPager id 为 viewpager_main_container，<br>
     * LinearLayout以及其子类 id 为linear_layout_main_bottom_bar,为底部tabs的容器。<br>
     * ViewGroup以及其子类 id 为relative_layout_main_title_bar，为title bar 的容器。如果{@link #isTitleBarEnable()}
     * 返回false,则此控件可以被添加。
     *
     * @return resources id
     */
    public int getContentViewId() {
        return 0;
    }

    /**
     * 设置是否使用TitleBar，默认使用。<br>
     * 如果设置为false,则{@link #getContentViewId()}中给定的布局，可以没有title bar的container
     *
     * @return true显示，false其他
     */
    public boolean isTitleBarEnable() {
        return true;
    }

    /**
     * 设置是否允许左右滑动切换page，默认使用。<br>
     *
     * @return true允许，false其他
     */
    public boolean isPageScrollable() {
        return true;
    }

    /**
     * 计算应该显示的正确位置。<br>
     * 如果是从还原状态回来，则将所有内容还原至save instance前.否则，将显示0位置的内容。
     *
     * @param savedInstanceState 之前save的状态
     */
    private void showAtRightPosition(Bundle savedInstanceState) {
        sMyLogger.d("showAtRightPosition");
        if (savedInstanceState != null) {// 还原之前的选中状态
            sMyLogger.d("showAtRightPosition --> from save instance.");
            bottomTabs[0].setChecked(false);
            savedInstanceState.clear();
        }
        bottomTabs[mCurrentPosition].setChecked(true);
        mMainContainerViewPager.post(new Runnable() {
            @Override
            public void run() {
                if (mMainContainerViewPager == null) {
                    return;
                }
                sMyLogger.d("showAtRightPosition --> position is " + mCurrentPosition);
                mMainContainerViewPager.setCurrentItem(mCurrentPosition, false);
                if (mCurrentPosition == 0) {
                    // 因为position 触发不了 view pager onPageChange
                    // 所以在这里进行手动的调用
                    fragments[0].onSelectChange(true);
                    // fragmentTitleShow(mCurrentPosition);

                }
                fragmentTitleShow(mCurrentPosition); // todo： may be remove this line

            }
        });
        isFromRestore = false;
    }

    private void sortFromRestore() {
        if (!isFromRestore) {
            return;
        }
        Iterator<Fragment> iterator = fragmentSort.keySet().iterator();
        while (iterator.hasNext()) {
            BaseStubFragment fragment = (BaseStubFragment) iterator.next();
            int fragmentPosition = fragment.getPositionInContainer();
            if (fragmentPosition != -1
                    && fragments[fragmentPosition] == null
                    && mAttachState[fragmentPosition]) {
                fragments[fragmentPosition] = fragment;
                fragmentSort.put(fragment, fragmentPosition);
                mAttachState[fragmentPosition] = true;
                sMyLogger.d("onAttachFragment-->from restore-->position in fragments = " + fragmentPosition);
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof BaseStubFragment) {
            if (isFromRestore) { // 从onSaveInstance还原，此时mCurrentPosition不可能等于-1.
                sMyLogger.d("onAttachFragment-->restore --. fragment name = " + fragment.getClass().getSimpleName());
                fragmentSort.put(fragment, 0);
                // 在这里先记录所有的Fragment。在activity super . on create以后进行排序
            } else { // 标识当前attach的fragment的位置.
                mAttachState[fragmentSort.get(fragment)] = true;
                sMyLogger.d("onAttachFragment-->position = " + fragmentSort.get(fragment) + " , name = " + fragment.getClass().getSimpleName());
            }
        } else {
            sMyLogger.d("onAttachFragment-->restore --. fragment is not BaseStubFragment's instance and will skip it.");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sMyLogger.d("onSaveInstanceState-->position = " + mCurrentPosition);
        outState.putInt("mCurrentPosition", mCurrentPosition);
        outState.putBooleanArray("mAttachState", mAttachState);
    }

    private void setFragmentState() {
        if (isFromRestore) { // 说明只是从save instance 状态 restore 回来的
            // 循环只获取没有还原回来的数据
            int size = fragments.length;
            for (int i = 0; i < size; i++) {
                if (fragments[i] == null) {
                    fragments[i] = getFragment(i)[0];
                    fragments[i].setPositionInContainer(i);
                    fragmentSort.put(fragments[i], i);
                    sMyLogger.d(fragments[i].getClass().getSimpleName() + " setFragmentState-->position = " + i);
                }
            }
            return;
        }
        fragments = getFragment(-1);
        if (fragments == null) {
            throw new IllegalArgumentException("The fragment's size must be not 0.");
        }
        if (fragmentSort == null) {
            fragmentSort = new HashMap<>(fragments.length + 2);
        }
        if (mAttachState == null) {
            mAttachState = new boolean[fragments.length];
        }
        for (int i = 0; i < fragments.length; i++) {
            // 刚获取，初始化位置
            BaseStubFragment fragment = fragments[i];
            fragment.setPositionInContainer(i);
            fragmentSort.put(fragment, i);
        }
    }

    private void setClickListenerToTabs() {
        bottomTabs = getBottomTabs(mBottomBarContainer);
        if (bottomTabs == null) {
            bottomTabs = getViewChildren(mBottomBarContainer);
        }
        if (mTabClickListener == null) {
            mTabClickListener = new TabClickListener();
        }
        for (int i = 0; i < bottomTabs.length; i++) {
            if (bottomTabs[i] != null) {
                bottomTabs[i].setOnClickListener(mTabClickListener);
            }
        }
    }

    /**
     * 获取group里边的CompoundButton。
     *
     * @param group 源group
     * @return group里的CompoundButton
     */
    private DotBottomButton[] getViewChildren(@NonNull ViewGroup group) {
        int size = group.getChildCount();
        int count = 0;
        for (int i = 0; i < size; i++) { // 计算CompoundButton的数量。
            if (group.getChildAt(i) instanceof DotBottomButton) {
                count++;
            }
        }
        DotBottomButton[] views = new DotBottomButton[count];
        count = 0;
        for (int i = 0; i < size; i++) {
            if (group.getChildAt(i) instanceof DotBottomButton) {
                views[count++] = (DotBottomButton) group.getChildAt(i);
            }
        }
        return views;
    }

    /**
     * 改变底部tab选择
     *
     * @param position 被选择的下标，丛0开始。
     */
    protected void changeBottomTab(int position) {

        if (mCurrentPosition<bottomTabs.length&&bottomTabs[mCurrentPosition].isChecked()) {
            bottomTabs[mCurrentPosition].setChecked(false);
        }
        if (!bottomTabs[position].isChecked()) {
            bottomTabs[position].setChecked(true);
        }
        mCurrentPosition = position;
    }

    /**
     * 负责通知新的跟旧的fragment进行Title处理。<br>
     * <b>注意：</b>调用此函数默认新旧fragment都已经被attach.
     * fix:将fragment show、hide 控制粒度细化。使其适应title变化
     *
     * @param oldPosition 将要被隐藏的fragment
     * @param newPosition 将要显示的fragment
     */
    private void fragmentChanged(int oldPosition, int newPosition) {
        if (!isTitleBarEnable()) { //不是用title bar
            fragmentHide(oldPosition);
            fragmentShow(newPosition);
            return;
        }
        fragmentTitleHide(oldPosition);
        fragmentTitleShow(newPosition);
        fragmentHide(oldPosition);
        fragmentShow(newPosition);
    }

    /**
     * 当fragment所在tab 页被选中
     *
     * @param position tab页位置
     */
    private void fragmentShow(int position) {
        fragments[position].onSelectChange(true);
    }

    /**
     * 当从fragment所在tab 页被滑动走
     *
     * @param position tab页位置
     */
    private void fragmentHide(int position) {
        fragments[position].onSelectChange(false);
    }

    /**
     * 当fragment显示的时候调用
     *
     * @param position 要显示title bar的位置。
     */
    private void fragmentTitleShow(int position) {
        sMyLogger.d("fragmentTitleShow--> position is " + position + " , fragment " + fragments[position].getClass().getSimpleName());
        if (!isTitleBarEnable()) {
            //不使用title bar
            return;
        }

        View newTitle = fragments[position].setTitleBar(mTitleBarContainer);
        if (mTitleBarContainer.getChildCount() == 0) {
            if (newTitle == null) {
                sMyLogger.d("fragmentTitleShow--> new title is null branch");
                if (mTitleBarContainer.getVisibility() == View.VISIBLE) {
                    mTitleBarContainer.setVisibility(View.GONE);
                }
            } else {
                sMyLogger.d("fragmentTitleShow--> new title is not null branch");

                if (mTitleBarContainer.getVisibility() != View.VISIBLE) {
                    mTitleBarContainer.setVisibility(View.VISIBLE);
                }
                if (newTitle.getParent() != null) {

                    ((ViewGroup) newTitle.getParent()).removeView(newTitle);
                    sMyLogger.d("fragmentTitleShow--> new title will be removed");

                }
                mTitleBarContainer.addView(newTitle);
                sMyLogger.d("fragmentTitleShow--> add new title");
            }
        } else {
            sMyLogger.d("fragmentTitleShow--> title container's child is not 0, skip in position " + position);
        }
        sMyLogger.d("fragmentTitleShow--> new title be added in position " + position);
    }

    /**
     * 当fragment隐藏的时候调用
     *
     * @param position 要隐藏title bar的位置。
     */
    private void fragmentTitleHide(int position) {
        if (!isTitleBarEnable()) {
            //不使用title bar
            return;
        }
        fragments[position].removeTitleBar();
        if (mTitleBarContainer.getChildCount() > 0) {
            mTitleBarContainer.removeAllViews();
        }
    }

    /**
     * 在此阶段，应该把所有将要显示在BottomBar的控件add进mBottomBarContainer中。并将作为teb的控件返回。
     *
     * @return 如果返回null, 则将会直接获取mBottomBarContainer的children.
     */
    public DotBottomButton[] getBottomTabs(@NonNull ViewGroup mBottomBarContainer) {
        return null;
    }

    /**
     * 设置要加载的fragments.
     *
     * @param position 如果为-1，则返回所有的fragments.如果为>=0的数值，则返回指定位置的fragment.,
     * @return 返回需要加载显示的fragment.
     */
    public BaseStubFragment[] getFragment(int position) {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        IVoipManager.getInstance().finishActivity(this);

        bottomTabs = null;
        fragments = null;
        mAttachState = null;
        fragmentSort.clear();
        fragmentSort = null;
        mMainContainerViewPager = null;
        mBottomBarContainer = null;
        mTitleBarContainer = null;
        mTabClickListener = null;
    }

    @Override
    public void refershFragment(int position, int category) {
        super.refershFragment(position, category);
        if (fragments == null) {
            return;
        }
        if (position < 0 || position >= fragments.length) {
            return;
        }
        fragments[position].refreshFragment(category, false);
    }

    /**
     * 跳至指定的tab.
     *
     * @param position 指定的位置.从0开始
     */
    public void selectTab(final int position) {
        if (position < 0 || position > bottomTabs.length - 1) {
            sMyLogger.d("out of bounds of page number");
            return;
        }

        // record current module
        UniversalApp.MODULE currentModule;
        if (position == 1) {//我的设备
            currentModule = UniversalApp.MODULE.DEVICE;
        } else if (position == 0) {//我的网关
            TraceUtil.onEvent(this, "NetTab_Tab");
            currentModule = UniversalApp.MODULE.GATEWAY;
            // 刷新设备首页数据
            DeviceListPresenter deviceListPresenter = DeviceListPresenter.getInstance();
            if (deviceListPresenter != null) {
                deviceListPresenter.moveToTop();
                deviceListPresenter.refresh();
            }
        } else if (position == 2) {//我的家人
            TraceUtil.onEvent(this, "FamiliyTab_Tab");
            currentModule = UniversalApp.MODULE.FAMILY;
        } else if (position == 3) {//发现生活
            currentModule = UniversalApp.MODULE.FIND;
        } else  if(position == 4){
            currentModule = UniversalApp.MODULE.CUSTOM;
        } else {
            currentModule = UniversalApp.MODULE.UNKNOWN;
        }
        UniversalApp.getInstance().setCurrentmodule(currentModule);
        mMainContainerViewPager.post(new Runnable() {
            @Override
            public void run() {
                mMainContainerViewPager.setCurrentItem(position, false);
            }
        });
    }

    class TabClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int i;
            for (i = 0; i < bottomTabs.length; i++) {
                if (bottomTabs[i] == v) {
                    // 找到tab的位置
                    break;
                }
            }
            if (mCurrentPosition == i) { // 如果的是已经选择的tab,则保持View的状态，并返回。
                DotBottomButton cb = (DotBottomButton) v;
                mFragmentContainerAdapter.mFragments[i].refreshFragment(0, true);
                cb.setChecked(true);
                return;
            }
//            mMainContainerViewPager.setCurrentItem(i, false);
            mFragmentContainerAdapter.mFragments[i].refreshFragment(0, false);
            selectTab(i);
            sMyLogger.d("TabClickListener-->onClick-->position = " + i);
        }
    }

    class ViewPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {
        }

        @Override
        public void onPageSelected(int i) {
            sMyLogger.d("ViewPagerChangeListener --> onPageSelected --> new position = " + i + " , mCurrentPosition = " + mCurrentPosition);
            fragmentChanged(mCurrentPosition, i);
            changeBottomTab(i);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    class FragmentContainerAdapter extends FragmentPagerAdapter {
        public BaseStubFragment[] mFragments = null;
        public void setData(BaseStubFragment[] fragments){
            mFragments = fragments;
        }
        public FragmentContainerAdapter(FragmentManager fm, BaseStubFragment[] fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public BaseStubFragment getItem(int arg0) {
            BaseStubFragment fragment = mFragments[arg0];
            sMyLogger.d("FragmentContainerAdapter --> getItem --> position = " + arg0 + " , fagment name = " + fragment.getClass().getSimpleName());
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

    }

}
