# NextVersion设计说明

在我们公司的产品中，现阶段由于灰度发布系统未完成，面对以下几个版本发布的功能需求而设计出NextVersion的SDK支持库：

- 支持多个更新源；
- 支持本地缓存；
- 无远程服务器场景下测试；
- 线下产品可用Root静默更新；
- 不同产品需要实现不同风格的弹窗；
- 需要支持多种更新级别；

在设计上，NextVersion分为两个过程，分别是：检测过程(check)、更新过程(upgrade)。

### 1. Check

`source`  >  `fetcher`  >  `version-parser`  >  `version-handler`

在Check过程中，NextVersion内部引擎调试各个接口的具体实现类，完成从更新源获取版本信息，到显示新版本弹窗的过程。

### 2. Upgrade

`downloader`  >  `installer`

Upgrade过程，由用户在新版本弹窗中点击“更新”按钮触发，或者是基于更新级别静默调用来触发。这个过程中，NextVersion根据版本信息对象，下载APK文件到本地，并调用安装器来安装。

## 一、检测过程 - Check

### 1.1 更新源 - `Source`

Source指定NextVersion可以从哪些源来获取版本信息。默认地，NextVersion提供了以下三类更新源：

- `UrlSource` 从URL地址获取版本信息。此为最常用方式；
- `MockAssetSource` 从App的assets目录获取指定JSON文件，作为更新源。此方式通常用于测试；
- `LocalCacheSource` 从本地缓存文件中获取更新源方式；此方式尚未实现，用于下载缓存；

### 1.2 更新源获取器 - `Fetcher`

Fetcher用于获取对应Source的内容。对应地，NextVersion提供以下三个更新源获取器：

- `RemoteHttpSourceFetcher` 从URL地址中获取服务器响应版本数据；
- `MockAssetSourceFetcher` 从App的assets目录读取版本数据；
- `LocalCacheFileSourceFetcher` 从本地缓存文件中读取版本数据；

### 1.3 版本数据解析器 - `VersionParser`

当Fetcher获取到版本数据后，需要由Parser将数据解析成Version对象以供后续使用。默认提供为JSON解析器：`SimpleJSONParser`，
它可以解析上面列出的默认支持JSON数据结构。

如果需要解析自定义的数据结构，可以通过实现`VersionParser`接口来实现。
也可以参考`thirdparty-fir`子模块的实现，它提供了如何解析Fir.im发布服务的响应数据。

### 1.4 版本信息处理器 - `VersionHandler`

由Parser解析出有效的版本信息，由NextVersion比较versionCode的大小关系后，如果发现新版本，将由VersionHandler接口负责展示。
这部分涉及各个App不同的UI设计需求，可以通过扩展来实现。参考`theme-rocket`和`theme-wave`两个模块。

需要指出的是，在VersionHandler中的主要职责是：

1. 将Version信息展示给用户，并提供相应的更新按钮；
2. 更新按钮被用户点击后，通过NextVersion.upgrade() 进入`更新阶段`。

## 二、升级过程 - Upgrade

在此阶段中，NextVersion已经通过check()接口获取到远程服务器的新版本信息，将在此下载Apk文件并安装。

### 2.1 Apk文件下载器 - `ApkDownloader`

Apk文件下载器，负责将Version的Apk下载地址的APk文件下载到本地并保存。以及，负责向用户展示下载进度。
参考`theme-wave`模块的 `WaveProgressDownloader`及相关文件。

### 2.2 Apk安装器 - `InstallHandler`

InstallHandler负责调用Android安装器来安装已下载的APK文件。
默认情况下，使用系统安装器来安装，另外提供一个Root状态下静默安装的功能（未实现）。

## 关于各个接口的责任链说明

在以上提供的各个接口，它们都可以通过`addXXX`方式添加多个，在内部，每个接口组以责任链的方式来工作，即其中任一个接口声明它已处理，接口组的其它接口将被中断：

- SourceFetcher
- VersionParser
- VersionFoundHandler
- ApkDownloader
- VersionInstallHandler

**部分接口可以通过设置priority来控制它们的优先级。**

