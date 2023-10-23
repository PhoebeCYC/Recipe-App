package com.example.recipeapp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.recipeapp.R
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

class GlideEngineUtils : ImageEngine {

    override fun loadImage(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context!!)
            .load(url)
            .into(imageView!!)
    }

    override fun loadImage(context: Context?, imageView: ImageView?, url: String?, maxWidth: Int, maxHeight: Int) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context!!)
            .load(url)
            .override(maxWidth, maxHeight)
            .into(imageView!!)
    }

    override fun loadAlbumCover(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (imageView != null) {
            Glide.with(context!!)
                .asBitmap()
                .load(url)
                .override(180, 180)
                .sizeMultiplier(0.5f)
                .transform(CenterCrop(), RoundedCorners(8))
                .placeholder(R.mipmap.default_photo)
                .into(imageView)
        }
    }


    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context?, url: String?, imageView: ImageView?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        if (imageView != null) {
            Glide.with(context!!)
                .load(url)
                .override(200, 200)
                .centerCrop()
                .placeholder(R.mipmap.default_photo)
                .into(imageView)
        }
    }

    override fun pauseRequests(context: Context?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context!!).pauseRequests()
    }

    override fun resumeRequests(context: Context?) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context!!).resumeRequests()
    }

    private object InstanceHolder {
        val instance: GlideEngineUtils = GlideEngineUtils()
    }

    companion object {
        fun createGlideEngine(): ImageEngine? {
            return InstanceHolder.instance
        }
    }
}