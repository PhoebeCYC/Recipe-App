package com.example.recipeapp.popUp

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.example.recipeapp.R

class BottomPopUp(context: Context, itemClick: View.OnClickListener?) : PopupWindow() {

    private var mContext: Context = context
    private var mView: View? = null
    private var ll_popup: LinearLayout? = null
    private var layoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    private var popup_Camera: TextView? = null
    private var popup_Album: TextView? = null

    init {
        mView = layoutInflater.inflate(R.layout.bottom_pop_up, null)
        ll_popup = mView!!.findViewById(R.id.ll_popup)
        popup_Camera = mView!!.findViewById(R.id.popup_Camera)
        popup_Album = mView!!.findViewById(R.id.popup_Album)

        popup_Camera?.setOnClickListener(itemClick)
        popup_Album?.setOnClickListener(itemClick)

        mView?.setOnTouchListener { _, motionEvent ->
            val contentTop = ll_popup?.top ?: 0
            val contentBottom = ll_popup?.bottom ?: 0
            val contentLeft = ll_popup?.left ?: 0
            val contentRight = ll_popup?.right ?: 0
            val x = motionEvent.x
            val y = motionEvent.y

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (y < contentTop || y > contentBottom || x < contentLeft || x > contentRight) {
                    dismiss()
                }
            }
            true
        }

        this.contentView = mView
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        this.isFocusable = true
        val dw = ColorDrawable(-0x50000000)
        this.setBackgroundDrawable(dw)
    }
}