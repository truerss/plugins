package com.github.truerss.base

import scala.util.Either

trait BaseContentReader {
  self: Priority with UrlMatcher with ConfigProvider with ContentType =>

  import Errors.Error
  import ContentTypeParam._

  type Response = Either[Error, Option[String]]

  /** if ContentTypeParam.URL, need pass into @content method URL, otherwise html content of page
    */
  val contentTypeParam: ContentTypeParam = ContentTypeParam.URL

  final def needUrl: Boolean = contentTypeParam match {
    case ContentTypeParam.URL  => true
    case ContentTypeParam.HTML => false
  }

  /** Extract content for given title
    */
  def content(urlOrContent: ContentTypeParam.RequestParam): Response
}
