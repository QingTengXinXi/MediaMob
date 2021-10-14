package com.media.mob.platform.youLiangHui.helper

import android.content.Intent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.media.mob.Constants
import com.media.mob.activity.MobConfirmActivity
import com.media.mob.helper.logger.MobLogger
import com.media.mob.network.NetworkHelper
import com.media.mob.platform.youLiangHui.helper.bean.Permission
import com.qq.e.comm.compliance.DownloadConfirmCallBack
import com.qq.e.comm.compliance.DownloadConfirmListener
import com.media.mob.platform.youLiangHui.helper.bean.ApkInfo
import com.media.mob.platform.youLiangHui.helper.bean.ApkResult

object DownloadConfirmHelper {

    private val classTarget = DownloadConfirmHelper::class.java.simpleName

    private var permissionExplain = HashMap<String, Permission>()

    var downloadConfirmCallBack: DownloadConfirmCallBack? = null

    val downloadConfirmListener = DownloadConfirmListener { context, scenes, infoUrl, confirmCallback ->
        MobLogger.e(classTarget, "应用下载发生的场景:$scenes, 应用信息获取地址:$infoUrl")

        this.downloadConfirmCallBack = confirmCallback

        val intent = Intent()
        intent.putExtra(MobConfirmActivity.CONFIRM_APP_INFO_URL, "$infoUrl&resType=api")
        intent.setClass(context, MobConfirmActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * 请求下载应用信息并解析
     */
    fun requestDownloadApkInfo(
        infoUrl: String?,
        callback: ((ApkInfo?) -> Unit)
    ) {
        if (infoUrl.isNullOrEmpty()) {
            callback.invoke(null)
            return
        }

        MobLogger.e(classTarget, "请求应用信息地址: $infoUrl")

        NetworkHelper.request(infoUrl) { result ->
            if (result?.isNotEmpty() == true) {
                try {
                    val apkResult = Gson().fromJson<ApkResult<ApkInfo>>(
                        result,
                        object : TypeToken<ApkResult<ApkInfo>>() {}.type
                    )

                    if (apkResult.checkResultAvailable()) {
                        callback.invoke(apkResult.data)
                        return@request
                    }
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
            callback.invoke(null)
        }
    }

    /**
     * 解析应用权限列表
     */
    fun analysisPermissionList(permissions: ArrayList<String>): ArrayList<Permission> {
        if (permissionExplain.isEmpty()) {
            initialPermissionInterpret()
        }

        val explains = ArrayList<Permission>()

        permissions.forEach {
            if (permissionExplain.containsKey(it)) {
                permissionExplain[it]?.let { permission ->
                    explains.add(permission)
                }
            }
        }

        MobLogger.e(classTarget, "解析应用权限列表: ${permissions.size} : ${explains.size}")

        return explains
    }

    /**
     * 解析本地存储的权限释义文件
     */
    private fun initialPermissionInterpret() {
        try {
            val result = Constants.application.assets.open("permission.json").bufferedReader().use { it.readText() }

            if (result.isNotEmpty()) {
                val permissionList = Gson().fromJson<ArrayList<Permission>>(
                    result,
                    object : TypeToken<ArrayList<Permission>>() {}.type
                )

                if (!permissionList.isNullOrEmpty()) {
                    permissionList.forEach {
                        permissionExplain[it.name] = it
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}