package com.media.mob.widget.dialog.permission

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.media.mob.R
import com.media.mob.platform.youLiangHui.helper.bean.Permission

class PermissionHolder(view: View): RecyclerView.ViewHolder(view) {

  fun bind(permission: Permission) = with(itemView) {

    val explain: TextView? = itemView.findViewById(R.id.tv_permission_explain)
    explain?.text = permission.explain

    val desc: TextView? = itemView.findViewById(R.id.tv_permission_desc)
    desc?.text = permission.desc
  }
}