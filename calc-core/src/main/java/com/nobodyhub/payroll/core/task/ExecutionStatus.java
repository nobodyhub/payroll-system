package com.nobodyhub.payroll.core.task;

import lombok.Data;

/**
 * @author Ryan
 */
@Data
public class ExecutionStatus {
    private ExecutionStatusCode statusCode = ExecutionStatusCode.OK;
    private String message = "";
}
