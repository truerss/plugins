package com.github.truerss.plugins

import java.net.URL

import com.github.truerss.base.ContentTypeParam.{HtmlRequest, RequestParam, UrlRequest}
import com.github.truerss.base.{BaseContentPlugin, ContentTypeParam, Errors, Unknown}
import com.github.truerss.plugins.TumblrModel.Converter
import com.tumblr.jumblr.JumblrClient
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.control.Exception._

class TumblrPlugin(config: Config = ConfigFactory.empty) extends BaseContentPlugin(config) {

  override val pluginName = "TumblrPlugin"
  override val author = "fntz <mike.fch1@gmail.com>"
  override val about = "Get image or content from tumblr post"
  override val version = "1.0.1"

  override val contentType = Unknown
  override val priority = 10
  override val contentTypeParam = ContentTypeParam.URL

  private val apiKeyPath = "consumer_key"

  val need = catching(classOf[Exception]) either config
    .getConfig(pluginName)
    .withFallback(ConfigFactory.empty()) fold (
    _ => ConfigFactory.empty(),
    c => c
  )

  private val links = Vector("tumblr.com")

  override def matchUrl(url: URL) = {
    val host = url.getHost
    if (links.exists(x => host.endsWith(x)) && need.hasPath(apiKeyPath)) {
      true
    } else {
      false
    }
  }

  override def content(urlOrContent: RequestParam): Response = {
    urlOrContent match {
      case UrlRequest(url) =>
        if (need.hasPath(apiKeyPath)) {
          val apiKey = need.getString(apiKeyPath)
          val blog = url.getHost
          val postId = url.getPath.split("/").filter(_.nonEmpty)(1).toLong

          val jc = new JumblrClient(apiKey, apiKey)
          catching(classOf[Exception]) either Converter.convert(jc.blogPost(blog, postId)) fold (
            err => Left(Errors.UnexpectedError(err.getMessage)),
            normal => Right(Some(s"<div>${normal.html}</div>"))
          )
        } else {
          Left(Errors.UnexpectedError("api key required"))
        }
      case HtmlRequest(_) =>
        Left(Errors.UnexpectedError("Pass url instead of content"))
    }
  }

}
