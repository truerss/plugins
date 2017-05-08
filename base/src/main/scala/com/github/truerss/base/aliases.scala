package com.github.truerss.base

object aliases {
  type WithContent = BaseContentReader with UrlMatcher with Priority with PluginInfo
}
