package kot.com.baselibrary.utils

import android.net.ParseException
import android.text.TextUtils
import java.math.BigDecimal
import java.util.regex.Pattern



/**
 * Created by yixiao on 2018/3/8.
 */
object  StringUtil {

    /**
    *校验邮箱是否正确
     */
    fun isEmail(email: String): Boolean {
        val str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$"
        val p = Pattern.compile(str)
        val m = p.matcher(email)
        return m.matches()
    }

    /**
     * 检测是否为空，或者不存在
     *
     * @param str
     * @return
     */
    fun isNullOrEmpty(str: String?): Boolean {
        return str == null || str == ""
    }
    /**
     * 检验手机号码是否合法
     *
     * @param phoneNum 手机号码
     * @return
     */
    fun checkPhoneNum(phoneNum: String): Boolean {
        // 现改为1开头，11位数！！！！！！！！！！！！！
        val telRegex = "[1]\\d{10}"
        return if (TextUtils.isEmpty(phoneNum))
            false
        else
            phoneNum.matches(telRegex.toRegex())
    }

    /**
     * 手机号中间四位显示*号
     *
     * @param phone
     * @return
     */
    fun HidePhoneNum(phone: String): String? {
        if (!TextUtils.isEmpty(phone) && phone.length > 6) {
            val sb = StringBuilder()
            for (i in 0 until phone.length) {
                val c = phone[i]
                if (i >= 3 && i <= 6) {
                    sb.append('*')
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        } else
            return phone
    }

    /**
     * 身份证号中间显示*号
     *
     * @param idcard
     * @return
     */
    fun HideIDCardNum(idcard: String?): String? {
        if (idcard != null && idcard.length > 15) {
            val sb = StringBuilder()
            for (i in 0 until idcard.length) {
                val c = idcard[i]
                if (i >= 4 && i <= idcard.length - 5) {
                    sb.append('*')
                } else {
                    sb.append(c)
                }
            }
            return sb.toString()
        } else
            return idcard
    }

    /**
     * double 相减
     * @param d1
     * @param d2
     * @return
     */
    fun sub(d1: Double, d2: Double): Double {
        val bd1 = BigDecimal(java.lang.Double.toString(d1))
        val bd2 = BigDecimal(java.lang.Double.toString(d2))
        return bd1.subtract(bd2).toDouble()
    }

    fun HideCardBankShowFirst(cardnumber: String, isHide: Boolean): String {

        if (cardnumber.length > 4) {
            var str = cardnumber
            if (isHide) {
                val sb = StringBuilder()
                for (i in 0 until cardnumber.length) {
                    val c = cardnumber[i]
                    if (cardnumber.length>=19){
                        if (i>cardnumber.length - 16&&i<= cardnumber.length - 5)
                        {
                            sb.append('*')
                        }
                        else {
                            sb.append(c)
                        }
                    }
                    else{

                        if (i>cardnumber.length - 13&&i<= cardnumber.length - 5)
                        {
                            sb.append('*')
                        }
                        else {
                            sb.append(c)
                        }
                    }

                }
                str = sb.toString()
            }
            var reslut = StringBuilder(str.replace(" ", ""))

            val i = reslut.length / 4
            val j = reslut.length % 4

            return reslut.toString()
        } else {
            return cardnumber
        }
    }
    /**
     * 显示银行卡前面显示*号，后四位显示数字，并且四位一空格
     *
     * @param cardnumber
     * @return 是否隐藏
     */
    fun HideCardBank(cardnumber: String, isHide: Boolean): String {

        if (cardnumber.length > 4) {
            var str = cardnumber
            if (isHide) {
                val sb = StringBuilder()
                for (i in 0 until cardnumber.length) {
                    val c = cardnumber[i]
                    if (i <= cardnumber.length - 9 ) {
                        sb.append(' ')
                    } else if (i>cardnumber.length - 9&&i<= cardnumber.length - 5)
                    {
                        sb.append('*')
                    }
                    else {
                        sb.append(c)
                    }
                }
                str = sb.toString()
            }
            var reslut = StringBuilder(str.toString().replace(" ", ""))

            val i = reslut.length / 4
            val j = reslut.length % 4
            return reslut.toString()
        } else {
            return cardnumber
        }
    }

    /**
     * 功能：身份证的有效验证
     *
     * @param IDStr 身份证号
     * @return 有效：返回"" 无效：返回String信息
     * @throws ParseException
     */
    @Throws(ParseException::class)
    fun IDCardValidate(IDStr: String): String {
        var errorInfo = ""// 记录错误信息
        if (IDStr.length != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。"
            return errorInfo
        }
        return ""
    }
}