package io.github.seraphina.myledger.common.model

import io.github.seraphina.myledger.common.config.CommonConfig

enum class RecordSource(val label: String, val packageName: String?) {
    WECHAT("微信", CommonConfig.WECHAT_PACKAGE),
    ALIPAY("支付宝", CommonConfig.ALIPAY_PACKAGE),
    MANUAL("手动", null)
}
