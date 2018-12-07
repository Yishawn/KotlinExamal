package kot.com.baselibrary.intfc

import java.io.File

/**
 *
 *
 * @auth dyx on 2018/12/4.
 */
interface ITakePhotos {
    /**
     * 拍照结果监听接口
     */
    interface TakeResultListener {
        fun takeSuccess(result: File)

        fun takeFail(result: File, msg: String)

        fun takeCancel()
    }
}