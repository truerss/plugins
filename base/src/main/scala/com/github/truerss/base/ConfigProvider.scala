package com.github.truerss.base

import com.typesafe.config.Config

trait ConfigProvider {
  val config: Config
}
