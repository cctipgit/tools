package com.hash.coinconvert.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.billy.android.swipe.SmartSwipeWrapper;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.SlidingConsumer;
import com.billy.android.swipe.listener.SimpleSwipeListener;
import com.duxl.baselib.utils.ArithmeticUtils;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.NullUtils;
import com.duxl.baselib.utils.SPUtils;
import com.duxl.baselib.widget.SimpleTextWatcher;
import com.hash.coinconvert.Constants;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.LayoutConvertItemViewBinding;
import com.hash.coinconvert.entity.CurrencyInfo;
import com.hash.coinconvert.livedatabus.LiveDataKey;
import com.hash.coinconvert.livedatabus.event.CalculatorChangedEvent;
import com.hash.coinconvert.utils.AmountUtils;
import com.hash.coinconvert.utils.AnimUtils;
import com.hash.coinconvert.utils.Calculator;
import com.hash.coinconvert.utils.FlagLoader;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页换算Item
 */
public class ConvertItemView extends FrameLayout implements DefaultLifecycleObserver {

    private static final String TAG = "ConvertItemView";
    private StringBuilder mInputKeyContents = new StringBuilder(); // 输入的键盘内容

    private LayoutConvertItemViewBinding mBinding;
    private OnClickListener mOnClickListener;
    private String mAfterCountChange;
    private LifecycleOwner mLifecycleOwner;

    private CurrencyInfo mCurrencyInfo;
    private CalculatorChangedEvent mCalculatorEvent;
    private int mDefaultCount = Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE; // 未输入时默认数量
    private int mDecimicalCryptocurrency = Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY; // 加密货币小数位
    private int mDecimicalLegalTender = Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER; // 法定货币小数位
    private int mQuoteColor = Constants.SP.DEFAULT.QUOTE_COLOR; // 0红涨绿跌、1绿涨红跌

    private int mDefaultHeight;
    private int mSelectHeight;
    private boolean mIsPause;
    private int mDP15;
    private boolean mTextTooLong; // 文本是否太长
    private int mLlPriceWidth;

    public ConvertItemView(Context context) {
        this(context, null);
    }

    public ConvertItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConvertItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDP15 = DisplayUtil.dip2px(context, 15);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ConvertItemView);
        if (typedArray != null) {
            typedArray.recycle();
        }

        View view = LayoutInflater.from(context).inflate(R.layout.layout_convert_item_view, this, false);
        mBinding = LayoutConvertItemViewBinding.bind(view);
        addView(view);

        mBinding.etCount.setShowSoftInputOnFocus(false);
        mBinding.etFormula.setShowSoftInputOnFocus(false);
        mBinding.vCover.setOnClickListener(v -> {
            //mBinding.etFormula.setSelection(mBinding.etFormula.getText().length());
            //mBinding.etFormula.requestFocus();
            if (mOnClickListener != null) {
                mOnClickListener.onClick(ConvertItemView.this);
            }
        });

        SlidingConsumer swipeConsumer = new SlidingConsumer();
        swipeConsumer.setMaxSettleDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        swipeConsumer.setRelativeMoveFactor(SlidingConsumer.FACTOR_FOLLOW);
        //swipeConsumer.setInterpolator(new AnticipateOvershootInterpolator());
        swipeConsumer.setInterpolator(new LinearInterpolator());
        swipeConsumer.addListener(new SimpleSwipeListener() {
            @Override
            public void onSwipeOpened(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
                super.onSwipeOpened(wrapper, consumer, direction);
                swipeConsumer.setMaxSettleDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
                consumer.setInterpolator(new AnticipateOvershootInterpolator());
                if (direction == SwipeConsumer.DIRECTION_LEFT) {
                    if (mOnSlideListener != null) {
                        mOnSlideListener.onSlide(-1, ConvertItemView.this);
                    }
                } else {
                    if (mOnSlideListener != null) {
                        mOnSlideListener.onSlide(1, ConvertItemView.this);
                    }
                }
            }

            @Override
            public void onSwipeClosed(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction) {
                super.onSwipeClosed(wrapper, consumer, direction);
                swipeConsumer.setMaxSettleDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
                consumer.setInterpolator(new LinearInterpolator());
            }
        });
        mBinding.swipeWrapper.addConsumer(swipeConsumer);

        mBinding.etCount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                computeViewWidth();
                if (mOnInputCountChangedListener != null && isSelected()) {
                    if (!s.toString().equals(mAfterCountChange)) {
                        mAfterCountChange = s.toString();
                        doInputCountChanged();

                    }
                }
            }
        });
        mBinding.etFormula.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                computeViewWidth();
            }
        });

        setSelected(isSelected());
        showCurrencyInfo();
    }

    public int getLayoutHeight() {
        return getLayoutParams().height;
    }

    public void setLayoutHeight(int height) {
        getLayoutParams().height = height;
        requestLayout();
    }

    /**
     * 设置item的高度
     *
     * @param defaultHeight 默认高度
     * @param increase      选中后增加的高度
     */
    public void setSelectHeight(int defaultHeight, int increase) {
        this.mDefaultHeight = defaultHeight;
        this.mSelectHeight = mDefaultHeight + increase;
    }

    /**
     * 根据输入的内容计算宽度，内容超长时左边的货币名称隐藏和淡出效果
     */
    private void computeViewWidth() {
        // 输入数量的文本宽度
        float countWidth = mBinding.etCount.getPaint().measureText(mBinding.etCount.getText().toString());
        // 表达式文本的宽度
        float formulaWidth = mBinding.etFormula.getPaint().measureText(mBinding.etFormula.getText().toString());
        // 文本可用宽度
        if (mLlPriceWidth <= 0) {
            mLlPriceWidth = mBinding.llA.getMeasuredWidth() - mBinding.tvToken.getMeasuredWidth() - mBinding.ivLocation.getMeasuredWidth();
        }
        float maxWidth = Math.max(countWidth, formulaWidth);
        Log.i(TAG, "computeViewWidth: llPriceWidth=" + mLlPriceWidth + ", widthCount=" + countWidth + ", widthFormula=" + formulaWidth);
        mTextTooLong = maxWidth + mDP15 > mLlPriceWidth;
        showLocationIcon();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.tvToken.getLayoutParams();
        if (maxWidth + mDP15 / 2 > mBinding.llA.getMeasuredWidth() - mBinding.tvToken.getMeasuredWidth()) {
            int margin = (int) (mBinding.llA.getMeasuredWidth() - mBinding.tvToken.getMeasuredWidth() - maxWidth) - mDP15 / 2;
            if (margin < -mBinding.tvToken.getMeasuredWidth()) {
                margin = -mBinding.tvToken.getMeasuredWidth();
            }
            layoutParams.leftMargin = margin;
        } else {
            layoutParams.leftMargin = 0;
        }
        mBinding.tvToken.setLayoutParams(layoutParams);

    }

    /**
     * 输入的内容改变处理
     */
    private void doInputCountChanged() {
        if (EmptyUtils.isEmpty(mAfterCountChange)) {
            mBinding.etCount.setHint(String.valueOf(mDefaultCount));
        }
        if (mOnInputCountChangedListener != null) {
            mOnInputCountChangedListener.onInputCountCountChanged(mAfterCountChange);
        }
        if (mCurrencyInfo == null) {
            return;
        }
        // 是否输入了金额
        boolean isHintAmount = EmptyUtils.isEmpty(mAfterCountChange);
        // 数量改变，将事件分发出去
        String amount = isHintAmount ? String.valueOf(mDefaultCount) : mAfterCountChange;
        // 当前货币数量对应的美元价格
        BigDecimal dollarAmount = ArithmeticUtils.newBigDecimal(amount).multiply(ArithmeticUtils.newBigDecimal(mCurrencyInfo.price));

        CalculatorChangedEvent event = new CalculatorChangedEvent(ConvertItemView.this, mCurrencyInfo.token, amount, isHintAmount, dollarAmount);
        LiveEventBus.get(LiveDataKey.CALCULATOR_CHANGED).post(event);
    }

    public void setLifecycleOwner(LifecycleOwner owner) {
        this.mLifecycleOwner = owner;
        mLifecycleOwner.getLifecycle().addObserver(this);
        // 监听货币数量输入改变事件
        LiveEventBus
                .get(LiveDataKey.CALCULATOR_CHANGED, CalculatorChangedEvent.class)
                .observe(mLifecycleOwner, event -> {
                    if (event == null || event.object == ConvertItemView.this) {
                        return;
                    }
                    mCalculatorEvent = event;
                    computerPrice();

                });
    }

    // 根据汇率和其它货币的数量实时折算价格
    private void computerPrice() {
        if (mCalculatorEvent == null || mCalculatorEvent.object == ConvertItemView.this || mCurrencyInfo == null) {
            return;
        }
        mInputKeyContents.delete(0, mInputKeyContents.length());

        int decimal = mDecimicalLegalTender; // 保留的小数位
        if (EmptyUtils.isNotNull(mCurrencyInfo) && "digital".equalsIgnoreCase(mCurrencyInfo.currencyType)) {
            decimal = mDecimicalCryptocurrency;
        }
        // 计算折算后的价格
        BigDecimal amount = AmountUtils.divide(mCalculatorEvent.dollarAmount, ArithmeticUtils.newBigDecimal(mCurrencyInfo.price), decimal);
        // 格式化后显示到UI
        if (mCalculatorEvent.isHintAmount) {
            mBinding.etCount.getText().clear();
            mBinding.etCount.setHint(AmountUtils.formatAmount(amount, decimal, 3, true));
        } else {
            mBinding.etCount.setCustomText(AmountUtils.formatAmount(amount, decimal, 3, true));
            mBinding.etCount.setSelection(mBinding.etCount.length());
            mInputKeyContents.delete(0, mInputKeyContents.length());
            mInputKeyContents.append(mBinding.etCount.getText().toString());
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        mIsPause = false;
        // 未输入时默认数量
        mDefaultCount = SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE, Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE);
        // // 加密货币小数位
        mDecimicalCryptocurrency = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY, Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY);
        // 法定货币小数位
        mDecimicalLegalTender = SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER, Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER);
        // 0红涨绿跌、1绿涨红跌
        mQuoteColor = SPUtils.getInstance().getInt(Constants.SP.KEY.QUOTE_COLOR, Constants.SP.DEFAULT.QUOTE_COLOR);

        if (isSelected() && mCalculatorEvent == null) {
            mBinding.etCount.setHint(String.valueOf(mDefaultCount));
            doInputCountChanged();
        }

        showCurrencyInfo();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        mIsPause = true;
    }

    // 显示货币信息
    private void showCurrencyInfo() {
        if (mBinding.swipeWrapper == null || mCurrencyInfo == null) {
            return;
        }

        FlagLoader.load(mBinding.ivIcon, 0, mCurrencyInfo.icon);
        mBinding.tvToken.setText(NullUtils.format(mCurrencyInfo.token));
        if (SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_CURRENCY_SYMBOL, Constants.SP.DEFAULT.DEFAULT_CURRENCY_SYMBOL)) {
            mBinding.tvName.setText(NullUtils.format(mCurrencyInfo.name) + " " + NullUtils.format(mCurrencyInfo.unitName));
        } else {
            mBinding.tvName.setText(NullUtils.format(mCurrencyInfo.name));
        }
        showLocationIcon();

        /*Rect rect = new Rect();
        mBinding.tvToken.getPaint().getTextBounds(mCurrencyInfo.token, 0, mCurrencyInfo.token.length(), rect);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBinding.tvToken.getLayoutParams();
        layoutParams.width = rect.width() + mDP15 / 2;
        mBinding.tvToken.setLayoutParams(layoutParams);*/

        // 临时解决切换货币的时候，token显示不完整问题
        ViewGroup.LayoutParams layoutParams = mBinding.flRootItem.getLayoutParams();
        layoutParams.width = DisplayUtil.getScreenWidth(getContext());
        mBinding.flRootItem.setLayoutParams(layoutParams);
    }

    private void showLocationIcon() {
        String countryCode = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE);
        if (SPUtils.getInstance().getBoolean(Constants.SP.KEY.DEFAULT_LOCAL_CURRENCY, Constants.SP.DEFAULT.DEFAULT_LOCAL_CURRENCY)
                && TextUtils.equals(countryCode, mCurrencyInfo.countryCode)
                && !mTextTooLong) {
            mBinding.ivLocation.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivLocation.setVisibility(View.GONE);
        }
    }

    public void closeSwipe() {
        if (EmptyUtils.isNotEmpty(mBinding.swipeWrapper.getAllConsumers())) {
            for (SwipeConsumer consumer : mBinding.swipeWrapper.getAllConsumers()) {
                consumer.smoothClose();
            }
        }
    }

    /**
     * 只清除输入按键，不做显示逻辑处理
     */
    public void clearInputKey() {
        if (mInputKeyContents.length() > 0) {
            mInputKeyContents.delete(0, mInputKeyContents.length());
        }
    }

    /**
     * 键盘非删除按键点击调用
     *
     * @param text
     */
    public void appendKey(String text) {
        Log.i(TAG, "appendKey: " + text + ", 已输入内容：" + mInputKeyContents.toString());
        if (verifyAppendKey(text)) {
            showInputContent();
        } else {
            if ("0".equals(mInputKeyContents.toString())) {
                AnimUtils.startAnimation(mBinding.etCount, R.anim.shake);
                return;
            }
            List<String> allNum = getAllNum();
            if (!allNum.isEmpty()) {
                String lastNum = allNum.get(allNum.size() - 1);
                if ("0".equals(lastNum) && "0".equals(text)) {
                    return;
                }
            }

            // 输入内容不合法，抖动动画提示
            if (hasOperator(mInputKeyContents.toString())) {
                AnimUtils.startAnimation(mBinding.etFormula, R.anim.shake);
            } else {
                AnimUtils.startAnimation(mBinding.etCount, R.anim.shake);
            }

        }
    }

    /**
     * 验证输入内容是否合法
     * 输入规则：
     * 1、数字最多只能有1个小数点
     * 2、计算公式末尾是运算符时，再输入运算符表示修改运算符
     * 3、表达式第一个位是操作符号时，前面添加0
     *
     * @param text
     * @return
     */
    private boolean verifyAppendKey(String text) {
        // 分输入的内容是数字、小数点、操作符三种情况进行处理

        // 已输入内容为空时:
        // 如果输入的是小数点，显示0.
        // 如果输入的是操作符，在操作符前面加0
        // 其他直接加上输入的字符
        if (mInputKeyContents.length() == 0) {
            if (".".equals(text)) {
                mInputKeyContents.append("0.");
                return true;
            }
            if (isOperator(text.charAt(0))) {
                mInputKeyContents.append("0");
                mInputKeyContents.append(text);
                return true;
            }
            mInputKeyContents.append(text);
            return true;
        }

        // 已输入的是0，将要输入的也是0，不做任何修改
        if (mInputKeyContents.length() == 1 && "0".equals(mInputKeyContents.toString()) && "0".equals(text)) {
            return false;
        }

        // 已输入的内容不为空：
        char lastChar = mInputKeyContents.charAt(mInputKeyContents.length() - 1);
        char inputChar = text.charAt(0);

        // 已输入的末尾是操作
        if (isOperator(lastChar)) {
            // 将要输入的也是操作符的情况
            if (isOperator(inputChar)) {
                // 修改末尾操作符
                delKey();
                mInputKeyContents.append(text);
                return true;
            }
            // 将要输入的是小数点
            if (".".equals(text)) {
                mInputKeyContents.append("0.");
                return true;
            }
            // 将要输入的是数字
            mInputKeyContents.append(text);
            return true;
        }

        // 已输入的末尾是数字
        if (!isOperator(lastChar) && '.' != lastChar) {
            // 将要输入的是操作符
            if (isOperator(inputChar)) {
                mInputKeyContents.append(text);
                return true;
            }
            List<String> allNum = getAllNum();
            // 将要输入的是小数点
            if (".".equals(text)) {
                if (allNum.get(allNum.size() - 1).indexOf(".") == -1) {
                    mInputKeyContents.append(text);
                    return true;
                } else {
                    return false;
                }
            }
            // 将要输入的是数字
            String lastNum = allNum.get(allNum.size() - 1);
            if ("0".equals(lastNum) && "0".equals(text)) {
                return false;
            }
            // 输入0和非0数字的情况（比如01），直接改成非0数字（比如1）
            if ("0".equals(mInputKeyContents.toString()) && !"0".equals(text)) {
                mInputKeyContents.delete(0, mInputKeyContents.length());
            }

            // 控制输入整数位最大13位，小数点位数最大8位
            int lastNumLength = lastNum.length();
            if (lastNum.indexOf(".") > 0) {
                lastNumLength = lastNum.substring(lastNum.indexOf(".") + 1).length();
                if (lastNumLength == 8) {
                    return false;
                }
            } else if (lastNumLength == 13) {
                return false;
            }

            mInputKeyContents.append(text);
            return true;
        }

        // 已输入的末尾是小数点
        if ('.' == lastChar) {
            // 将要输入的是操作符
            if (isOperator(inputChar)) {
                mInputKeyContents.append(text);
                return true;
            }
            // 将要输入的是小数点
            if (".".equals(text)) {
                return false;
            }
            // 将要输入的是数字
            mInputKeyContents.append(text);
            return true;
        }

        return true;
    }

    /**
     * 键盘删除按键点击调用
     */
    public void delKey() {
        if (mInputKeyContents.length() > 0) {
            char lastChar = mInputKeyContents.charAt(mInputKeyContents.length() - 1);
            if (isOperator(lastChar)) { // 是运算符
                // 先删除末尾的运算符
                mInputKeyContents.deleteCharAt(mInputKeyContents.length() - 1);
                // 再判断表达式是否已经括号起来了
                lastChar = mInputKeyContents.charAt(mInputKeyContents.length() - 1);
                if (lastChar == ')') {
                    // 删除表达式中最外层的括号
                    mInputKeyContents.deleteCharAt(mInputKeyContents.length() - 1);
                    mInputKeyContents.delete(0, 1);
                }
            } else {
                // 不是运算符，直接删除末尾输入
                mInputKeyContents.deleteCharAt(mInputKeyContents.length() - 1);
            }
            showInputContent();
        }
    }

    /**
     * 长按删除
     */
    public void longDelKey() {
        if (mInputKeyContents.length() > 0) {
            mInputKeyContents.delete(0, mInputKeyContents.length());
            showInputContent();
        }
    }


    public void longTimesKey() {
        longOperatorKey("*");
    }

    /**
     * 长按除号按键
     */
    public void longDivKey() {
        longOperatorKey("/");
    }

    /**
     * 长按加减乘除运算符按键
     * 1、如果表达式最后是运算符，将运算符之前的表达式括起来，并修改最后的运算符
     * 2、如果表达式最后不是运算符，直接将表达式括起来，并在最后添加运算符
     * 3、如果表达式已经括起来了，不再进行处理
     *
     * @param operator 运算符：+、-、*、/
     */
    private void longOperatorKey(String operator) {
        String text = mInputKeyContents.toString();
        if (EmptyUtils.isEmpty(text)) {
            return;
        }

        char lastChar = text.charAt(text.length() - 1);
        if (isOperator(lastChar)) {
            char beforeLastChar = text.charAt(text.length() - 2);
            if (')' == beforeLastChar) {
                // 表达式已经括起来了，不再处理
                return;
            }
            mInputKeyContents.delete(0, mInputKeyContents.length());
            String newText = text.substring(0, text.length() - 1);
            mInputKeyContents.append("(");
            mInputKeyContents.append(newText);
            mInputKeyContents.append(")");
            mInputKeyContents.append(operator);
            showInputContent();
            return;
        }

        mInputKeyContents.delete(0, mInputKeyContents.length());
        mInputKeyContents.append("(");
        mInputKeyContents.append(text);
        mInputKeyContents.append(")");
        mInputKeyContents.append(operator);
        showInputContent();
    }

    private void showInputContent() {
        // 判断输入的内容是否有操作符
        boolean hasOperator = hasOperator(mInputKeyContents.toString());
        if (hasOperator) {
            String formula = mInputKeyContents.toString();
            mBinding.etFormula.setVisibility(View.VISIBLE);
            mBinding.etFormula.setText(formatFormula(formula));
            mBinding.etFormula.setSelection(mBinding.etFormula.length());
            mBinding.etFormula.requestFocus();

            List<String> allNum = getAllNum();
            if (allNum.size() > 1) {
                if (isOperator(formula.charAt(formula.length() - 1))) {
                    // 去掉表达式末尾的操作符
                    formula = formula.substring(0, formula.length() - 1);
                }
                // 计算表达式的值
                int decimal = mDecimicalLegalTender; // 保留的小数位
                if (EmptyUtils.isNotNull(mCurrencyInfo) && "digital".equalsIgnoreCase(mCurrencyInfo.currencyType)) {
                    decimal = mDecimicalCryptocurrency;
                }
                BigDecimal result = Calculator.evaluate(formula, decimal, RoundingMode.DOWN);
                mBinding.etCount.setCustomText(formatCount(result.toPlainString(), true));
                mBinding.etCount.setSelection(mBinding.etCount.length());
                mBinding.etCount.requestFocus();
                mBinding.etFormula.requestFocus();
            } else {
                mBinding.etCount.setCustomText(formatCount(allNum.get(0), false));
                mBinding.etCount.requestFocus();
                mBinding.etFormula.requestFocus();
            }


        } else {
            mBinding.etFormula.getText().clear();
            mBinding.etFormula.setVisibility(View.INVISIBLE);
            String result = formatCount(mInputKeyContents.toString(), false);
            if (mInputKeyContents.toString().endsWith(".")) {
                // formatCount后会去除用户输入的小数点，这里手动添加下
                result += ".";
            }
            mBinding.etCount.setCustomText(result);
            mBinding.etCount.setSelection(mBinding.etCount.length());
            mBinding.etCount.requestFocus();
        }
    }

    private String formatFormula(String input) {
        return input.replace("*", "×").replace("/", "÷");
    }

    /**
     * 表达式计算需要保留小数位后再显示（例如1/3=0.333333333333333）
     * 当前item表达式计算需要去除小数点末尾的0
     * 当前item输入非表达式原样分组显示
     * 其它item触发当前计算的结果需要保留小数位末尾的0
     *
     * @param count
     * @param isCalculator 是否表达式计算
     * @return
     */
    private String formatCount(String count, boolean isCalculator) {
        if (EmptyUtils.isEmpty(count)) {
            return count;
        }

        int decimal = mDecimicalLegalTender; // 保留的小数位
        if (EmptyUtils.isNotNull(mCurrencyInfo) && "digital".equalsIgnoreCase(mCurrencyInfo.currencyType)) {
            decimal = mDecimicalCryptocurrency;
        }
        if (isCalculator) {
            return AmountUtils.formatAmount(count, decimal, 3, false);
        } else if (isSelected()) {
            return AmountUtils.formatAmount(count, -1, 3, true);
        }
        return AmountUtils.formatAmount(count, decimal, 3, true);
    }

    // 获取输入的所有数字 （2+3)*5
    public List<String> getAllNum() {
        String str = mInputKeyContents.toString().replaceAll("\\(", "").replaceAll("\\)", "");
        List<String> result = new ArrayList();

        boolean hasOperator = false;
        int beginIndex = 0;
        if (EmptyUtils.isNotEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                char charI = str.charAt(i);
                if (isOperator(charI)) {
                    result.add(str.substring(beginIndex, i));
                    beginIndex = i + 1;
                    hasOperator = true;
                } else if (i == str.length() - 1) {
                    result.add(str.substring(beginIndex));
                }
            }
            if (!hasOperator) {
                result.add(str);
            }
        }
        return result;
    }

    /**
     * 判断字符是否是运算符
     *
     * @param c
     * @return
     */
    public boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * 判断字符串中是否包含操作符
     *
     * @param str
     * @return
     */
    public boolean hasOperator(String str) {
        if (EmptyUtils.isNotEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (isOperator(str.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        //super.setOnClickListener(l);
        mOnClickListener = l;
    }

    @Override
    public void setSelected(boolean selected) {
        boolean beforeSelected = isSelected();
        super.setSelected(selected);
        mBinding.flRootItem.setBackgroundColor(selected ? getContext().getResources().getColor(R.color.convert_item_select_bg) : Color.TRANSPARENT);
        if (beforeSelected != selected) {
            if (selected) {
                // 判断是否输入了表达式
                boolean hasOperator = hasOperator(mInputKeyContents.toString());
                if (hasOperator) {
                    mBinding.etFormula.setVisibility(View.VISIBLE);
                    mBinding.etFormula.requestFocus();
                    mBinding.etFormula.setSelection(mBinding.etFormula.length());
                } else {
                    mBinding.etCount.requestFocus();
                    mBinding.etCount.setSelection(mBinding.etCount.length());
                }

                if (mInputKeyContents.length() == 0) {
                    mBinding.etCount.setHint(String.valueOf(mDefaultCount));
                    mBinding.etCount.getText().clear();
                    doInputCountChanged();
                }
            } else {
                mBinding.etFormula.setVisibility(View.INVISIBLE);
            }
            ObjectAnimator animator = ObjectAnimator.ofInt(this, "layoutHeight", isSelected() ? mDefaultHeight : mSelectHeight, isSelected() ? mSelectHeight : mDefaultHeight);
            animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            animator.start();
        }
    }

    public interface OnInputCountChangedListener {
        /**
         * 输入的内容改变/表达式计算的结果改变
         *
         * @param count
         */
        void onInputCountCountChanged(String count);
    }

    private OnInputCountChangedListener mOnInputCountChangedListener;

    public void setOnInputCountChangedListener(OnInputCountChangedListener listener) {
        this.mOnInputCountChangedListener = listener;
    }

    public interface OnSlideListener {
        /**
         * 滑动事件
         *
         * @param orientation -1左滑动，1右滑动
         */
        void onSlide(int orientation, ConvertItemView convertItemView);
    }

    private OnSlideListener mOnSlideListener;

    public void setOnSlideListener(OnSlideListener listener) {
        this.mOnSlideListener = listener;
    }

    /**
     * 设置货币信息
     *
     * @param currency
     */
    public void setCurrencyInfo(CurrencyInfo currency) {
        boolean isInitCurrency = this.mCurrencyInfo == null || !this.mCurrencyInfo.token.equals(currency.token);
        if (isInitCurrency && isSelected()) {
            // 初始化或切换货币后，需要将用户输入数量清空
            mInputKeyContents.delete(0, mInputKeyContents.length());
            mBinding.etCount.getText().clear();
            mBinding.etFormula.getText().clear();
        }
        int quote = 0; // -1跌、0持平、1涨
        /*// 涨跌根据汇率浮动计算
        if (EmptyUtils.isNotNull(mCurrencyInfo) && EmptyUtils.isNotNull(currency)) {
            if (TextUtils.equals(mCurrencyInfo.token, currency.token)) {
                // 计算涨跌
                quote = ArithmeticUtils.newBigDecimal(currency.price).compareTo(ArithmeticUtils.newBigDecimal(mCurrencyInfo.price));
            }
        }*/

        String beforePrice = mBinding.etCount.getText().length() > 0 ? mBinding.etCount.getText().toString() : mBinding.etCount.getHint().toString();
        if (mCurrencyInfo != null) {
            Log.i(TAG, "汇率价格变化--------------0--" + mCurrencyInfo.token + ", bPrice=" + (mCurrencyInfo == null ? "" : mCurrencyInfo.price) + ", cPrice=" + currency.price);
        }

        this.mCurrencyInfo = currency;
        // 显示货币
        showCurrencyInfo();
        Log.i(TAG, "汇率价格变化--------------1--" + mCurrencyInfo.token + ", isSelected=" + isSelected());
        if (isSelected()) {
            //当前选中的货币不需要计算汇率涨跌
            mBinding.vRateQuote.setBackgroundResource(0);
            mBinding.vRateQuote.clearAnimation();
            if (isInitCurrency) {
                doInputCountChanged();
            }
            return;
        }
        Log.i(TAG, "汇率价格变化--------------2--" + mCurrencyInfo.token + ", isInitCurrency=" + isInitCurrency + ", mInputKeyContents.length=" + mInputKeyContents.length());
        /*if (!isInitCurrency && mInputKeyContents.length() == 0) {
            // 用户没有输入价格时，不计算实时价格
            return;
        }*/

        // 重新根据汇率计算价格
        computerPrice();
        if (mIsPause) {
            // 当前页面不可见，不需要显示涨跌动画
            return;
        }
        String currentPrice = mBinding.etCount.getText().length() > 0 ? mBinding.etCount.getText().toString() : mBinding.etCount.getHint().toString();
        Log.i(TAG, "汇率价格变化--------------3--" + mCurrencyInfo.token + ", beforePrice=" + beforePrice + ", currentPrice=" + currentPrice);
        // 涨跌根据折算后的价格是否变化计算（因为小数位的原因可能涨跌后并不能体现出价格变化，这种情况就不需要动画）
        if (!TextUtils.equals(beforePrice, currentPrice)) {
            // 计算涨跌
            quote = ArithmeticUtils.newBigDecimal(currentPrice).compareTo(ArithmeticUtils.newBigDecimal(beforePrice));
        }
        Log.i(TAG, "汇率价格变化--------------4--" + mCurrencyInfo.token + ", quote=" + quote);
        if (quote == 0) {
            mBinding.vRateQuote.setBackgroundResource(0);
            mBinding.vRateQuote.clearAnimation();
        } else {
            int bg = 0;
            if (quote == 1) { // 涨
                // 0红涨绿跌、1绿涨红跌
                bg = mQuoteColor == 0 ? R.drawable.rate_quote_red : R.drawable.rate_quote_green;
            } else if (quote == -1) { // 跌
                bg = mQuoteColor == 0 ? R.drawable.rate_quote_green : R.drawable.rate_quote_red;
            }
            if (bg != 0) {
                mBinding.vRateQuote.setBackgroundResource(bg);
                mBinding.vRateQuote.setVisibility(View.VISIBLE);
                Log.i(TAG, "汇率价格变化，启动动画：" + mCurrencyInfo.token + ", " + quote);
                AnimUtils.startAnimation(mBinding.vRateQuote, R.anim.rate_quote, new SimpleAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mBinding.vRateQuote.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    public CurrencyInfo getCurrencyInfo() {
        return mCurrencyInfo;
    }
}
