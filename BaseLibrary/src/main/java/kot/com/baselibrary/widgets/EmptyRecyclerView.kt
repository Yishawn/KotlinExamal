package kot.com.baselibrary.widgets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.orhanobut.logger.Logger

/**
 * Created by yixiao on 2018/5/14.
 */
class EmptyRecyclerView: RecyclerView {
    private var emptyView: View? = null
    private val TAG = "EmptyRecyclerView"

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            Logger.i(TAG, "onItemRangeInserted" + itemCount)
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }


    constructor(context: Context, attrs: AttributeSet):super(context,attrs){}
     fun checkIfEmpty() {
        if (emptyView != null && adapter != null) {
            val emptyViewVisible = adapter.itemCount == 0
            emptyView!!.setVisibility(if (emptyViewVisible) View.VISIBLE else View.GONE)
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    //设置没有内容时，提示用户的空布局
    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
        checkIfEmpty()
    }


}