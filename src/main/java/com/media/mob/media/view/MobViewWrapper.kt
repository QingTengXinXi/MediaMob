package com.media.mob.media.view

import android.content.Context
import com.media.mob.bean.SlotTactics
import com.media.mob.bean.log.MediaRequestLog

abstract class MobViewWrapper(context: Context): IMobView(context) {

  override var showReportState: Boolean = false

  override var clickReportState: Boolean = false

  override fun destroy() {
    viewShowListener = null
    viewClickListener = null
    viewCloseListener = null
  }

  fun reportMediaActionEvent(event: String, slotTactics: SlotTactics, mediaRequestLog: MediaRequestLog) {
    if (("show" == event && !showReportState) || ("click" == event && !clickReportState)) {
      if ("show" == event) {
        showReportState = true
      } else if ("click" == event) {
        clickReportState = true
      }

      val params = HashMap<String, String>()
      params["request_id"] = mediaRequestLog.requestId

      params["position_id"] = mediaRequestLog.positionId
      params["position_name"] = mediaRequestLog.positionName

      params["third_slot_type"] = "Splash"

      params["third_app_id"] = slotTactics.thirdAppId
      params["third_slot_id"] = slotTactics.thirdSlotId
      params["third_platform_name"] = slotTactics.thirdPlatformName
    }
  }
}