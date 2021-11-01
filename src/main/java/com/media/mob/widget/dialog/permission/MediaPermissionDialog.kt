package com.media.mob.widget.dialog.permission

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.media.mob.R
import com.media.mob.platform.youLiangHui.helper.bean.Permission

class MediaPermissionDialog(context: Context, private val permissions: ArrayList<Permission>) :
    Dialog(context, R.style.mob_custom_dialog) {

    private var containerView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.mob_dialog_permission)

        val layoutParams = window?.attributes

        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT

        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.horizontalMargin = 0F

        window?.attributes = layoutParams

        window?.decorView?.setPadding(0, 0, 0, 0)

        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)

        setCanceledOnTouchOutside(true)

        containerView = findViewById<RelativeLayout>(R.id.rv_permission_container)

        refreshContainerView()
    }

    private fun refreshContainerView() {
        if (permissions.isEmpty()) {
            dismiss()
            return
        }

        val close: ImageView? = containerView?.findViewById(R.id.iv_permission_close)
        close?.setOnClickListener {
            dismiss()
        }

        val result: RecyclerView? = containerView?.findViewById(R.id.rv_permission_result)
        result?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        result?.adapter = MediaPermissionAdapter(permissions)
    }
}