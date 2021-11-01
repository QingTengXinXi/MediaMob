<center><font size=6><b>MediaMob</b></font></center>

### 说明
MediaMob 是为 Android 开发者提供的国内主流广告SDK聚合的开源项目。旨在帮助 Android 开发者 方便、快捷 的接入国内主流的广告SDK。

### 版本概览

聚合广告SDK目前已经聚合了[百青藤](https://union.baidu.com/bqt/#/)、 [优量汇](https://e.qq.com/dev/index.html)、 [穿山甲](https://www.csjplatform.com/)、 [京准通](https://jan.jd.com/#/login)以及 [快手联盟](https://u.kuaishou.com/)。聚合广告SDK各个版本对应第三方广告平台版本如下：

| 聚合SDK版本 | 百青藤广告SDK版本 | 优量汇广告SDK版本 | 穿山甲广告SDK版本 | 京准通广告SDK版本 | 快手联盟广告SDK版本 |
| :-------------: | :-------------------: | :-------------------: | :-------------------: | :-------------------: | :-------------------: |
|      1.0.0      |         9.16          |       4.422.1292      |        4.0.1.1        |         1.2.8         |        3.3.17         |


### 版本变更记录

**1.0.0**

- 接入百青藤、优量汇、穿山甲、京准通、快手联盟五家主流的广告联盟
- 聚合广告SDK整体架构设计，支持五家联盟的开屏、激励视频、插屏等广告样式


### 支持的广告类型

聚合广告SDK目前支持以下广告类型，您可以根据开发需要选择合适的广告。





### **全局配置**

### 聚合广告SDK支持最低系统版本：Android SDK Level 16+

```groovy
minSdkVersion 16
```











### **错误码**

|  错误码  |              说明              |             排查方向             |
| :-----: | :--------------------------- | :----------------------------- |
|  81001  | 广告位策略配置执行完成，无广告平台填充 | 请检查广告位策略配置，适当增加广告位流量策略配置 |
|  82002  | 百青藤广告SDK未填充广告 | 百青藤广告SDK未填充广告，具体原因需查询百青藤SDK错误码: [错误码](https://union.baidu.com/miniappblog/2020/12/01/newAndroidSDK/) |
|  82003  | 百青藤广告SDK物料和缓存失败 | 百青藤广告SDK缓存广告物料失败，具体原因需查询百青藤SDK错误码: [错误码](https://union.baidu.com/miniappblog/2020/12/01/newAndroidSDK/) |
|  83000  | 穿山甲广告SDK未初始化完成就请求广告 | 检查聚合广告SDK初始化时机，确保聚合SDK初始话成功后再请求广告 |
|  83002  | 穿山甲广告SDK未填充广告 | 穿山甲广告SDK未填充广告，具体原因需查询穿山甲SDK错误码: [错误码](https://www.csjplatform.com/support/doc/5de4cc6d78c8690012a90aa5) |
|  83003  | 穿山甲广告SDK请求广告超时 | 穿山甲广告SDK请求广告超时，具体原因需查询穿山甲SDK错误码: [错误码](https://www.csjplatform.com/support/doc/5de4cc6d78c8690012a90aa5) |
|  83004  | 穿山甲广告SDK请求成功回调返回空对象 | 穿山甲广告SDK请求成功回调返回空对象或空列表，具体原因需查询穿山甲SDK错误码: [错误码](https://www.csjplatform.com/support/doc/5de4cc6d78c8690012a90aa5) |
|  83005  | 穿山甲广告SDK渲染广告失败 | 穿山甲广告SDK渲染广告失败，具体原因需查询穿山甲SDK错误码: [错误码](https://www.csjplatform.com/support/doc/5de4cc6d78c8690012a90aa5) |
|  84000  | 优量汇广告SDK未初始化完成就请求广告 | 检查聚合广告SDK初始化时机，确保聚合SDK初始话成功后再请求广告 |
|  84002  | 优量汇广告SDK未填充广告 | 优量汇广告SDK未填充广告，具体原因需查询优量汇SDK错误码: [错误码](https://developers.adnet.qq.com/doc/android/union/union_debug#sdk%20%E9%94%99%E8%AF%AF%E7%A0%81) |
|  84003  | 优量汇广告SDK渲染广告失败 | 优量汇广告SDK渲染广告失败，具体原因需查询优量汇SDK错误码: [错误码](https://developers.adnet.qq.com/doc/android/union/union_debug#sdk%20%E9%94%99%E8%AF%AF%E7%A0%81) |
|  85001  | 京准通广告SDK暂不支持的广告类型 | 检查广告配置信息是否包含京准通不支持的广告类型，京准通广告SDK支持的广告类型请查询京准通接入文档: [接入文档](https://help-sdk-doc.jd.com/ansdkDoc/jie-ru-wen-dang.html) |
|  85002  | 京准通广告SDK未填充广告 | 京准通广告SDK未填充广告，具体原因需查询京准通SDK错误码: [错误码](https://help-sdk-doc.jd.com/ansdkDoc/access_docs/Android/%E9%94%99%E8%AF%AF%E7%A0%81/%E9%94%99%E8%AF%AF%E7%A0%81.html) |
|  85003  | 京准通广告SDK请求成功回调返回空对象 | 京准通广告SDK请求成功回调返回空对象或空列表，具体原因需查询京准通SDK错误码: [错误码](https://help-sdk-doc.jd.com/ansdkDoc/access_docs/Android/%E9%94%99%E8%AF%AF%E7%A0%81/%E9%94%99%E8%AF%AF%E7%A0%81.html) |
|  85003  | 京准通广告SDK渲染广告失败 | 京准通广告SDK渲染广告失败，具体原因需查询京准通SDK错误码: [错误码](https://help-sdk-doc.jd.com/ansdkDoc/access_docs/Android/%E9%94%99%E8%AF%AF%E7%A0%81/%E9%94%99%E8%AF%AF%E7%A0%81.html) |
|  86000  | 快手联盟广告SDK未初始化完成就请求广告 | 检查聚合广告SDK初始化时机，确保聚合SDK初始话成功后再请求广告 |
|  86002  | 快手联盟广告SDK未填充广告 | 快手联盟广告SDK未填充广告，具体原因需查询快手联盟SDK错误码 |
|  86003  | 快手联盟广告SDK请求成功回调返回空对象 | 快手联盟广告SDK请求成功回调返回空对象或空列表，具体原因需查询快手联盟SDK错误码 |
|  86004  | 快手联盟广告SDK展示广告异常 | 快手联盟广告SDK展示广告异常，具体原因需排查广告代码。 |
