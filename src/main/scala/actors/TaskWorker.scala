package actors

import actors.TaskMaster._
import actors.TaskWorker.WorkFinished
import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef}


object TaskWorker {
  case class WorkFinished(isFinished: Boolean = true)
}

abstract class TaskWorker(masterPath: ActorPath) extends Actor with ActorLogging {

  val master = context.actorSelection(masterPath)

  override def preStart() = master ! NewWorker(self)

  // TODO: add postRestart function for better robustness

  def working(task: Any, masterRef: ActorRef): Receive = {
    case WorkFinished(isFinished) =>
      log.debug("Worker finished task.")
      masterRef ! TaskResponse(self, isFinished)
      context.become(idle)
  }



  def idle: Receive = {
    case TaskIncoming =>
      master ! TaskRequest(self)
    case TaskTicket(task, taskOwner) =>
      log.debug("Worker get task.")
      context.become(working(task, sender()))
      work(taskOwner, task)
    case TaskConsumedOut =>
      log.debug("Worker did not get any task.")

  }

  def receive = idle

  def finish(): Unit = {
    self ! WorkFinished(isFinished = true)
  }

  def fail(): Unit = {
    self ! WorkFinished(isFinished = false)
  }

  def work(taskOwner: Option[ActorRef], task: Any): Unit

  // define this method if you want to deal with the result of processing
  // def submitWork(workResult: WorkerResult): Unit


}
