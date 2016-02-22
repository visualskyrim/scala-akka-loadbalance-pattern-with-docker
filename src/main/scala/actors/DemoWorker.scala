package actors

import akka.actor.{ActorRef, ActorPath}

/**
 * Created by kongmu on 9/30/15.
 */
class DemoWorker(masterPath: ActorPath) extends TaskWorker(masterPath) {
  override def work(taskOwner: Option[ActorRef], task: Any): Unit = {
    log.info("Working!")
  }
}
