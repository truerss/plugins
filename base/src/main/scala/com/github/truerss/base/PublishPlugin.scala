package com.github.truerss.base

object PublishActions {
  sealed trait Action
  case class Favorite(entry: Entry) extends Action
  case class NewEntries(entries: Iterable[Entry]) extends Action
}

trait PublishPlugin {
  self: ConfigProvider with PluginInfo =>

  import PublishActions._

  def publish(action: Action): Unit

}
