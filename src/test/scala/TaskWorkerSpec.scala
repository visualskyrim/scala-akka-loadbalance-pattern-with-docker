import actors.TaskMaster.{NewWorker, TaskResponse, TaskTicket}
import actors.TaskWorker
import base.TestWorker
import org.scalatest.{ BeforeAndAfterAll, FlatSpecLike, Matchers }
import akka.actor.{ Actor, Props, ActorSystem }
import akka.testkit.{ ImplicitSender, TestKit, TestActorRef }
import scala.concurrent.duration._

class TaskWorkerSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("TaskWorkerSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "TaskWorker" should "tell master it is created" in {
    val worker = TestActorRef(Props(new TestWorker(self.path)))
    expectMsgType[NewWorker].worker should be(worker)
  }

  "An Worker" should "tell use whether task is finished according to the task given." in {
    val worker = TestActorRef(Props(new TestWorker(self.path)))
    expectMsgType[NewWorker].worker should be(worker)

    worker ! TaskTicket(true, None)
    expectMsgType[TaskResponse].isFinished should be(true)

    worker ! TaskTicket(false, None)
    expectMsgType[TaskResponse].isFinished should be(false)

    worker ! TaskTicket("true", None)
    expectMsgType[TaskResponse].isFinished should be (false)
  }
}
