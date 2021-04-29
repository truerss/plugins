package com.github.truerss.plugins

import dorkbox.notify.{Notify, Pos}

import java.awt.{SystemTray, Toolkit, TrayIcon}
import java.awt.TrayIcon.MessageType

sealed trait OsNotification {
  def position: Pos
  def isDark: Boolean

  def push(): Unit = {
    OsNotification.pushDefault(position, isDark)
  }
}

object OsNotification {
  val title = "TrueRSS"
  val text = "New Entries Received."

  def pushDefault(position: Pos, isDark: Boolean): Unit = {
    val notify = Notify.create()
      .title(title)
      .text(text)
      .position(position)
    if (isDark) {
      notify.darkStyle()
    }
    notify.showInformation()
  }

  def apply(pos: Pos, mode: Boolean): OsNotification = {
    Option(System.getProperty("os.name")) match {
      case Some("Linux") =>
        Linux(pos, mode)

      case Some("MacOs") =>
         MacOs(pos, mode)

      case Some("Windows") =>
        Windows(pos, mode)

      case _ =>
        new OsNotification {
          override val position: Pos = pos

          override val isDark: Boolean = mode
        }
    }
  }
}

case class Linux(position: Pos,
                 isDark: Boolean) extends OsNotification {
  import OsNotification._

  private val commandText = Seq(Linux.command, title, text)

  override def push(): Unit = {
    val builder = new ProcessBuilder()
    val command = builder.command(commandText : _*)
    command.start().waitFor()
  }
}
object Linux {
  val command = "notify-send"
}

case class Windows(position: Pos,
                   isDark: Boolean) extends OsNotification {
  import OsNotification._

  override def push(): Unit = {
    if (SystemTray.isSupported) {
      val tray = SystemTray.getSystemTray
      val image = Toolkit.getDefaultToolkit.createImage(Windows.icon)
      val trayIcon = new TrayIcon(image, title)
      trayIcon.setImageAutoSize(true)
      trayIcon.setToolTip(text)
      tray.add(trayIcon)
      trayIcon.displayMessage(title, text, MessageType.INFO)
    } else {
      super.push()
    }
  }
}
object Windows {
  val icon = "icon.png"
}

case class MacOs(position: Pos,
                 isDark: Boolean) extends OsNotification {
  import OsNotification._

  private val commandText =
    Seq("osascript", "-e", s"'display notification $text with title $title'")

  override def push(): Unit = {
    val builder = new ProcessBuilder()
    val command = builder.command(commandText : _*)
    command.start().waitFor()
  }
}
