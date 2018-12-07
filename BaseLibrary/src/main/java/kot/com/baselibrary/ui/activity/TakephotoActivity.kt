package kot.com.baselibrary.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.alertview.AlertView
import com.bigkoo.alertview.OnDismissListener
import com.kotlin.base.utils.DateUtils

import com.orhanobut.logger.Logger

import kot.com.baselibrary.R
import kot.com.baselibrary.base.utils.ARouterUtils
import kot.com.baselibrary.common.BaseApplication
import kot.com.baselibrary.common.ResultCode
import kot.com.baselibrary.ext.onClick
import kot.com.baselibrary.injection.component.ActivityComponent
import kot.com.baselibrary.injection.component.DaggerActivityComponent
import kot.com.baselibrary.injection.module.ActivityModule
import kot.com.baselibrary.injection.module.LifecycleProviderModule
import kot.com.baselibrary.intfc.IPermissionsResultListener
import kot.com.baselibrary.intfc.ITakePhotos
import kot.com.baselibrary.presenter.BasePresenter
import kot.com.baselibrary.presenter.view.BaseView
import kot.com.baselibrary.toast.ToastUtils
import kot.com.baselibrary.utils.router.RouterPath
import kot.com.baselibrary.widgets.ProgressLoading
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 *
 *
 * @auth dyx on 2018/12/5.
 */
abstract class TakephotoActivity <T : BasePresenter<*>> : BaseActivity(), BaseView, ITakePhotos, OnDismissListener, ITakePhotos.TakeResultListener, View.OnClickListener, PopupWindow.OnDismissListener {

    private var imm: InputMethodManager? = null
    val REQUEST_IMAGE = 2
    private var mAlertView: AlertView? = null//避免创建重复View，先创建View，然后需要的时候show出来，推荐这个做法
    private var mAlertViewExt: AlertView? = null//窗口拓展例子
    private var etName: EditText? = null//拓展View内容
    private lateinit var mTempFile: File
    private var imageUri: Uri? = null
    private var imagename: String = ""
    private var imageurl: String = ""
    private var listener: ITakePhotos.TakeResultListener? = null
    lateinit var   bt_open: Button
    var  photoWindow: PopupWindow? =null
    var  navigationHeight:Int = 0
    @Inject
    lateinit var mPresenter: T

    lateinit var mActivityComponent: ActivityComponent

    lateinit var mLoadingDialog: ProgressLoading


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivityInjection()
        injectComponent()
        listener=this
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?;
        mLoadingDialog = ProgressLoading.create(this)
        ARouter.getInstance().inject(this)
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        navigationHeight = resources.getDimensionPixelSize(resourceId)
    }


    companion object {
        val RESULT_LOAD_IMAGE = 3//选择图片
        val RESULT_TAKE_PHOTO = 4//拍照
        val RESULT_CROP_PHOTO = 5//切割图片
    }

    override fun onError(text: String, data: Int) {

    }

    /**
     * 返回错误
     */
    override fun onError(text: String, data: Int, isShow: Boolean,redirectLogin:Boolean) {
        // 是否需要Toast提示
        if(isShow){
            ToastUtils.centerToast(this,text)
        }
        if (data== ResultCode.AUTHENTICATION_ERROR && redirectLogin){
            ARouterUtils.getInstance().startActivity(this, RouterPath.UserCenter.PATH_LOGIN)
            finish()
        }
        onError(text, data)
    }
    /*
        Dagger注册
     */
    protected abstract fun injectComponent()

    /*
        初始化Activity Component
     */
    private fun initActivityInjection() {
        mActivityComponent = DaggerActivityComponent.builder().appComponent((application as BaseApplication).appComponent)
                .activityModule(ActivityModule(this))
                .lifecycleProviderModule(LifecycleProviderModule(this))
                .build()

    }

    /*
        显示加载框，默认实现
     */
    override fun showLoading() {
        mLoadingDialog.showLoading()
    }

    /*
        隐藏加载框，默认实现
     */
    override fun hideLoading() {
        mLoadingDialog.hideLoading()
    }

    /*
        错误信息提示，默认实现
     */
    override fun onClick(view: View){
        when(view.id){
            R.id.tv_pick_phone->{

            }
            R.id.tv_pick_zone->{

            }
            R.id.tv_cancel->{
                photoWindow!!.dismiss()
            }
        }}

    /*
        弹出选择框，默认实现
        可根据实际情况，自行修改
     */
    protected fun showAlertView(view: View) {
//        AlertView(null, null, "取消", null,
//                arrayOf("拍照", "从相册中选择"),
//                this, AlertView.Style.ActionSheet, this).show()
        openPopupWindow(view)
    }



    /*
        获取图片，成功回调
     */




    /*
        获取图片，失败回调
     */


    fun hasSdcard(): Boolean {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
    }
    fun  openPopupWindow( v: View) {

        //防止重复按按钮

        if (photoWindow != null && photoWindow!!.isShowing()) {

            return

        }

        //设置PopupWindow的View

        var view = LayoutInflater.from(this).inflate(R.layout.alert_sheet, null);

        photoWindow =  PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,

                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //设置背景,这个没什么效果，不添加会报错

        photoWindow!!.setBackgroundDrawable( BitmapDrawable());

        //设置点击弹窗外隐藏自身

        photoWindow!!.setFocusable(true);

        photoWindow!!.setOutsideTouchable(true);

        //设置动画

        photoWindow!!.animationStyle=R.style.PopupWindow

        //设置位置

        photoWindow!!.showAtLocation(v, Gravity.BOTTOM, 0, 15);

        //设置消失监听

        photoWindow!!.setOnDismissListener(this);

//    设置PopupWindow的View点击事件

        setOnPopupViewClick(view);

        //设置背景色

        setBackgroundAlpha(0.5f);

    }




    fun  setOnPopupViewClick( view: View) {

        var tv_pick_phone: TextView
        var tv_pick_zone: TextView
        var  tv_cancel: TextView

        tv_pick_phone = view.findViewById<TextView>(R.id.tv_pick_phone) as TextView
        tv_pick_phone.onClick {
            //获取系统版本
            val currentapiVersion = Build.VERSION.SDK_INT
            //激活相机
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //判断存储卡是否可用 可用进行存储
            if (hasSdcard()) {
                val simpleDateFormat = SimpleDateFormat("yyyy_MM_dd_mm_ss")
                val fileName = simpleDateFormat.format(Date())
                val tempFile = File(Environment.getExternalStorageDirectory(), fileName + ".jpg")

                if (checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        ), 300, object: IPermissionsResultListener {
                            override fun onSuccessful(grantResults: IntArray) {
                                if (currentapiVersion < 24) {
                                    imageUri = Uri.fromFile(tempFile)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                                    startActivityForResult(intent,RESULT_TAKE_PHOTO)}else{
                                    val contentValues = ContentValues(1)
                                    contentValues.put(MediaStore.Images.Media.DATA, tempFile.absolutePath)
                                    //检查是否有存储权限，以免崩溃
                                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                                    startActivityForResult(intent, RESULT_TAKE_PHOTO)
                                }

                            }

                            override fun onFailure() {

                                ToastUtils.centerToast(this@TakephotoActivity,"请到设置->权限管理->相机权限，选择允许访问相机")
                            }

                        })){
                    if (currentapiVersion < 24) {
                        imageUri = Uri.fromFile(tempFile)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(intent, RESULT_TAKE_PHOTO)
                    }else{
                        val contentValues = ContentValues(1)
                        contentValues.put(MediaStore.Images.Media.DATA, tempFile.absolutePath)
                        //检查是否有存储权限，以免崩溃
                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(intent,RESULT_TAKE_PHOTO)
                    }
                }

            }
            photoWindow!!.dismiss()

        }
        tv_pick_zone =  view.findViewById<TextView>(R.id.tv_pick_zone) as TextView
        tv_pick_zone.onClick {
            if (checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 300, object: IPermissionsResultListener {
                        override fun onSuccessful(grantResults: IntArray) {
                            if (Build.VERSION.SDK_INT < 19) {
                                intent = Intent(Intent.ACTION_GET_CONTENT)
                                intent.type = "image/*"
                            } else {
                                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            }

                            startActivityForResult(intent, REQUEST_IMAGE)
                        }
                        override fun onFailure() {
                            ToastUtils.centerToast(this@TakephotoActivity,"请到设置->权限管理->存储权限，选择允许访问存储")

                        }

                    })) {if (Build.VERSION.SDK_INT < 19) {
                intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
            } else {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }

                startActivityForResult(intent, REQUEST_IMAGE)}
            photoWindow!!.dismiss()
        }
        tv_cancel =  view.findViewById<TextView>(R.id.tv_cancel) as TextView
        tv_cancel.onClick{
            photoWindow!!.dismiss()
        }


    }




//设置屏幕背景透明效果

    fun  setBackgroundAlpha( alpha:Float) {

        var lp = window.attributes

        lp.alpha = alpha

        window.attributes = lp

    }





    @SuppressLint("NewApi")
    override fun  onDismiss() {
        setBackgroundAlpha(1f);

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.i("resultCode", resultCode.toString())

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE -> {

                    val bitmap = getSmallBitmap(getRealPathFromURI(data!!.data), 480, 800)
                    val file = saveFile(bitmap, DateUtils.convertTimeToString(System.currentTimeMillis(), DateUtils.FORMAT_SHORT) + ".jpg")
                    listener!!.takeSuccess(file)
//                    mPresenter.upImage(OkHttpUtil.createTextRequestBody("2"), OkHttpUtil.createPartWithAllImageFormats("file", file))
                }
                RESULT_TAKE_PHOTO -> {
                    Logger.e("TAG", "RESULT_TAKE_PHOTO")
                    val imageUri = imageUri
                    val intent = Intent("com.android.camera.action.CROP")
                    intent.setDataAndType(imageUri, "image/*")
                    intent.putExtra("scale", true)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    //启动裁剪程序
                    startActivityForResult(intent, RESULT_CROP_PHOTO)
                }
                RESULT_CROP_PHOTO -> {
                    val imageUri = imageUri
                    Logger.i("ssuir", imageUri.toString())
                    var bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri))
                    val bitmapname=DateUtils.convertTimeToString(System.currentTimeMillis(), DateUtils.FORMAT_SHORT).replace(" ","")+".jpg"
                    bitmap=compressImage(bitmap,bitmapname )
                    val file=saveFile(bitmap, bitmapname)
                    listener!!.takeSuccess(file)

                }

                else -> {
                    Logger.e("TAG", "123")
                }

            }
        }



    }

    fun compressImage(image: Bitmap, srcPath: String): Bitmap {

        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        while (baos.toByteArray().size / 1024 > 100) {    // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset()// 重置baos即清空baos
            options -= 10// 每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos)// 这里压缩options%，把压缩后的数据存放到baos中

        }
        val isBm = ByteArrayInputStream(baos.toByteArray())// 把压缩后的数据baos存放到ByteArrayInputStream中
        val bitmap = BitmapFactory.decodeStream(isBm, null, null)// 把ByteArrayInputStream数据生成图片
        try {
            val out = FileOutputStream(srcPath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bitmap
    }

    @Throws(IOException::class)
    fun saveFile(bm: Bitmap, fileName: String): File {
        val path = getSDPath() + "/retisyun/"
        val dirFile = File(path)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        val myCaptureFile = File(path + fileName)
        val bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        bos.flush()
        bos.close()
        return myCaptureFile
    }

    fun getSDPath(): String {
        var sdDir: File? = null
        val sdCardExist = Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory()//获取跟目录
        }
        return sdDir!!.toString()
    }

    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     *
     * @param filePath
     * @return
     */
    fun getSmallBitmap(filePath: String, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true  //只返回图片的大小信息
        BitmapFactory.decodeFile(filePath, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    private fun getRealPathFromURI(contentURI: Uri): String {
        val result: String
        val cursor = contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }
    /*
        新建临时文件
     */
    fun createTempFile(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 111)
            //权限还没有授予，需要在这里写申请权限的代码
        }else {
            //权限已经被授予，在这里直接写要执行的相应方法即可
            val tempFileName = "${DateUtils.curTime}.png"
            Logger.i("tempFileName",tempFileName)
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                this.mTempFile = File(Environment.getExternalStorageDirectory(),tempFileName)
                return
            }

            this.mTempFile = File(filesDir,tempFileName)
        }


    }

}