package com.nobodyhub.payroll.core.item.payment;

import com.nobodyhub.payroll.core.item.abstr.Item;

import java.math.BigDecimal;

/**
 * Payment item
 *
 * @author yan_h
 * @since 2018-05-04.
 */
public class PaymentItem extends Item<BigDecimal, PaymentItem> {
    private final PaymentType paymentType;

    public PaymentItem(String itemId, PaymentType paymentType) {
        super(itemId);
        this.paymentType = paymentType;
    }

    @Override
    public BigDecimal getDefaultValue() {
        return BigDecimal.ZERO;
    }

    @Override
    public PaymentItem build() {
        return new PaymentItem(itemId, paymentType);
    }
}
