import _root_.TaskMaster._
import _root_.TaskWorker.WorkFinished
import akka.actor.{ActorRef, ActorPath, ActorLogging, Actor}
import akka.actor.Actor.Receive

/**
 * Created by kongmu on 8/7/15.
 */

object TaskWorker {
  case class WorkFinished()
}

abstract class TaskWorker(masterPath: ActorPath) extends Actor with ActorLogging {

  val master = context.actorSelection(masterPath)

  def working(task: Object, masterRef: ActorRef): Receive = {
    case WorkFinished =>
      log.debug("Worker finished task.")
      masterRef ! TaskResponse(self)
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

  def finish(): Unit = {
    self ! WorkFinished
  }

  def work(taskOwner: ActorRef, task: Object): Unit

  // define this method if you want to deal with the result of processing
  // def submitWork(workResult: WorkerResult): Unit


}
