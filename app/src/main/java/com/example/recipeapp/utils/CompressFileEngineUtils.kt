package com.example.recipeapp.utils

import com.luck.picture.lib.engine.CompressFileEngine
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File

class CompressFileEngineUtils {
    fun getCompressEngine(): CompressFileEngine {
        return CompressFileEngine { context, source, call ->
            Luban.with(context).load(source).ignoreBy(0)
                .setCompressListener(object : OnNewCompressListener {
                    override fun onStart() {}
                    override fun onSuccess(source: String, compressFile: File) {
                        call?.onCallback(source, compressFile.absolutePath)
                    }

                    override fun onError(source: String, e: Throwable) {
                        call?.onCallback(source, null)
                    }
                }).launch()
        }
    }
}