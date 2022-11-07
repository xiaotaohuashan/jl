package com.jl.core.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.StringRes;

import com.jl.core.log.LogUtils;
import com.jl.myapplication.config.Configs;

import java.math.BigDecimal;


/**
 * 字符串拼接
 * Created by lenovo on 2017/9/4.
 */

public class StringUtil {
    private static Context mContext;

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * 从带货币符号的金额中获取货币符号
     * @param str
     * @return
     */
    public static String getSymbolFromTotlaMoney(String str) {
        int strFirstIntIndex = getStrFirstIntIndex(str);
        return str.substring(0, strFirstIntIndex);
    }

    /**
     * 从带有符号的金额中获取金额数目
     * @param str
     * @return
     */
    public static double getAmountFromTotlaMoney(String str) {
        if(TextUtils.isEmpty(str)) {
            return 0;
        }else {
            try {
                int strFirstIntIndex = getStrFirstIntIndex(str);
                String substring = str.substring(strFirstIntIndex, str.length());
                return Double.parseDouble(substring);
            }catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    /**
     * 获取价格除以100后的值（"totalPrice":3075,"fixedPrice":"¥30.75",）
     * @param price
     * @return
     */
    public static double divide100(Double price) {
        return new BigDecimal("" + price).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 除法
     * @param num1
     * @param num2
     * @return
     */
    public static double divide(Double num1, Double num2) {
        return new BigDecimal(String.valueOf(num1)).divide(BigDecimal.valueOf(num2),2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 保留两位小数
     * @param price
     * @return
     */
    public static double double2(Double price) {
        return new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取字符串中第一个数字的索引
     */
    public static int getStrFirstIntIndex(String str) {
        int index = -1;
        try {
            int length = str.length();
            for (int i = 0;i < length;i++) {
                char c = str.charAt(i);
                if(c >= '0' && c <= '9') {
                    index = i;
                    break;
                }
            }
            return index;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 截取的字符串是否等于equals
     *
     * @param data
     * @param begin
     * @param end
     * @param equals
     * @return
     */
    public static boolean isSubstringEquals(String data, int begin, int end, String equals) {
        String subString = substring(data, begin, end);
        return subString.equals(equals);
    }

    /**
     * 截取字符串
     *
     * @param data
     * @param begin
     * @param end
     * @return
     */
    public static String substring(String data, int begin, int end) {
        try {
            return data.substring(begin, end);
        } catch (Exception e) {
            LogUtils.i("StringUtil", "substring", "字符串截取异常");
        }
        return data;
    }

    /**
     * 截取字符串
     *
     * @param data
     * @param begin
     * @return
     */
    public static String substring(String data, int begin) {
        try {
            return data.substring(begin);
        } catch (Exception e) {
            LogUtils.i("StringUtil", "substring", "字符串截取异常");
        }
        return data;
    }

    /**
     * 字符串拼接
     *
     * @param format
     * @param data
     * @return
     */
    public static String stringFormat(String format, Object... data) {
        return String.format(format, data);
    }

    /**
     * 字符串拼接
     *
     * @param resId
     * @param data
     * @return
     */
    public static String stringFormat(@StringRes int resId, Object... data) {
        String model = getString(resId);
        return String.format(model, data);
    }

    /**
     * 获取字符串
     *
     * @param id
     * @return
     */
    private static String getString(@StringRes int id) {
        if (mContext == null) {
            return "";
        }
        return mContext.getString(id);
    }

    /**
     * 手机号用****号隐藏中间数字
     *
     * @param phone
     * @return
     */
    public static String hidePhone(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            return phone;
        }
        String newPhone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        return newPhone;
    }

    public static String hidePhoneNum(String phone){
        String newPhone=phone;
        if (TextUtils.isEmpty(phone) || phone.length()<7 || phone.length()>11){
            return phone;
        }
        switch (phone.length()){
            case 7:{
                newPhone = phone.replaceAll("(\\d{2})\\d{3}(\\d{2})", "$1***$2");
                break;
            }
            case 8:{
                newPhone = phone.replaceAll("(\\d{3})\\d{3}(\\d{2})", "$1***$2");
                break;
            }
            case 9:{
                newPhone = phone.replaceAll("(\\d{3})\\d{3}(\\d{3})", "$1***$2");
                break;
            }
            case 10:{
                newPhone = phone.replaceAll("(\\d{3})\\d{3}(\\d{4})", "$1***$2");
                break;
            }
            case 11:{
                newPhone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                break;
            }
        }
        return newPhone;
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * @param
     * @time
     * @describe 验证是否8到16位
     */
    public static boolean checkPassLenth(String text, int minLenth) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (text.length() < minLenth) {
            return false;
        }
        return true;
    }

    /**
     * 校验手机号格式
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return phoneNumber.matches(Configs.PHONE_NUMBER_REGEX);
    }

    /**
     * 校验验证码格式  6位
     *
     * @param verifyCode
     * @return
     */
    public static boolean checkVerifyCode(String verifyCode) {
        if (TextUtils.isEmpty(verifyCode)) {
            return false;
        }
        String trim = verifyCode.trim();
        return TextUtils.isDigitsOnly(trim) && trim.length() == 6;
    }

    /**
     * 校验密码格式  8-16位字母数字组合
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        if (password.length() > 16 || password.length() < 8) {
            return false;
        }
        if (password.matches(Configs.PASSWORD_REGEX)) {
            return !TextUtils.isDigitsOnly(password) && !password.matches(Configs.CHARACTOR_REGEX);
        }
        return false;
    }

}
