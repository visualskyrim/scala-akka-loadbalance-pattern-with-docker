package actors

import actors.TaskMaster._
import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.collection.mutable

/**
 * Created by kongmu on 8/7/15.
 */

// companion object for protocol
object TaskMaster {
  case class NewWorker(worker: ActorRef)
  case class TaskList(taskList: List[Any])
  case class TaskIncoming()
  case class TaskResponse(worker: ActorRef, isFinished: Boolean=true)
  case class TaskRequest(worker: ActorRef)
  case class TaskConsumedOut()
  // Add original sender of task, so that worker can know who send this.
  // In that case, worker can directly send result of the task to the original sender
  // Change the type of `task` to apply more specified task, or you can define you own task.
  case class TaskTicket(task: Any, taskOwner: Option[ActorRef])
}


class TaskMaster extends Actor with ActorLogging {
  
  val taskQueue = mutable.Queue.empty[Object]
  val workers = mutable.Map.empty[ActorRef, Option[TaskTicket]]
  
  
  override def receive: Receive = {

    // when new worker spawns
    case NewWorker(worker) =>
      context.watch(worker)
      workers += (worker -> None)
      notifyFreeWorker()

    // when worker send task result back
    case TaskResponse(worker, isFinished) =>
      if (isFinished)
        log.debug(s"task is finished.")
      else
        log.debug(s"task failed to finish.")
      workers += (worker -> None)
      self ! TaskRequest(worker)

    // when worker want task
    case TaskRequest(worker) =>
      if (workers.contains(worker)) {
        if (taskQueue.isEmpty)
          worker ! TaskConsumedOut()
        else {
          if (workers(worker) == None) {
            val task = taskQueue.dequeue()
            assignTask(task, worker)
          } else {
            // this will never happen
            log.error("Some worker is requiring task while processing the task.")
          }
        }
      }

    case TaskList(taskList: List[Object]) =>
      // TODO: Workload control here!
      // TODO: Tell the guy who send these tasks to slow down!
      taskQueue.enqueue(taskList: _*)
      notifyFreeWorker()

  }

  def assignTask(task: Object, worker: ActorRef) = {
    workers += (worker -> Some(TaskTicket(task, Some(self))))
    worker ! TaskTicket(task, Some(self))
  }

  def notifyFreeWorker() = {
    if (taskQueue.nonEmpty)
      workers.foreach {
        case (worker, m) if m.isEmpty => worker ! TaskIncoming()
        case _ => log.error("Something wired in the worker map!")
      }
  }
}
