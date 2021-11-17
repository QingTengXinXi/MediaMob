package com.media.mob.bean.request

import android.app.Activity
import com.media.mob.bean.TacticsConfig
import com.media.mob.bean.TacticsInfo
import com.media.mob.bean.log.MediaPlatformLog
import com.media.mob.bean.log.MediaRequestLog

class MediaRequestParams<T>(
    val activity: Activity,
    val slotParams: SlotParams,
    val tacticsInfo: TacticsInfo,
    val tacticsConfig: TacticsConfig,
    val mediaRequestLog: MediaRequestLog,
    val mediaPlatformLog: MediaPlatformLog,
    var mediaRequestResult: (MediaRequestResult<T>) -> Unit
)