package io.github.seraphina.myledger.common.config

object CommonConfig {
    const val DATABASE_NAME = "my_ledger.db"
    const val RECORD_TABLE = "ledger_record"
    const val SETTINGS_TABLE = "ledger_settings"
    const val SETTINGS_ROW_ID = 0L
    const val DEDUP_WINDOW_MILLIS = 5 * 60 * 1000L
    const val KEEP_ALIVE_CHANNEL_ID = "ledger_keep_alive"
    const val KEEP_ALIVE_NOTIFICATION_ID = 1001
    const val KEEP_ALIVE_RESTART_DELAY_MILLIS = 2 * 1000L
    const val WECHAT_PACKAGE = "com.tencent.mm"
    const val ALIPAY_PACKAGE = "com.eg.android.AlipayGphone"

    val INCOME_KEYWORDS = listOf(
        "收款", "到账", "收入", "收到", "转入", "退款", "退还", "返还", "入账", "红包"
    )

    val EXPENSE_KEYWORDS = listOf(
        "支付", "付款", "支出", "消费", "扣款", "扣费", "代扣", "转出", "转账", "已付", "已扣"
    )

    val WECHAT_PAYMENT_KEYWORDS = listOf(
        "微信支付", "微信收款", "微信零钱", "收款到账", "支付凭证"
    )

    val ALIPAY_PAYMENT_KEYWORDS = listOf(
        "支付宝", "支付成功", "付款成功", "收款成功", "退款"
    )
}
