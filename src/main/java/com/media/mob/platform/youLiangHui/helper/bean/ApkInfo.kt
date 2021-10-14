package com.media.mob.platform.youLiangHui.helper.bean

import com.google.gson.annotations.SerializedName

class ApkInfo(
    /**
     * 应用名称
     */
    @SerializedName("appName")
    var appName: String,
    /**
     * 包名
     */
    @SerializedName("pkgName")
    var packageName: String,
    /**
     * 图标
     */
    @SerializedName("iconUrl")
    var icon: String,
    /**
     * 描述
     */
    @SerializedName("description")
    var description: String,
    /**
     * 开发者
     */
    @SerializedName("authorName")
    var authorName: String,
    /**
     * 版本号
     */
    @SerializedName("versionName")
    var versionName: String?,
    /**
     * 版本名
     */
    @SerializedName("versionCode")
    var versionCode: Int,
    /**
     * 应用下载地址
     */
    @SerializedName("apkUrl")
    var downloadUrl: String?,
    /**
     * 应用下载次数
     */
    @SerializedName("appDownCount")
    var downloadCount: Long,
    /**
     * 文件大小
     */
    @SerializedName("fileSize")
    var fileSize: Long,
    /**
     * 标签
     */
    @SerializedName("tag")
    var label: String,
    /**
     * 版本更新时间
     */
    @SerializedName("apkPublishTime")
    var publishTime: Long,
    /**
     * 隐私协议地址
     */
    @SerializedName("privacyAgreement")
    var agreement: String,
    /**
     * 权限集合
     */
    @SerializedName("permissions")
    var permissions: ArrayList<String>
) {

    /**
     * 接口返回事件不统一，有可能是秒也有可能是毫秒
     */
    fun loadPublishTime(): Long {
        return if (publishTime > 946688401000L) publishTime else publishTime * 1000
    }

    override fun toString(): String {
        return "ApkInfo(appName='$appName', packageName='$packageName', icon='$icon', description='$description', authorName='$authorName', versionName=$versionName, versionCode=$versionCode, downloadUrl=$downloadUrl, downloadCount=$downloadCount, fileSize=$fileSize, label='$label', publishTime=$publishTime, agreement='$agreement', permissions=$permissions)"
    }
}