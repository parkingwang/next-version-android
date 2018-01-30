# NextVersion

NextVersion是一个用于Android App检查自动更新的Library。

----

## 使用说明

#### 1. 添加Nexus依赖：

```gradle
repositories {
    maven { url 'https://dl.bintray.com/parkingwang/maven' }
}
```

#### 2. 引入NextVersion

NextVersion 项目分为多个子模块。包括核心模块和UI扩展模块：

> VERSION= 1.0.1

```gradle
compile 'com.parkingwang:version:{VERSION}'
compile 'com.parkingwang:version-theme-wave:{VERSION}'
compile 'com.parkingwang:version-theme-rocket:{VERSION}'
compile 'com.parkingwang:version-theme-check:{VERSION}'
compile 'com.parkingwang:version-source-fir:{VERSION}'
compile 'com.parkingwang:version-source-pkw:{VERSION}'
```

当前库支持以下三种提示框Theme样式：

- `Rocket` 火箭样式，应用于KOP、PAD项目；
- `Wave` 波浪样式，应用于IOP项目；
- `Check` 打勾样式，应用于ECP项目；

当前库支持以下第三方版本发布检测平台：

- `Fir` Fir.im
- `Pkw` 停车王App版本检测服务

#### 3. 检测更新

**Fir.im示例**

通过以下命令，控制 NextVersion.check() 发起检测更新请求：

```java
NextVersion.with(context)
    .addSource(new UrlSource.Builder()
            .GET()
            .url("http://api.fir.im/apps/latest/com.domain.app.name")
            .param("api_token", "token-token-token-token-token-token")
            .param("userId", "yoojia")
            .param("channel", "official")
            .param("cityName", "深圳市")
            .param("cityCode", 0755)
            .param("provinceName", "广东省")
            .build())
    .setVersionNotFoundHandler(SimpleNotFoundHandler.create(context))
    .addVersionFoundHandler(WaveVersionHandler.create(context))
    .addApkDownloader(WaveProgressDownloader.create(context))
    .check();
```

**Pkw示例**

```java
NextVersion.with(context)
    .setDebugEnabled(true)
    .addSource(new PkwUrlSource("http://release.domain.com", "app.name")
            .userId("yoojia")
            .channel("official")
            .cityName("")
            .cityCode("")
            .versionCode(30600)
            .versionName("3.6.0")
            .networkType(0)
            .provinceName(""))
    .addVersionFoundHandler(WaveVersionHandler.create(context))
    .addApkDownloader(WaveProgressDownloader.create(context))
    .check();
```

----

## 默认支持版本响应数据

默认上，内置支持的服务器响应版本数据格式为：

> GET /your-server-url

返回数据：

```json
{
  "status": 200,
  "msg": "success",
  "data": {
    "upgradeLevel": 0,
    "versionCode": 1002,
    "versionName": "1.0.0",
    "releaseNote": "这是一个新版本",
    "fileSize": 123456,
    "fileHash": "string",
    "fileUrl": "http://domain.com/apk-release.apk"
  }
}
```

其它参数都比较好理解，在此特别说明`upgradeLevel`参数的含义。

NextVersion内置支持4个版本升级级别：

- `0` - 推荐级别：App每次启动时弹出窗口提示用户更新，用户可以点击“稍后”按钮来取消；App在每次启动时检测版本更新;
- `1` - 推荐级别：App启动弹出时窗口提示用户更新，用户可以点击“稍后”按钮来取消。24小时内只提示一次；
- `2` - 强制级别：App每次启动时弹出窗口提示用户更新，点击更新后下载APK。用户不可取消；
- `3` - 强制下载级别：AApp每次启动时不弹窗，自动下载APK，完成后安装。用户不可取消；

## 扩展说明

通过在其它项目中，会根据需要自定义版本更新窗口的样式。NextVersion提供版本检测全生命周期的扩展接口，可以通过实现这些接口来实现相应功能。

#### 1. 扩展版本信息通知

可以参见`theme-wave`模块的`WaveVersionHandler`。

#### 2. 扩展下载器

可以参考`theme-wave`模块的`WaveProgressDownloader`。

