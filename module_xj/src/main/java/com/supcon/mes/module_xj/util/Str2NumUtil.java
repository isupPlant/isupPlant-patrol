package com.supcon.mes.module_xj.util;

import com.supcon.mes.mbap.view.CustomEditText;

import java.util.regex.Pattern;

/**
 * ClassName
 * Created by zhangwenshuai1 on 2019/5/27
 * Email zhangwenshuai1@supcon.com
 * Desc
 */
public class Str2NumUtil {
    /*
     * 是否为浮点数？double或float类型。
     * @param str 传入的字符串。
     * @return 是浮点数返回true,否则返回false。
     */
    public static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断“.”开始的赋值为“0.”
     * @param charSequence
     * @param customEditText
     * @return
     */
    public static boolean eqPointStart(CharSequence charSequence, CustomEditText customEditText) {
        if (".".equals(charSequence.toString())) {
            customEditText.setContent("0.");
            customEditText.editText().setSelection(customEditText.editText().length());  // 光标置末尾
            return true;
        }
        return false;
    }


}
