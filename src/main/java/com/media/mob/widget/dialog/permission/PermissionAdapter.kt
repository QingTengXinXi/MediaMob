package com.media.mob.widget.dialog.permission

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.media.mob.R.layout
import com.media.mob.platform.youLiangHui.helper.bean.Permission

class PermissionAdapter(private val permissions: ArrayList<Permission>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PermissionHolder(LayoutInflater.from(parent.context).inflate(layout.mob_item_permission, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val permission = permissions[position]

        if (viewHolder is PermissionHolder) {
            viewHolder.bind(permission)
        }
    }

    override fun getItemCount(): Int {
        return permissions.size
    }
}