package com.github.truerss.plugins

import com.github.truerss.base.PublishActions

object Boot extends App {

  val plugin = new TrueRSSNotifierPlugin
  plugin.publish(PublishActions.NewEntries(Nil))

}
