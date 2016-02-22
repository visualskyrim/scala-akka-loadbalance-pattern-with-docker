
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{ActorPath, ActorSystem, Props}
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory
import actors._

import scala.concurrent.duration.Duration

/**
 * Created by otakou on 6/11/14.
 */
object StartNode {

  val systemName = "load-balance"
  val systemConfigKey = "load-balance"
  val systemNodeConfigKey = "node"
  val masterNodeConfigKey = "master"
  val workerNodeConfigKey = "worker"

  val workerNodePopulationConfigKey = "population"

  val configHostKey = "host"
  val configPortKey = "port"


  val masterActorName = "master"
  val actorPathPattern = "akka://%s/user/%s"
  val masterPathPattern = s"akka.tcp://$systemName@%s:%s/user/$masterActorName"

  def main(args: Array[String]): Unit = {

    if (args.length == 1) {
      args(0) match {
        case "worker" =>
          startAsWorkerGroup()
        case "master" =>
          startAsMaster()
        case _ =>
          println(s"Can not parse start mode: ${args(0)}")
      }
    } else {
      println(s"Please choose start mode.")
    }
  }

  def startAsWorkerGroup(): Unit = {

    println("Start actor system ...")
    val system = ActorSystem(systemName, ConfigFactory.load.getConfig(systemConfigKey))

    val workerNodeConfig = ConfigFactory.load.getConfig(systemNodeConfigKey).getConfig(workerNodeConfigKey)
    val masterNodeConfig = ConfigFactory.load.getConfig(systemNodeConfigKey).getConfig(masterNodeConfigKey)

    println("Parse worker config ...")
    // get worker config
    val workerCounter = workerNodeConfig.getInt(workerNodePopulationConfigKey)
    println("Connect to master ...")
    // connect to master

    val masterHost = masterNodeConfig.getString(configHostKey)
    val masterPort = masterNodeConfig.getInt(configPortKey)
    val masterLocation = masterPathPattern.format(masterHost, masterPort)
    println(s"to location $masterLocation")

    println("Connect to agent ...")

    val masterOpt = try {
      Option(system.actorSelection(masterLocation))
    } catch {
      case x: Throwable =>
        Option.empty
    }

    if (masterOpt.isEmpty) {
      println("Can not connect to master node!")
    } else {
      println("Worker Start!")
      val workerGroup = system.actorOf(Props(new DemoWorker(
        ActorPath.fromString(masterLocation)))
        .withRouter(RoundRobinRouter(workerCounter)))
    }
  }

  def startAsMaster(): Unit = {

    println("Master Start!")
    println("Parse master config ...")

    println("Start actor system ...")
    val system = ActorSystem(systemName, ConfigFactory.load.getConfig(systemConfigKey))

    println("Spawn master ...")
    val master = system.actorOf(Props(new TaskMaster()), name = masterActorName)

    /* Test this system with a scheduled task sending tasks to master */


    val scheduler = system.scheduler
    val task = new Runnable {
      def run() {
        // create random task
        val task = s"Task:${UUID.randomUUID().toString}"
        master ! List(task)
      }
    }

    implicit val executor = system.dispatcher
    scheduler.schedule(
      initialDelay = Duration(2, TimeUnit.SECONDS),
      interval = Duration(1, TimeUnit.SECONDS),
      runnable = task)
  }
}