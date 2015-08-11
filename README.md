# scala-akka-loadbalance-pattern-with-docker

This seed project is the usage of docker distribution for a [balancing workload Akka pattern](http://letitcrash.com/post/29044669086/balancing-workload-across-nodes-with-akka-2).

## The arrangement of actor system

Instead of using `BalancingDispatcher`, this pattern us an actor to do both message dispatching and load balance.

You will find two kinds of actors in the `src/main/scala/actors/`:

1. TaskMaster: The dispatching actor. There is a task queue inside the `TaskMaster` to store unassigned tasks.
2. TaskWorker: The actor actually consumes the message and do the work.

Here is how they work together:

1. When a `TaskWorker` was spawned, it notifies `TaskMaster` that it is ready for working.
2. When `TaskMaster` knows this `TaskWorker` has been spawned, it tells `TaskWorker` that there is work to do if there is task in task queue.
Or if there is no task in the queue, it will tell `TaskWorker` to stand by.
3. When `TaskWorker` know there is task in `TaskMaster`, it will request for the task.
4. When `TaskMaster` get request for task from `TaskWorker`, it sends task to `TaskWorker`.
5. After `TaskWorker` get task, it start to work on it.
6. After `TaskWorker` finished task, it tells `TaskMaster` that task is done.
7. When `TaskMaster` know `TaskWorker` finished task, it will send another task if there is any. Or tell `TaskWorker` to stand by otherwise.



