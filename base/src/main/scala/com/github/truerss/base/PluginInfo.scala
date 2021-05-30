package com.github.truerss.base

trait PluginInfo { self: BasePlugin =>
  val author: String
  val about: String
  val pluginName: String
  val version: String
}
