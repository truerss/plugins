package com.github.truerss.base

import java.net.URI

trait UrlMatcher {
  def matchUrl(url: URI): Boolean
}
