package com.github.truerss.plugins

import com.github.truerss.base.{BasePublishPlugin, Entry, PublishActions}
import com.typesafe.config.{Config, ConfigFactory}
import dorkbox.notify.Pos

import java.util
import java.util.concurrent.{Executors, ThreadFactory, TimeUnit}
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

  private val position = getPosition
  private val mode = isDark
  private val currentNotifier = OsNotification(position, mode)
  private val current = new java.util.Vector[Entry]()
  private val every = 30
  private val exec = Executors.newSingleThreadScheduledExecutor(
    new TrueRSSNotifierThreadFactory
  )
  exec.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {
      tryToPublish()
    }
  }, 0, every, TimeUnit.SECONDS)

  override def publish(action: PublishActions.Action): Unit = {
    action match {
      case PublishActions.NewEntries(xs) =>
        current.addAll(toJava(xs))

      case _ =>
        // ignored in this plugin
    }
  }

  private def tryToPublish(): Unit = {
    if (!current.isEmpty) {
      try {
        currentNotifier.push()
      } catch {
        case _: Throwable =>
          OsNotification.pushDefault(position, mode)
      } finally {
          current.clear()
      }
    }
  }

  private def getPosition: Pos = {
    positionMap.getOrElse(
      need.getString("position").toLowerCase.trim, Pos.TOP_RIGHT
    )
  }

  private def isDark: Boolean = {
    need.getBoolean("dark")
  }

}

object TrueRSSNotifierPlugin {

  val positionMap = Map(
    "top-left" -> Pos.TOP_LEFT,
    "top-right" -> Pos.TOP_RIGHT,
    "center" -> Pos.CENTER,
    "bottom-left" -> Pos.BOTTOM_LEFT,
    "bottom-right" -> Pos.BOTTOM_RIGHT
  )

  def toJava(xs: Iterable[Entry]): java.util.Collection[Entry] = {
    val col = new util.ArrayList[Entry](xs.size)
    xs.foreach(col.add)
    col
  }

  class TrueRSSNotifierThreadFactory extends ThreadFactory {
    override def newThread(r: Runnable): Thread = {
      new Thread(r, "truerss-notifier")
    }
  }
}