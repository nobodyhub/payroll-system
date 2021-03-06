package com.nobodyhub.payroll.core.service.server;

import com.nobodyhub.payroll.core.service.proto.PayrollCoreProtocol;
import com.nobodyhub.payroll.core.service.proto.PayrollCoreServiceGrpc;
import com.nobodyhub.payroll.core.task.Task;
import com.nobodyhub.payroll.core.task.TaskFactory;
import io.grpc.stub.StreamObserver;

/**
 * The Service provider for payroll core server
 *
 * @author Ryan
 */
public class PayrollCoreServerService extends PayrollCoreServiceGrpc.PayrollCoreServiceImplBase {
    /**
     * Factory to provide tasks
     */
    protected final TaskFactory taskFactory;

    public PayrollCoreServerService(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    @Override
    public StreamObserver<PayrollCoreProtocol.Request> doCalc(StreamObserver<PayrollCoreProtocol.Response> responseObserver) {
        return new StreamObserver<PayrollCoreProtocol.Request>() {
            Task task = null;

            @Override
            public void onNext(PayrollCoreProtocol.Request value) {
                if (task == null) {
                    task = taskFactory.get(value.getTaskId());
                    task.setup(responseObserver);
                }
                task.execute(value);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
                task.countDown();
            }

            @Override
            public void onCompleted() {
                task.await();
                responseObserver.onCompleted();
                task.cleanup();
            }
        };
    }
}
