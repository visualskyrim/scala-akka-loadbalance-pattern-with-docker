package base

import actors.TaskWorker
import akka.actor.Actor.Receive
import akka.actor.{ActorRef, ActorPath}

/**
 * Created by kongmu on 8/10/15.
 */
class TestWorker(masterPath: ActorPath) extends TaskWorker(masterPath: ActorPath) {

  override def work(taskOwner: Option[ActorRef], task: Any): Unit = {
    // check task type
    task match {
      case x: Boolean =>
        // send response back asap
        if (x) {
          finish()
        } else {
          fail()
        }
      case _ =>
        // if we don't know this task
        log.error("Can't not process this task.")
        fail()
    }
  }
}
