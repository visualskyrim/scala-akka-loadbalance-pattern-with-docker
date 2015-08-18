import actors.TaskMaster
import actors.TaskMaster._
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import scala.concurrent.duration._

/**
 * Created by kongmu on 8/12/15.
 */
class TaskMasterSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  val testMasterPath = self.path

  def this() = this(ActorSystem("TaskMasterSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "TaskMaster" should "tell worker that there is task in master when worker is spawned" in {
    val master = TestActorRef(Props(new TaskMaster()))
    master ! TaskList(List(1))

    master ! NewWorker(self)
    expectMsgType[TaskIncoming]
  }

  "TaskMaster" should "tell worker that there is no task when a worker send a response of task" in {
    val master = TestActorRef(Props(new TaskMaster()))

    master ! TaskResponse(self, isFinished = true)
    expectMsgType[TaskConsumedOut]
  }

  "TaskMaster" should "tell workers that there is task to do when new tasks are coming" in {
    val master = TestActorRef(Props(new TaskMaster()))
    master ! NewWorker(self)

    master ! TaskList(List(1))

    expectMsgType[TaskIncoming]
  }
}
