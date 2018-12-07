package  kot.com.baselibrary.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import kot.com.baselibrary.R


/**
 * Created by yixiao on 2018/5/17.
 */
class AutoNewLineLayout : ViewGroup {
    /**
     * 两个子控件之间的横向间隙
     */
    protected var horizontalSpace = 0f

    /**
     * 两个子控件之间的垂直间隙
     */
    protected var vertivalSpace = 0f
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.AutoNewLineLayout)
        horizontalSpace = ta.getDimension(R.styleable.AutoNewLineLayout_horizontalSpace, 0f)
        vertivalSpace = ta.getDimension(R.styleable.AutoNewLineLayout_vertivalSpace, 0f)
        ta.recycle()
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //AT_MOST
        var width = 0
        var height = 0
        var rawWidth = 0//当前行总宽度
        var rawHeight = 0// 当前行高
        var rowIndex = 0//当前行位置
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility === View.GONE) {
                if (i == count - 1) {
                    //最后一个child
                    height += rawHeight
                    width = Math.max(width, rawWidth)
                }
                continue
            }
            //这里调用measureChildWithMargins 而不是measureChild
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val lp = child.layoutParams as ViewGroup.MarginLayoutParams

            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            if (rawWidth.toFloat() + childWidth.toFloat() + (if (rowIndex > 0) horizontalSpace else 0f) > widthSpecSize - paddingLeft - paddingRight) {
                //换行
                width = Math.max(width, rawWidth)
                rawWidth = childWidth
                height += (rawHeight + vertivalSpace).toInt()
                rawHeight = childHeight
                rowIndex = 0
            } else {
                rawWidth += childWidth
                if (rowIndex > 0) {
                    rawWidth += horizontalSpace.toInt()
                }
                rawHeight = Math.max(rawHeight, childHeight)
            }

            if (i == count - 1) {
                width = Math.max(rawWidth, width)
                height += rawHeight
            }

            rowIndex++
        }

        setMeasuredDimension(
                if (widthSpecMode == View.MeasureSpec.EXACTLY) widthSpecSize else width + paddingLeft + paddingRight,
                if (heightSpecMode == View.MeasureSpec.EXACTLY) heightSpecSize else height + paddingTop + paddingBottom
        )
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val viewWidth = r - l
        var leftOffset = paddingLeft
        var topOffset = paddingTop
        var rowMaxHeight = 0
        var rowIndex = 0//当前行位置
        var childView: View
        var w = 0
        val count = childCount
        while (w < count) {
            childView = getChildAt(w)
            if (childView.getVisibility() === View.GONE) {
                w++
                continue
            }
            val lp = childView.getLayoutParams() as ViewGroup.MarginLayoutParams
            // 如果加上当前子View的宽度后超过了ViewGroup的宽度，就换行
            val occupyWidth = lp.leftMargin + childView.getMeasuredWidth() + lp.rightMargin
            if (leftOffset + occupyWidth + paddingRight > viewWidth) {
                leftOffset = paddingLeft  // 回到最左边
                topOffset += (rowMaxHeight + vertivalSpace).toInt()  // 换行
                rowMaxHeight = 0

                rowIndex = 0
            }
            val left = leftOffset + lp.leftMargin
            val top = topOffset + lp.topMargin
            val right = leftOffset + lp.leftMargin + childView.getMeasuredWidth()
            val bottom = topOffset + lp.topMargin + childView.getMeasuredHeight()
            childView.layout(left, top, right, bottom)
            // 横向偏移
            leftOffset += occupyWidth
            // 试图更新本行最高View的高度
            val occupyHeight = lp.topMargin + childView.getMeasuredHeight() + lp.bottomMargin
            if (rowIndex != count - 1) {
                leftOffset += horizontalSpace.toInt()
            }
            rowMaxHeight = Math.max(rowMaxHeight, occupyHeight)
            rowIndex++
            w++
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.MarginLayoutParams(p)
    }


}