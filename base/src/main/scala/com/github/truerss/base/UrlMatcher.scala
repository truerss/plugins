package com.github.truerss.base

import java.net.URL

trait UrlMatcher {
  def matchUrl(url: URL): Boolean
}

