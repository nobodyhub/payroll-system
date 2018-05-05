package com.nobodyhub.payroll.core.formula.arithmetic;

import com.google.common.collect.Sets;
import com.nobodyhub.payroll.core.exception.PayrollCoreException;
import com.nobodyhub.payroll.core.formula.common.Formula;
import com.nobodyhub.payroll.core.item.ItemContext;
import com.nobodyhub.payroll.core.item.payment.PaymentItem;

import java.math.BigDecimal;
import java.util.Set;

/**
 * A Formula formed by Arithmetic Expressions
 *
 * @author Ryan
 */
public class ArithmeticFormula extends Formula {
    private FormulaExpression expression;

    @Override
    public PaymentItem evaluate(ItemContext context) throws PayrollCoreException {
        BigDecimal result = expression.evaluate(context);
        context.add(targetItemId, result);
        PaymentItem item = createPaymentItem();
        item.setValue(result);
        return item;
    }

    @Override
    public Set<String> getRequiredItems() {
        Set<String> itemIds = Sets.newHashSet();
        expression.getRequiredItems(itemIds);
        return itemIds;
    }
}