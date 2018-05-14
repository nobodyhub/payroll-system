package com.nobodyhub.payroll.core.proration.impl;

import com.google.common.collect.Maps;
import com.nobodyhub.payroll.core.exception.PayrollCoreException;
import com.nobodyhub.payroll.core.item.calendar.CalendarItem;
import com.nobodyhub.payroll.core.item.calendar.Period;
import com.nobodyhub.payroll.core.proration.abstr.Proration;
import com.nobodyhub.payroll.core.task.execution.ExecutionContext;
import com.nobodyhub.payroll.core.util.PayrollCoreConst;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author yan_h
 * @since 2018-05-10.
 */
public class CalendarProration extends Proration {

    public CalendarProration(String id, String calendarItemId) {
        super(id, calendarItemId);
    }

    @Override
    public SortedMap<LocalDate, BigDecimal> prorate(ExecutionContext context,
                                                    SortedMap<LocalDate, BigDecimal> beforeValues)
            throws PayrollCoreException {
        SortedMap<Period, BigDecimal> periodValues = convertValueToPeriod(beforeValues, context.getPeriod());
        CalendarItem item = context.get(calendarItemId, CalendarItem.class);
        return proratePeriod(item, periodValues, context.getPeriod());
    }

    protected SortedMap<LocalDate, BigDecimal> proratePeriod(CalendarItem item,
                                                             SortedMap<Period, BigDecimal> data,
                                                             Period period)
            throws PayrollCoreException {
        SortedMap<LocalDate, BigDecimal> calendar = unzip(item.getValues(), period);
        BigDecimal totalVal = calendar.values().stream().reduce(BigDecimal.ZERO, (a, b) -> (a.add(b)));


        SortedMap<LocalDate, BigDecimal> resultMap = Maps.newTreeMap();
        for (Period sub : data.keySet()) {
            BigDecimal periodVal = BigDecimal.ZERO;
            for (Map.Entry<LocalDate, BigDecimal> entry : calendar.entrySet()) {
                LocalDate sDate = entry.getKey();
                if (sub.isAfter(sDate)) {
                    // sdate is before period
                    continue;
                }
                if (sub.contains(sDate)) {
                    periodVal = periodVal.add(entry.getValue());
                } else {
                    // sdate is after period
                    break;
                }
            }
            resultMap.put(sub.getStart(),
                    data.get(sub)
                            .multiply(periodVal)
                            .divide(totalVal, PayrollCoreConst.MATH_CONTEXT));
        }
        return resultMap;
    }

    protected SortedMap<LocalDate, BigDecimal> unzip(SortedMap<LocalDate, BigDecimal> values,
                                                     Period period) {
        SortedMap<LocalDate, BigDecimal> unzipVals = Maps.newTreeMap();
        for (Map.Entry<LocalDate, BigDecimal> entry : values.entrySet()) {
            LocalDate date = entry.getKey();
            BigDecimal value = entry.getValue();
            while (period.isAfter(date)) {
                date = date.plusDays(1);
                continue;
            }
            while (period.contains(date)) {
                unzipVals.put(date, value);
                date = date.plusDays(1);
            }
        }
        return unzipVals;
    }
}
