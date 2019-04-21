package com.wyx.components.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author: yongxiang.wei
 * @version: 1.2.0, 2019/4/19 16:43
 * @since: 1.2.0
 */
public class ExpandCollpaseTextView extends FrameLayout {
    //折叠状态下，默认显示2行文字
    final int COLLAPSE_SHOW_LINE_NUMBER = 2;
    //折叠状态下，最少显示1行文字
    final int COLLAPSE_SHOW_LINE_MIN_NUMBER = 1;
    //折叠状态下，显示的行数
    int mCollapseShowLineNumber = COLLAPSE_SHOW_LINE_NUMBER;
    //最大行数
    final int MAX_LINE = Integer.MAX_VALUE;

    //文本显示TextView
    TextView mTvContent;

    //当前是否是展开折叠状态，false: 折叠状态；true：展开状态
    boolean mExpanded;

    //是否正在观测View的状态——》获取TextView的宽度
    boolean mIsOberving;

    //展开状态值
    final int EXPAND_STATE = 0;
    //折叠状态值
    final int COLLAPSE_STATE = 1;

    //默认文字大小，单位是sp
    final int DEFAULT_TEXT_SIZE = 14;
    //默认文字颜色
    final int DEFAULT_TEXT_COLOR = Color.parseColor("#333333");
    //默认展开/收起文字颜色
    final int DEFAULT_EXPAND_COLOR = Color.parseColor("#00C25F");

    //文本内容
    String mText;
    //文本文字大小
    float mTextSize = DEFAULT_TEXT_SIZE;
    //文本颜色
    int mTextColor = DEFAULT_TEXT_COLOR;
    //行高，暂时不用
    float mTextLineHeight = 0;

    //展开按钮显示的文本
    String mExpandText = "展开>>";
    //收起按钮显示的文本
    String mCollapseText = "<<收起";
    //展开/折叠文本字体大小，单位sp
    int mExpandTextSize = DEFAULT_TEXT_SIZE;
    //展开/折叠文本颜色
    int mExpandTextColor = DEFAULT_EXPAND_COLOR;

    //折叠状态下，最后一行最小空白值的百分比
    final float SPACE_MIN_PERCENT = 0f;
    //折叠状态下，最后一行最大空白值的百分比
    final float SPACE_MAX_PERCENT = 100f;
    //折叠状态下，最后一行默认空白值的百分比
    final int SPACE_DEFAULT_PERCENT = 20;
    //折叠状态下，最后一行空白占比百分比，取值范围0-100，默认20
    float mSpacePercent = SPACE_DEFAULT_PERCENT;
    //折叠状态下，最后一行空白所暂的宽度，= mSpacePercent / SPACE_MAX_PERCENT * mTvContent.getWidth()
    float mSpacePercentWidth;

    //点击文本的任何地方都可以展开、收起
    final int EXPAND_CLICK_EVENT_ON_ALL = 0;
    //只有点击展开、收起文字，才能展开和收起文字
    final int EXPAND_CLICK_EVENT_ON_EXPAND_TEXT = 1;
    //默认点击文本的任何地方都可以展开、收起
    int mExpendClickEventOn = EXPAND_CLICK_EVENT_ON_ALL;

    //用于计算文本的宽度
    Paint mPaint;

    //展开、收起文本的宽度
    float mExpandTextWidth;
    //省略文字
    String mEllipsisText = ".....";
    //省略文字宽度
    float mEllipsisTextWidth;

    public ExpandCollpaseTextView(@NonNull Context context) {
        this(context, null);
    }

    public ExpandCollpaseTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandCollpaseTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ExpandCollpaseTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 初始化
     * @param context
     * @param attrs
     */
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {

        //创建一个TextView用于显示文本
        mTvContent = new TextView(context);
        mTvContent.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        //添加TextView到容器中
        addView(mTvContent);

        mTextLineHeight = mTvContent.getLineSpacingExtra();
        if (attrs != null) {
            //从xml中读取属性值
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ExpandCollpaseTextView);

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_expand_text)) {
                //设置展开按钮上的文字
                mExpandText = a.getString(R.styleable.ExpandCollpaseTextView_expand_text);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_collapse_text)) {
                //设置收起按钮上要显示的文字
                mCollapseText = a.getString(R.styleable.ExpandCollpaseTextView_collapse_text);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_expand_text_size)) {
                //设置展开、收起文字大小
                mExpandTextSize = a.getInt(R.styleable.ExpandCollpaseTextView_expand_text_size, DEFAULT_TEXT_SIZE);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_expand_text_color)) {
                //设置展开、收起文字颜色
                mExpandTextColor = a.getColor(R.styleable.ExpandCollpaseTextView_expand_text_color, DEFAULT_EXPAND_COLOR);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_collapse_show_line_number)) {
                //设置收起时显示的行数
                mCollapseShowLineNumber = a.getInt(R.styleable.ExpandCollpaseTextView_collapse_show_line_number, COLLAPSE_SHOW_LINE_NUMBER);
                if(mCollapseShowLineNumber < COLLAPSE_SHOW_LINE_MIN_NUMBER){
                    mCollapseShowLineNumber = COLLAPSE_SHOW_LINE_MIN_NUMBER;
                }
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_content_text)) {
                //设置文本内容
                mText = a.getString(R.styleable.ExpandCollpaseTextView_content_text);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_text_line_height)) {
                //设置文本内容行高
                mTextLineHeight = a.getDimension(R.styleable.ExpandCollpaseTextView_text_line_height, mTvContent.getLineSpacingExtra());
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_content_text_size)) {
                //设置文本内容字体大小
                mTextSize = a.getInt(R.styleable.ExpandCollpaseTextView_content_text_size, DEFAULT_TEXT_SIZE);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_content_text_color)) {
                //设置文本颜色
                mTextColor = a.getColor(R.styleable.ExpandCollpaseTextView_content_text_color, DEFAULT_TEXT_COLOR);
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_expand_state)) {
                //设置当前是展开还是收起状态，默认收起状态
                mExpanded = a.getColor(R.styleable.ExpandCollpaseTextView_expand_state, COLLAPSE_STATE) == EXPAND_STATE;
            }

            if (a.hasValue(R.styleable.ExpandCollpaseTextView_collapse_line_space_percent)) {
                //设置收起时，最后一行空白所占的比例
                mSpacePercent = a.getInt(R.styleable.ExpandCollpaseTextView_collapse_line_space_percent, SPACE_DEFAULT_PERCENT);
                if (mSpacePercent < SPACE_MIN_PERCENT) {
                    mSpacePercent = SPACE_MIN_PERCENT;
                }
                if (mSpacePercent > SPACE_MAX_PERCENT) {
                    mSpacePercent = SPACE_MAX_PERCENT;
                }
            }
            if (a.hasValue(R.styleable.ExpandCollpaseTextView_expend_click_event_on)) {
                //设置点击可以展开、收起的事件源，默认点击任何地方都可以展开和收起
                mExpendClickEventOn = a.getInt(R.styleable.ExpandCollpaseTextView_expend_click_event_on, EXPAND_CLICK_EVENT_ON_ALL);
            }
            a.recycle();
        }
        //点击文本任何地方都可以展开、收起
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mExpendClickEventOn != EXPAND_CLICK_EVENT_ON_ALL) {
                    return;
                }
                troggleExpand();
            }
        });
        //设置文本内容的字体大小和颜色
        mTvContent.setTextSize(mTextSize);
        mTvContent.setTextColor(mTextColor);
        mTvContent.setLineSpacing(mTextLineHeight, mTvContent.getLineSpacingMultiplier());
        //更新文本内容
        updateContent();
    }

    public TextView getTextView() {
        return mTvContent;
    }

    public ExpandCollpaseTextView setTextLineHeight(float pLineHeight) {
        mTextLineHeight = pLineHeight;
        mTvContent.setLineSpacing(pLineHeight, mTvContent.getLineSpacingMultiplier());
        return this;
    }

    public ExpandCollpaseTextView setTextSize(float pTextSize) {
        mTvContent.setTextSize(pTextSize);
        return this;
    }

    public ExpandCollpaseTextView setTextColor(int pTextColor) {
        mTvContent.setTextColor(pTextColor);
        return this;
    }

    public ExpandCollpaseTextView setText(int pTextResId) {
        return setText(getContext().getString(pTextResId));
    }

    public ExpandCollpaseTextView setText(String pText) {
        mText = pText;
        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setExpandTextSize(int pTextSize) {
        mExpandTextSize = pTextSize;
        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setExpandTextColor(int pTextColor) {
        mExpandTextColor = pTextColor;
        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setExpandText(int pExpandTextResId) {
        return setExpandText(getContext().getString(pExpandTextResId));
    }

    public ExpandCollpaseTextView setExpandText(String pExpandText) {
        mExpandText = pExpandText;
        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setCollapseLineShowSpacePercent(float pSpacePercent) {
        if (pSpacePercent < SPACE_MIN_PERCENT) {
            pSpacePercent = SPACE_MIN_PERCENT;
        }
        if (pSpacePercent > SPACE_MAX_PERCENT) {
            pSpacePercent = SPACE_MAX_PERCENT;
        }
        mSpacePercent = pSpacePercent;
        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setCollapseShowLineNumber(int pCollapseShowLineNumber) {
        if(pCollapseShowLineNumber < COLLAPSE_SHOW_LINE_MIN_NUMBER){
            pCollapseShowLineNumber = COLLAPSE_SHOW_LINE_MIN_NUMBER;
        }
        if(pCollapseShowLineNumber == mCollapseShowLineNumber){
            return this;
        }
        mCollapseShowLineNumber = pCollapseShowLineNumber;

        updateContent();
        return this;
    }

    public ExpandCollpaseTextView setExpandClickEvenOn(int pClickEvenOn) {
        mExpendClickEventOn = pClickEvenOn;
        updateContent();
        return this;
    }

    private void updateContent() {
        int tvWidth = mTvContent.getWidth();

        if (tvWidth <= 0) {
            if (mIsOberving) {
                return;
            }
            mIsOberving = true;
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mIsOberving = false;
                    updateContent();
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            return;
        }
        int textLen = mText == null ? 0 : mText.length();
        if (textLen < 1) {
            mTvContent.setText(mText);
            return;
        }

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }
        Context context = getContext();
        mPaint.setTextSize(sp2px(context, mExpandTextSize));
        mExpandTextWidth = mPaint.measureText(mExpandText);
        mPaint.setTextSize(sp2px(context, mTextSize));
        mEllipsisTextWidth = mPaint.measureText(mEllipsisText);
        mSpacePercentWidth = (mSpacePercent / SPACE_MAX_PERCENT) * tvWidth;

        CollapseInfo collapseInfo = makeCollapseInfo();

        if (mExpanded) {
            if (collapseInfo.isOverCollapseLine) {
                mTvContent.setMaxLines(MAX_LINE);
                SpannableStringBuilder spannBuilder = new SpannableStringBuilder(mText);
                int pos = spannBuilder.length();
                int end = pos + (mCollapseText == null ? 0 : mCollapseText.length());
                spannBuilder = spannableFormat(spannBuilder, mCollapseText, pos, end, mExpandTextSize, mExpandTextColor);
                setExpandTextClickListener(spannBuilder, pos, end);
                mTvContent.setText(spannBuilder);
            } else {
                mTvContent.setText(mText);
            }

        } else {
            mTvContent.setMaxLines(mCollapseShowLineNumber);

            if (collapseInfo.isOverCollapseLine) {
                SpannableStringBuilder spannBuilder = new SpannableStringBuilder(collapseInfo.collpaseTextSb.toString());
                int pos = spannBuilder.length();
                int end = pos + (mExpandText == null ? 0 : mExpandText.length());
                spannBuilder = spannableFormat(spannBuilder, mExpandText, pos, end, mExpandTextSize, mExpandTextColor);
                if (mExpendClickEventOn == EXPAND_CLICK_EVENT_ON_EXPAND_TEXT) {
                    setExpandTextClickListener(spannBuilder, pos, end);
                }
                mTvContent.setText(spannBuilder);
            } else {
                mTvContent.setText(mText);
            }
        }
    }

    private void setExpandTextClickListener(SpannableStringBuilder spannBuilder, int pos, int end) {
        if(mExpendClickEventOn != EXPAND_CLICK_EVENT_ON_EXPAND_TEXT){
            return;
        }
        if(spannBuilder == null){
            return;
        }
        if(pos < 0){
            pos = 0;
        }
        if(end > spannBuilder.length()){
            end = spannBuilder.length();
        }
        if(pos >= end){
            return;
        }
        spannBuilder.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        troggleExpand();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        //设置点击部分的文字颜色
//                        ds.setColor(ds.linkColor);
                        ds.setUnderlineText(false);    //去除超链接的下划线
                    }
                },
                pos,
                end,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        //必须要设置下面的这个方法，否则点击会无效
        mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
        //设置高亮文字颜色
        mTvContent.setHighlightColor(getResources().getColor(android.R.color.transparent));
    }

    private void troggleExpand(){
        mExpanded = !mExpanded;
        updateContent();
    }

    private CollapseInfo makeCollapseInfo() {
        CollapseInfo collapseInfo = new CollapseInfo();
        int textLen = mText == null ? 0 : mText.length();
        if (textLen < 1) {
            return collapseInfo;
        }
        int line = 1;
        String tempText;
        float text1Width = 0;
        int tvWidth = mTvContent.getWidth();
        for (int i = 0; i < textLen; i++) {
            tempText = mText.charAt(i) + "";
            if (text1Width == 0) {
                collapseInfo.lineStartPos.add(i);
            }
            text1Width += mPaint.measureText(tempText);
            if (text1Width <= tvWidth) {
                continue;
            } else {
                collapseInfo.lineEndPos.add(i - 1);
                line++;
                text1Width = 0;

                if (line > mCollapseShowLineNumber) {
                    collapseInfo.isOverCollapseLine = true;
                    break;
                }

            }
        }
        if (collapseInfo.isOverCollapseLine) {
            int collapseLineIndex = mCollapseShowLineNumber - 2;

            if (collapseLineIndex >= collapseInfo.lineEndPos.size()) {
                collapseLineIndex = collapseInfo.lineEndPos.size() - 1;
            }

            if (collapseLineIndex < 0) {
                collapseLineIndex = 0;
            }

            int i = collapseInfo.lineEndPos.get(collapseLineIndex);
            i += 1;
            if(mCollapseShowLineNumber > 1) {
                collapseInfo.collpaseTextSb.append(mText.substring(0, i));
            }
            float enableWidth = tvWidth - mEllipsisTextWidth - mExpandTextWidth - mSpacePercentWidth;


            if (enableWidth <= 0 || i >= textLen) {

            } else {
                text1Width = 0;
                for (; i < textLen; i++) {
                    tempText = mText.charAt(i) + "";
                    text1Width += mPaint.measureText(tempText);
                    if (text1Width < enableWidth) {
                        collapseInfo.collpaseTextSb.append(tempText);
                    } else {
                        break;
                    }
                }
            }
            collapseInfo.collpaseTextSb.append(mEllipsisText);
        }
        return collapseInfo;
    }

    private boolean isOverCollapseLine() {
        int textLen = mText == null ? 0 : mText.length();
        if (textLen < 1) {
            return false;
        }
        int line = 1;
        String tempText;
        float text1Width = 0;
        int tvWidth = mTvContent.getWidth();
        for (int i = 0; i < textLen; i++) {
            tempText = mText.charAt(i) + "";
            text1Width += mPaint.measureText(tempText);
            if (text1Width <= tvWidth) {
                continue;
            } else {
                line++;
                if (line > mCollapseShowLineNumber) {
                    return true;
                }
                text1Width = 0;
            }
        }
        return false;
    }

    public static SpannableStringBuilder spannableFormat(
            String pStr,
            int pStartPos,
            int pEndPos,
            int pTextSize,
            int pTextColor) {
        return spannableFormat(null, pStr, pStartPos, pEndPos, pTextSize, pTextColor);
    }


    public static SpannableStringBuilder spannableFormat(
            SpannableStringBuilder formated,
            String pStr,
            int pStartPos,
            int pEndPos,
            int pTextSize,
            int pTextColor) {

        if (TextUtils.isEmpty(pStr)) {
            formated = new SpannableStringBuilder();
            return formated;
        }

        int pos = pStartPos;
        int end = pEndPos;

        if (formated == null) {
            formated = new SpannableStringBuilder(pStr);
        } else {
            formated.append(pStr);
        }

        int len = formated.length();

        if (pos < 0 || pos >= end || end < 0 || end > len) {
            return formated;
        }

        AbsoluteSizeSpan sizeSpan = null;
        ForegroundColorSpan colorSpan = null;

        if (pTextSize != -1) {
            sizeSpan = new AbsoluteSizeSpan(pTextSize, true);
            formated.setSpan(sizeSpan, pos, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (pTextColor != -1) {
            colorSpan = new ForegroundColorSpan(pTextColor);
            formated.setSpan(colorSpan, pos, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return formated;
    }

    private static class CollapseInfo {
        private boolean isOverCollapseLine;
        private ArrayList<Integer> lineStartPos = new ArrayList();
        private ArrayList<Integer> lineEndPos = new ArrayList();
        private StringBuilder collpaseTextSb = new StringBuilder();

    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
