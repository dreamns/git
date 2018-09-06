package com.cmri.universalapp.applink;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cmri.universalapp.R;
import com.cmri.universalapp.SmartMainActivity;
import com.cmri.universalapp.base.AppConstant;
import com.cmri.universalapp.family.member.MemberModuleInterface;
import com.cmri.universalapp.family.member.domain.IFamilyAdminUseCase;
import com.cmri.universalapp.index.view.IndexWebViewActivity;
import com.cmri.universalapp.login.model.PersonalInfo;
import com.cmri.universalapp.util.Base64;
import com.cmri.universalapp.util.MyLogger;

import static com.cmri.universalapp.applink.WebEntranceActivity.byteArrayToStr;

/**
 * Created by 15766_000 on 2017/5/11.
 */

public class AppLinkManager {
    private static MyLogger sMyLogger = MyLogger.getLogger(AppLinkManager.class.getSimpleName());
    private static final AppLinkManager INSTANCE = new AppLinkManager();
    public static final String APP_LINK_URL = "com.cmri.universalapp://param?";
    public static final String HAS_APP_LINK = "has_app_link";

    private AppLinkManager() {
    }

    public static AppLinkManager getInstance() {
        return INSTANCE;
    }

    private AppLinkData mLinkInfo;


    /**
     * getLinkInfo
     *
     * @return
     */
    public AppLinkData getLinkInfo() {
        return mLinkInfo;
    }

    /**
     * setLinkInfo
     *
     * @param mLinkInfo
     */
    public void setLinkInfo(AppLinkData mLinkInfo) {
        this.mLinkInfo = mLinkInfo;
    }

    /**
     * clearLinkInfo
     */
    private void clearLinkInfo() {
        setLinkInfo(null);
    }

    /**
     * handle the link from outside
     */
    public void handleLink(Context context) {
        if (context == null || ((Activity) context).isFinishing()) {
            return;
        }
        if (!SmartMainActivity.isAlive()) {
            return;
        }

        if (getLinkInfo() != null) {
            String url = getLinkInfo().getUrl();
            if (url != null) {
                if ("addFamilyUseQRCode".equalsIgnoreCase(url)) {
                    String desFamilyId = getLinkInfo().getParam().get("familyId");
                    String currentFamilyId = PersonalInfo.getInstance().getFamilyId();
                    if (desFamilyId != null && !desFamilyId.equals(currentFamilyId)) {
                        handleFamilyQRCode(context, desFamilyId, PersonalInfo.getInstance().getPassId(),
                                PersonalInfo.getInstance().getFamilyName(),
                                getLinkInfo().getParam().get("familyName"));
                    }
                } else if (url.matches("^cmcc://.*$")) {
                    Uri cmccUri = Uri.parse(url);
                    Intent cmccIntent = new Intent(Intent.ACTION_VIEW, cmccUri);
                    extractParam(url, cmccIntent);
                    context.startActivity(cmccIntent);
                } else if (url.matches("^(https?)://.*$")) {
                    Intent intent = new Intent(context, IndexWebViewActivity.class);
                    intent.putExtra(AppConstant.EXTRA_URL, url);
//                    intent.putExtra(AppConstant.EXTRA_TITLE, title);
//                    intent.putExtra(AppConstant.EXTRA_CONTENT_NAME,contentName);
                    context.startActivity(intent);
                }
            }
            clearLinkInfo();
        }
    }

    private void extractParam(String url, Intent cmccIntent) {
        if (url.endsWith("social_FamilyPhotosList")) {

        }
    }

    private void handleFamilyQRCode(Context context, final String desFamilyId, final String applyPassId,
                                    final String familyName,
                                    final String desFamilyName) {
        sMyLogger.e("handleFamilyQRCode");
        final String familyId = PersonalInfo.getInstance().getFamilyId();
        String note = "";
        if (null != familyId && familyId.length() > 0) {
            note = context.getString(R.string.join_new_family_member, familyName);
        }
        SpannableString ss = new SpannableString(desFamilyName == null ? "" : desFamilyName);
        if (ss.length() > 0) {
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#30c0b1")), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(context.getString(R.string.confirm_join_family));
        builder.append(ss);

        showConfirmDialog(context, builder, note,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MemberModuleInterface.getInstance().getAdminUseCase().agreeJoin(
                                PersonalInfo.getInstance().getPassId(),
                                "",
                                desFamilyId,
                                applyPassId,
                                1,
                                IFamilyAdminUseCase.AgreeSource.APP_SHARE_LINK
                        );
                        confirmJoinDialog.dismiss();
                    }
                });
    }

    Dialog confirmJoinDialog;

    private void showConfirmDialog(Context context, CharSequence tip, String subTip, View.OnClickListener listener) {
        if (context == null || ((Activity) context).isFinishing() || !SmartMainActivity.isAlive()) {
            return;
        }

        confirmJoinDialog = new Dialog(context, R.style.dialog_noframe);
        confirmJoinDialog.setContentView(R.layout.layout_tip_dialog_with_subtip);
        confirmJoinDialog.setCancelable(false);
        Button dismissBtn = (Button) confirmJoinDialog.findViewById(R.id.button_tip_dialog_cancel);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmJoinDialog.dismiss();
            }
        });

        TextView tipText = (TextView) confirmJoinDialog.findViewById(R.id.text_view_tip_dialog_text);
        TextView subTipText = (TextView) confirmJoinDialog.findViewById(R.id.text_view_sub_tip);
        if (subTip != null && subTip.length() > 0) {
            subTipText.setVisibility(View.VISIBLE);
        } else {
            subTipText.setVisibility(View.GONE);
        }
        tipText.setText(tip);
        subTipText.setText(subTip);
        ((Button) confirmJoinDialog.findViewById(R.id.button_tip_dialog_ok)).setOnClickListener(listener);
        if (!confirmJoinDialog.isShowing()) {
            if (context == null || ((Activity) context).isFinishing() || !SmartMainActivity.isAlive()) {
                return;
            }
            confirmJoinDialog.show();
        }
    }

    /**
     * parseLink
     *
     * @param intent
     */
    public void parseLink(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            Uri data = intent.getData();
            //data.getQueryParameter("param");
            //Pattern pattern = Pattern.compile("param.(.*?)");
            //pattern.matcher(test);
            if (data == null) {
                return;
            }

            String urlString = data.toString();
            if (urlString == null || urlString.length() == 0) {
                return;
            }
            String queryParameter64 = urlString.replace(AppLinkManager.APP_LINK_URL, "");
            sMyLogger.e(queryParameter64);
            String queryParameter = "";
            if (queryParameter64 != null && queryParameter64.length() > 0) {
                queryParameter = byteArrayToStr(Base64.decode(queryParameter64.getBytes(), Base64.DEFAULT));
            }
            sMyLogger.e(queryParameter);
            AppLinkData applinkData = JSON.parseObject(queryParameter, AppLinkData.class);
            if (applinkData != null) {
                AppLinkManager.getInstance().setLinkInfo(applinkData);
                //handleLinks(applinkData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sMyLogger.e(e.getMessage());
        } finally {

        }
    }


}
