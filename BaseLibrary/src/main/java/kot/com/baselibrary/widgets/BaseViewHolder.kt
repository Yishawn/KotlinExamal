package kot.com.baselibrary.widgets

import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.util.SparseArray
import android.view.View


/**
 * Created by yixiao on 2018/5/11.
 */
class BaseViewHolder: RecyclerView.ViewHolder {

    private var views: SparseArray<View>? = null
    private lateinit var convertView: View

    constructor(itemView: View):super(itemView){
        this.views = SparseArray<View>()
        convertView = itemView
    }


    //根据id检索获得该View对象
    private fun <T : View> retrieveView(viewId: Int): View? {
        var view: View? = views!!.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            views!!.put(viewId, view)
        }
        return view
    }

    fun setText(viewId: Int, charSequence: CharSequence): BaseViewHolder {
        val textView = retrieveView<View>(viewId) as TextView
        textView.setText(charSequence)
        return this
    }

    fun getView(viewId: Int): View? {
        return retrieveView<View>(viewId)
    }

}