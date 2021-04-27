package com.github.truerss.plugins

import com.github.truerss.base.{BasePublishPlugin, Entry, PublishActions}
import com.typesafe.config.{Config, ConfigFactory}
import dorkbox.notify.{Notify, Pos}

import java.util
import java.util.{Collections, Timer, TimerTask}
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import scala.util.control.Exception.catching

class TrueRSSNotifierPlugin(config: Config = ConfigFactory.empty()) extends BasePublishPlugin(config) {

  import TrueRSSNotifierPlugin._

  override val author: String = "fntz <mike.fch1@gmail.com>"
  override val about: String = "Publish notification about new entries"
  override val pluginName: String = "TrueRSSNotifierPlugin"
  override val version: String = "0.0.1"

  private val defaultConfig =
    ConfigFactory.load(getClass.getClassLoader, "default")
    .getConfig(pluginName)

  private val need = catching(classOf[Exception]) either config.getConfig(pluginName).withFallback(defaultConfig) fold(
    _ => defaultConfig,
    identity
  )

  private val position = getPosition(need)

  private val current = new java.util.Vector[Entry]()
  private val timer = new Timer(s"$pluginName-timer")
  @volatile private var timerTask: TimerTask = null
  private val lock = new ReentrantLock()
  private val delay = 30*1000

  override def publish(action: PublishActions.Action): Unit = {
    action match {
      case PublishActions.NewEntries(xs) =>
        lock.lock()
        try {
          Option(timerTask) match {
            case Some(_) =>
              current.addAll(toJava(xs))

            case _ =>
              timerTask = newTimerTask(commandText)
              timer.schedule(timerTask, delay)
          }
        } finally {
          lock.unlock()
        }

      case _ =>
        // ignored in this plugin
    }
  }

  private def newTimerTask(commandText: Seq[String]): TimerTask = {
    new TimerTask {
      override def run(): Unit = {
        println(s"----------> ${current.size()}")
        current.removeAllElements()
        runCommandOrDefault(commandText)
        clean
      }
    }
  }

  private def clean: Unit = {
    timerTask = null
  }

  private def runCommandOrDefault(commandText: Seq[String]): Unit = {
    if (commandText.nonEmpty) {
      pushDefaultNotification()
    } else {
      try {
        // try to use notify-send lib
        val builder = new ProcessBuilder()
        val command = builder.command(commandText : _*)
        command.start().waitFor()
      } catch {
        // when not available
        case _: Throwable =>
          pushDefaultNotification()
      }
    }

  }

  private def pushDefaultNotification(): Unit = {
    val notify = Notify.create()
      .title(title)
      .text(text)
      .position(position)
    if (isDark) {
      notify.darkStyle()
    }
    notify.showInformation()
  }

  def isDark: Boolean = {
    need.getBoolean("dark")
  }

}

object TrueRSSNotifierPlugin {
  val title = "TrueRSS"
  val text = "New Entries Received."
  val linuxNotify = Seq("notify-send", title, text)

  val macOsNotify = Seq("osascript", "-e", s"'display notification $text with title $title'")

  def getPosition(c: Config): Pos = {
    c.getString("position").toLowerCase.trim match {
      case "top-left" => Pos.TOP_LEFT
      case "top-right" => Pos.TOP_RIGHT
      case "center" => Pos.CENTER
      case "bottom-left" => Pos.BOTTOM_LEFT
      case "bottom-right" => Pos.BOTTOM_RIGHT
      case _ => Pos.TOP_LEFT
    }
  }

  def commandText: Seq[String] = {
    Option(System.getProperty("os.name")) match {
      case Some("Linux") =>
        linuxNotify

      case Some("MacOs") =>
        macOsNotify

      case Some("Windows") =>
        Seq.empty

      case _ =>
        Seq.empty
    }
  }

  def toJava(xs: Iterable[Entry]): java.util.Collection[Entry] = {
    val col = new util.ArrayList[Entry](xs.size)
    xs.foreach(col.add)
    col
  }
}