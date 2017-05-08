package com.github.truerss.base

import com.typesafe.config.Config

abstract class BasePlugin(override val config: Config)
  extends ConfigProvider

abstract class BaseFeedPlugin(config: Config)
  extends BasePlugin(config)
  with ConfigProvider
  with BaseFeedReader
  with PluginInfo
  with Priority
  with UrlMatcher

abstract class BaseContentPlugin(config: Config)
  extends BasePlugin(config)
  with ConfigProvider
  with BaseContentReader
  with PluginInfo
  with Priority
  with UrlMatcher
  with ContentType

abstract class BaseSitePlugin(config: Config)
  extends BasePlugin(config)
  with BaseContentReader
  with BaseFeedReader
  with ConfigProvider
  with PluginInfo
  with Priority
  with UrlMatcher
  with ContentType

abstract class BasePublishPlugin(config: Config)
  extends BasePlugin(config)
  with PublishPlugin
  with ConfigProvider
  with PluginInfo