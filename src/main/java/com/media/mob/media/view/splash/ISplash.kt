package com.media.mob.media.view.splash

import android.view.ViewGroup
import com.media.mob.media.IMob

interface ISplash : IMob {

    /**
     * 展示开屏广告
     */
    fun show(viewGroup: ViewGroup)
}