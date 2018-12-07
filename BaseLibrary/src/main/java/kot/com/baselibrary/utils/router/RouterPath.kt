package kot.com.baselibrary.utils.router

/**
 * Created by yixiao on 2018/3/12.
 */
object RouterPath {
    //用户模块
    class UserCenter{
        companion object {
            const val PATH_FRAGMENT = "/userCenter/fragment"
            const val PATH_LOGIN = "/userCenter/login"
            const val PATH_APPROVE = "/userCenter/approve"
            const val PATH_ARGEEMENT ="/userCenter/argeement"
            const val PATH_RISKWARN ="/userCenter/riskwarn"
            const val PATH_RISKDISCLAIMER ="/userCenter/riskdisclaimer"
            const val PATH_RISKHINT="/userCenter/riskhint"
            const val PATH_RISKRESULTS="/userCenter/riskresults"
            const val PATH_MESWSAGE="/userCenter/MessageCenter"
            const val PATH_REGISTER ="/Login/Register"
            const val PATH_FORGETPWD ="/Login/ForgetPwd"
            const val PATH_HELP_CENTER ="/Login/HelpCenter"
            const val PATH_PWD_MANAGEMENT ="/userCenter/PwdManagement"
            const val PATH_INVITE_FRIENDS ="/userCenter/InviteFriends"
            const val PATH_USER_INFO ="/userCenter/UserInfo"
            const val PATH_ABOUT_US ="/userCenter/AboutActivity"
            const val PATH_RISK_MESSAGE ="/userCenter/RiskMessageActivity"
            const val PATH_TEST_SETTING ="/userCenter/TestSetting"
            const val PATH_RISK_SUBJECT ="/userCenter/RiskSubjectActivity"
        }
    }
    //主Model
    class App{
        companion object {
            const val PATH_FRAGMENT = "/App/fragment"
            const val PATH_Main = "/App/main"
            const val PATH_NEWSCONTENT = "/App/newscontent"
            const val PATH_GUIDE ="/App/splash"
        }
    }
    //财富模块
    class Treasure{
        companion object {
            const val PATH_FRAGMENT = "/Treasure/fragment"
            const val PATH_RECHARGESUCCES = "/Treasure/rechargesuccess"
            const val PATH_FIRSTRECHARGE = "/Treasure/firstrecharge"
            const val PATH_RECHARGE = "/Treasure/recharge"
            const val PATH_EERRORLIST = "/Treasure/errorlist"
            const val PATH_HINT = "/Treasure/rechargehint"
            const val PATH_REVIEWIMAGE="/Treasure/ReviewImage"
            const val PATH_MYPRO="/Treasure/Myproduct"
            const val PATH_WITHDRAW ="/Treasure/Withdraw"
            const val PATH_WITHDRAW_SUCCESS="/Treasure/WithdrawSuccess"
            const val PATH_NOT_FIRST_ACTIVITY="/Treasure/NotFirstActivity"
        }
    }
    //产品模块
    class Product{
        companion object {
            const val PATH_FRAGMENT = "/Product/fragment"
            const val PATH_PRODUCTDETAILS="/Product/productdetails"
            const val PATH_PRODUCTPARTICULARS="/Product/particulars"
            const val PATH_SUBSCRIBE="/Product/subscribe"
            const val PATH_CONTRACT ="/Product/contract"
            const val PATH_UNINVESTED="/Product/uninvested"
            //合同列表
            const val PATH_CONTRACT_LIST ="/Product/contractList"
            //预约状态页面
            const val PATH_ORDER_STATUS ="/Product/orderstatus"
            const val PATH_DEALSUCCESS ="/Product/DealSuccess"
            const val PATH_AGREEMENT_DETAILS ="/Product/AgreementDetails"
            const val PATH_FIRST_SUBSCRIBE="/Product/FirstSubscribe"
            const val PATH_NOT_FIRST_SUBSCRIBE="/Product/NotFirstSubscribe"
            const val PATH_REMINDER_ACTIVITY="/Product/ReminderActivity"
        }
    }

}