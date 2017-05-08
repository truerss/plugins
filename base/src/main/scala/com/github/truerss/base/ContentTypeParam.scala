package com.github.truerss.base

object ContentTypeParam {

  sealed trait RequestParam
  case class HtmlRequest(body: String) extends RequestParam
  case class UrlRequest(url: java.net.URL) extends RequestParam
  object RequestParam {
    def apply(url: java.net.URL) = UrlRequest(url)
    def apply(html: String) = HtmlRequest(html)
  }

  sealed trait ContentTypeParam
  case object HTML extends ContentTypeParam
  case object URL extends ContentTypeParam

}
