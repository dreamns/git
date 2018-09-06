package com.cmri.universalapp.base.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmri.universalapp.R;

/**
 * Created by zyn on 2017/6/1.
 */

public class DotBottomButton extends RelativeLayout {
    private TextView titleView;
    private ImageView dotView;
    private ImageView iconView;

    private boolean isChecked;

    public DotBottomButton(Context context) {
        super(context);
        init(context, null);
    }

    public DotBottomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DotBottomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DotBottomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_dot_bottom_item, this, true);
        iconView = (ImageView) findViewById(R.id.iv_bottom_bar_image);
        titleView = (TextView) findViewById(R.id.tv_bottom_bar_title);
        dotView = (ImageView) findViewById(R.id.iv_bottom_bar_dot);
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dot_bottom_button, 0, 0);
            Drawable iconSrc = array.getDrawable(R.styleable.dot_bottom_button_dbb_src);
            if (iconSrc != null) {
                iconView.setImageDrawable(iconSrc);
            }
            Drawable dotSrc = array.getDrawable(R.styleable.dot_bottom_button_dbb_dotSrc);
            if (dotSrc != null) {
                dotView.setImageDrawable(dotSrc);
            }
            ColorStateList stateList = array.getColorStateList(R.styleable.dot_bottom_button_dbb_titleColor);
            if (stateList != null) {
                titleView.setTextColor(stateList);
            }
            String title = array.getString(R.styleable.dot_bottom_button_dbb_title);
            if (title != null) {
                titleView.setText(title);
            }
            float titleSize = array.getDimension(R.styleable.dot_bottom_button_dbb_titleSize, 15);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleSize);
            array.recycle();
        }
        setChecked(false);
        setDotVisiable(false);
    }
    public void setTitle(String title){
        if(titleView!=null){
            titleView.setText(title);
        }
    }

    public void setIcon(Drawable drawable){
        if(iconView!=null) {
            iconView.setImageDrawable(drawable);
        }
    }
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        iconView.setEnabled(isChecked);
        titleView.setEnabled(isChecked);
        if (isChecked) {
            setDotVisiable(false);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setDotVisiable(boolean isShow) {
        if (isShow) {
            dotView.setVisibility(VISIBLE);
        } else {
            dotView.setVisibility(INVISIBLE);
        }
    }
}

