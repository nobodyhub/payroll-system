package com.nobodyhub.payroll.core.task.execution;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.nobodyhub.payroll.core.exception.PayrollCoreException;
import com.nobodyhub.payroll.core.item.ItemFactory;
import com.nobodyhub.payroll.core.item.calendar.Period;
import com.nobodyhub.payroll.core.item.common.Item;
import com.nobodyhub.payroll.core.proration.ProrationFactory;
import com.nobodyhub.payroll.core.service.proto.PayrollCoreProtocol;
import com.nobodyhub.payroll.core.task.status.ExecutionStatus;
import com.nobodyhub.payroll.core.util.DateFormatUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.nobodyhub.payroll.core.exception.PayrollCoreExceptionCode.CONTEXT_INCOMPATIBLE;
import static com.nobodyhub.payroll.core.exception.PayrollCoreExceptionCode.CONTEXT_NOT_FOUND;

/**
 * Context for execution
 *
 * @author yan_h
 * @since 2018-05-09.
 */
@Getter
@RequiredArgsConstructor
public abstract class ExecutionContext {
    /**
     * the identifier of the data in <code>items</code>
     */
    protected final String dataId;
    /**
     * the factory of all items
     */
    protected final ItemFactory itemFactory;
    /**
     * the target period of execution
     */
    protected final Period period;
    /**
     * Proration rules shared by all executions
     */
    protected final ProrationFactory prorationFactory;
    /**
     * the context contains all items
     */
    protected final Map<String, Item> items = Maps.newHashMap();
    /**
     * Execution Status
     */
    protected final ExecutionStatus executionStatus = new ExecutionStatus();

    /**
     * add new item to the context
     *
     * @param item
     */
    public void add(Item item) {
        items.put(item.getItemId(), item);
    }

    public void add(String itemId, Map<String, String> data) throws PayrollCoreException {
        Item item = itemFactory.getItem(itemId);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            item.setStringValue(DateFormatUtils.parseDate(entry.getKey()), entry.getValue());
        }
    }

    /**
     * add all values as items into the context
     *
     * @param itemValueList
     * @throws PayrollCoreException
     */
    public void addAll(List<PayrollCoreProtocol.ItemValue> itemValueList) throws PayrollCoreException {
        for (PayrollCoreProtocol.ItemValue itemValue : itemValueList) {
            add(itemValue.getItemId(), itemValue.getValuesMap());
        }
    }

    /**
     * convert the content to the response to client
     *
     * @return
     */
    public PayrollCoreProtocol.Response toResponse() {
        Map<String, String> values = Maps.newHashMap();
        for (Item item : items.values()) {
            //TOOD: remove comments
//            values.put(item.getItemId(), item.getValueAsString());
        }
        return PayrollCoreProtocol.Response.newBuilder()
                .setStatusCode(executionStatus.getStatusCode().toString())
                .setMessage(executionStatus.getMessage())
                .setDataId(dataId)
                .putAllValues(values)
                .build();
    }

    /**
     * get item from the context for given itemId
     *
     * @param itemId
     * @return
     * @throws PayrollCoreException
     */
    public Item get(String itemId) throws PayrollCoreException {
        Item item = items.get(itemId);
        if (item == null) {
            throw new PayrollCoreException(CONTEXT_NOT_FOUND)
                    .addValue("itemId", itemId);
        }
        return item;
    }

    /**
     * get item with given class and id from the context
     *
     * @param itemId
     * @param itemCls
     * @param <T>
     * @return
     * @throws PayrollCoreException
     */
    public <T> T get(String itemId, Class<T> itemCls) throws PayrollCoreException {
        Item item = get(itemId);
        if (item.getClass().isAssignableFrom(itemCls)) {
            return itemCls.cast(item);
        }
        throw new PayrollCoreException(CONTEXT_INCOMPATIBLE)
                .addValue("itemId", itemId)
                .addValue("itemCls", itemCls);
    }

    /**
     * get value from given item from the context
     *
     * @param itemId
     * @param date
     * @param valueCls
     * @param <T>
     * @return
     * @throws PayrollCoreException
     */
    public <T> T getItemValue(String itemId, LocalDate date, Class<T> valueCls) throws PayrollCoreException {
        Object object = get(itemId).getValue(date);
        if (object.getClass().isAssignableFrom(valueCls)) {
            return valueCls.cast(object);
        }
        throw new PayrollCoreException(CONTEXT_INCOMPATIBLE)
                .addValue("itemId", itemId)
                .addValue("valueCls", valueCls);
    }


    /**
     * get ids of all items in the context
     *
     * @return
     */
    public Set<String> getAllItemIds() {
        return Sets.newHashSet(items.keySet());
    }
}
