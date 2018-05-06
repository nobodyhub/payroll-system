package com.nobodyhub.payroll.core.item.hr;

import com.nobodyhub.payroll.core.item.common.Item;
import com.nobodyhub.payroll.core.util.DateFormatUtils;

import java.time.LocalTime;

/**
 * Date&Time HR item
 *
 * @author yan_h
 * @since 2018-05-04.
 */
public class HrTimeItem extends Item<LocalTime, HrTimeItem> {

    public HrTimeItem(String itemId) {
        super(itemId, LocalTime.class);
    }

    @Override
    public void setStringValue(String value) {
        this.value = DateFormatUtils.parseTime(value);
    }

    @Override
    public LocalTime getDefaultValue() {
        return LocalTime.now();
    }

    @Override
    public HrTimeItem build() {
        return new HrTimeItem(itemId);
    }
}
