package com.nobodyhub.payroll.core.task.execution;

import com.nobodyhub.payroll.core.exception.PayrollCoreException;
import com.nobodyhub.payroll.core.context.NormalFormulaContainer;
import com.nobodyhub.payroll.core.context.RetroFormulaContainer;
import com.nobodyhub.payroll.core.formula.normal.NormalFormula;
import com.nobodyhub.payroll.core.formula.retro.RetroFormula;
import com.nobodyhub.payroll.core.service.common.HistoryData;
import com.nobodyhub.payroll.core.context.TaskContext;
import com.nobodyhub.payroll.core.task.callback.Callback;
import com.nobodyhub.payroll.core.context.ExecutionContext;
import com.nobodyhub.payroll.core.context.RetroExecutionContext;

import java.util.List;

/**
 * Execution for retroactive task
 *
 * @author yan_h
 * @since 2018-05-08.
 */
public class RetroTaskExecution implements Runnable {
    /**
     * the execution context for the related normal execution
     */
    private final ExecutionContext executionContext;
    /**
     * Task context
     */
    private final TaskContext taskContext;
    /**
     * The history data
     */
    private final HistoryData historyData;
    /**
     * Callback to handle the execution
     */
    private final Callback callback;


    public RetroTaskExecution(ExecutionContext executionContext, HistoryData historyData, Callback callback) {
        this.executionContext = executionContext;
        this.taskContext = executionContext.getTaskContext();
        this.historyData = historyData;
        this.callback = callback;
    }

    @Override
    public void run() {
        callback.onStart();
        NormalFormulaContainer normalFormulaContext = taskContext.getNormalFormulaContext();
        RetroFormulaContainer retroFormulaContext = taskContext.getRetroFormulaContext();
        try {
            //re-calc past data
            List<RetroExecutionContext> retroContexts = historyData.toRetroContexts(executionContext.getDataId(), taskContext);
            for (RetroExecutionContext retroContext : retroContexts) {
                for (NormalFormula formula : normalFormulaContext.getFormulas()) {
                    retroContext.add(formula.evaluate(retroContext));
                }
            }
            //handle diff values
            for (RetroFormula formula : retroFormulaContext.getFormulas()) {
                executionContext.add(formula.evaluate(retroContexts));
            }
        } catch (PayrollCoreException e) {
            callback.onError(e, executionContext);
        }
        callback.onCompleted(executionContext);
    }
}
