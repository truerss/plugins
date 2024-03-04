package com.github.truerss.base

object ContentTypeParam {

  sealed trait RequestParam
  case class HtmlRequest(body: String) extends RequestParam
  case class UrlRequest(url: java.net.URI) extends RequestParam
  object RequestParam {
    def apply(url: java.net.URI) = UrlRequest(url)
    def apply(html: String) = HtmlRequest(html)
  }

  sealed trait ContentTypeParam
  case object HTML extends ContentTypeParam
  case object URI extends ContentTypeParam

}
