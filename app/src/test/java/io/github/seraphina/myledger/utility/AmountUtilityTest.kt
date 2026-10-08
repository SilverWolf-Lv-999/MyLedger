package io.github.seraphina.myledger.utility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AmountUtilityTest {
    @Test
    fun parseCentsFromYuanText() {
        assertEquals(2500L, AmountUtility.parseCents("微信支付 已支付 25.00 元"))
    }

    @Test
    fun parseCentsFromSymbolText() {
        assertEquals(1L, AmountUtility.parseCents("收款到账 ￥0.01"))
    }

    @Test
    fun parseCentsFromDecimalText() {
        assertEquals(1234L, AmountUtility.parseCents("付款成功 12.34"))
    }

    @Test
    fun parseCentsWithoutDecimal() {
        assertEquals(10000L, AmountUtility.parseCents("你已成功付款 100元"))
    }

    @Test
    fun parseCentsReturnsNullForTextWithoutAmount() {
        assertNull(AmountUtility.parseCents("微信支付凭证"))
    }

    @Test
    fun parseInputCentsAcceptsSymbolAndGrouping() {
        assertEquals(123456L, AmountUtility.parseInputCents("¥1,234.56"))
    }

    @Test
    fun parseInputCentsAcceptsSingleDecimal() {
        assertEquals(1050L, AmountUtility.parseInputCents("10.5"))
    }

    @Test
    fun parseInputCentsReturnsNullForInvalidInput() {
        assertNull(AmountUtility.parseInputCents("abc"))
    }

    @Test
    fun formatCentsUsesGroupingAndTwoDecimals() {
        assertEquals("1,234.56", AmountUtility.format(123456L))
        assertEquals("0.01", AmountUtility.format(1L))
    }

    @Test
    fun formatWithSymbolPrefixesCurrency() {
        assertEquals("¥25.00", AmountUtility.formatWithSymbol(2500L))
    }
}
