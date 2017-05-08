package com.github.truerss.base

object PublishActions {
  sealed trait Action
  case object Favorite extends Action
}

trait PublishPlugin {
  self: ConfigProvider with PluginInfo =>

  import PublishActions._

  def publish(action: Action, entry: Entry): Unit

}
