package com.github.truerss.plugins

import dorkbox.notify.{Notify, Position, Theme}

import java.awt.{SystemTray, Toolkit, TrayIcon}
import java.awt.TrayIcon.MessageType

sealed trait OsNotification {
  def position: Position
  def isDark: Boolean

  def push(): Unit = {
    OsNotification.pushDefault(position, isDark)
  }
}

object OsNotification {
  val title = "TrueRSS"
  val text = "New Entries Received."

  def pushDefault(position: Position, isDark: Boolean): Unit = {
    val notify = Notify.Companion
      .create()
      .title(title)
      .text(text)
      .position(position)
    if (isDark) {
      notify.setTheme(Theme.Companion.getDefaultDark())
    }
    notify.showInformation()
  }

  def apply(pos: Position, mode: Boolean): OsNotification = {
    Option(System.getProperty("os.name")) match {
      case Some("Linux") =>
        Linux(pos, mode)

      case Some("MacOs") =>
        MacOs(pos, mode)

      case Some("Windows") =>
        Windows(pos, mode)

      case _ =>
        new OsNotification {
          override val position: Position = pos

          override val isDark: Boolean = mode
        }
    }
  }
}

case class Linux(position: Position, isDark: Boolean) extends OsNotification {
  import OsNotification._

  private val commandText = Seq(Linux.command, title, text)

  override def push(): Unit = {
    val builder = new ProcessBuilder()
    val command = builder.command(commandText: _*)
    command.start().waitFor()
  }
}
object Linux {
  val command = "notify-send"
}

case class Windows(position: Position, isDark: Boolean) extends OsNotification {
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

case class MacOs(position: Position, isDark: Boolean) extends OsNotification {
  import OsNotification._

  private val command =
    s"""display notification "$text" with title "$title" """
  // todo as in linux impl
  override def push(): Unit = {
    val runtime = Runtime.getRuntime
    val code = Array("osascript", "-e", command)
    val process = runtime.exec(code)
    process.waitFor()
  }
}
