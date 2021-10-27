package com.hd.tvpro.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.geniusgithub.mediarender.device.DeviceInfo
import com.geniusgithub.mediarender.device.DeviceUpdateBrocastFactory
import com.geniusgithub.mediarender.device.ItatisticsEvent
import com.geniusgithub.mediarender.util.CommonLog
import com.geniusgithub.mediarender.util.LogFactory
import com.hd.tvpro.constants.TimeConstants
import com.lzy.okgo.OkGo
import com.lzy.okgo.https.HttpsUtils
import com.lzy.okgo.model.HttpHeaders
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 作者：By 15968
 * 日期：On 2021/10/26
 * 时间：At 13:36
 */
open class App : Application(), ItatisticsEvent {
    private val log: CommonLog = LogFactory.createLog()

    companion object {
        lateinit var INSTANCE: App
    }

    private var mDeviceInfo: DeviceInfo = DeviceInfo()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        //OKGO配置
        val builder = OkHttpClient.Builder()
        builder.readTimeout(TimeConstants.HTTP_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
        builder.writeTimeout(TimeConstants.HTTP_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
        builder.connectTimeout(TimeConstants.HTTP_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
        //方法一：信任所有证书,不安全有风险
        val sslParams1 = HttpsUtils.getSslSocketFactory()
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, HttpsUtils.UnSafeTrustManager)
            .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
        val headers = HttpHeaders()
        headers.put("charset", "UTF-8")
        OkGo.getInstance().init(this).setOkHttpClient(builder.build())
            .setRetryCount(1)
            .addCommonHeaders(headers)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    open fun updateDevInfo(name: String?, uuid: String?) {
        mDeviceInfo.dev_name = name
        mDeviceInfo.uuid = uuid
    }

    open fun setDevStatus(flag: Boolean) {
        mDeviceInfo.status = flag
        DeviceUpdateBrocastFactory.sendDevUpdateBrocast(this)
    }

    open fun getDevInfo(): DeviceInfo? {
        return mDeviceInfo
    }

    override fun onEvent(eventID: String?) {
        log.e("eventID = $eventID")
    }

    override fun onEvent(eventID: String?, map: HashMap<String, String>?) {
        log.e("eventID = $eventID")
    }
}