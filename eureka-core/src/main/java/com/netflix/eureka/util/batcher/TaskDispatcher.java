package com.netflix.eureka.util.batcher;

/**
 * 任务调度器从客户端接收任务，并将其执行委托给可配置数量的工作者。任务可以一次处理一个，也可以分批处理。只有未过期的任务才会被执行，如果有相同id的新任务被安排执行，旧的任务会被删除。
 * 懒惰地将工作（只在需要时）分派给工作者，保证数据总是最新的，没有陈旧的任务处理发生。
 * 任务处理器
 * 该组件的客户端必须提供一个TaskProcessor接口的实现，它将完成任务处理的实际工作。这个实现必须是线程安全的，因为它是由多个线程同时调用的。
 *
 * Task dispatcher takes task from clients, and delegates their execution to a configurable number of workers.
 * The task can be processed one at a time or in batches. Only non-expired tasks are executed, and if a newer
 * task with the same id is scheduled for execution, the old one is deleted. Lazy dispatch of work (only on demand)
 * to workers, guarantees that data are always up to date, and no stale task processing takes place.
 * <h3>Task processor</h3>
 * A client of this component must provide an implementation of {@link TaskProcessor} interface, which will do
 * the actual work of task processing. This implementation must be thread safe, as it is called concurrently by
 * multiple threads.
 * <h3>Execution modes</h3>
 * To create non batched executor call {@link TaskDispatchers#createNonBatchingTaskDispatcher(String, int, int, long, long, TaskProcessor)}
 * method. Batched executor is created by {@link TaskDispatchers#createBatchingTaskDispatcher(String, int, int, int, long, long, TaskProcessor)}.
 *
 * @author Tomasz Bak
 */
public interface TaskDispatcher<ID, T> {

    void process(ID id, T task, long expiryTime);

    void shutdown();
}
