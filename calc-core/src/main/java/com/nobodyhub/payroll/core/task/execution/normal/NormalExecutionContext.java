package com.nobodyhub.payroll.core.task.execution.normal;

import com.nobodyhub.payroll.core.item.ItemFactory;
import com.nobodyhub.payroll.core.item.calendar.Period;
import com.nobodyhub.payroll.core.proration.ProrationFactory;
import com.nobodyhub.payroll.core.task.execution.ExecutionContext;

/**
 * Execution context for normal(non-retroactive) calculation
 *
 * @author Ryan
 */
public class NormalExecutionContext extends ExecutionContext {

    public NormalExecutionContext(String dataId, ItemFactory itemFactory, Period period, ProrationFactory prorationFactory) {
        super(dataId, itemFactory, prorationFactory, period);
    }
}
