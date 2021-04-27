package com.github.truerss.plugins

import java.net.URL

import com.github.truerss.base.ContentTypeParam.{HtmlRequest, RequestParam, UrlRequest}
import com.github.truerss.base._
import com.typesafe.config.{Config, ConfigFactory}
import org.jsoup.Jsoup

import scala.util.control.Exception._

class RedditImageViewerPlugin(config: Config = ConfigFactory.empty())
  extends BaseContentPlugin(config) {

  override val pluginName = "RedditImageViewerPlugin"
  override val author = "fntz <mike.fch1@gmail.com>"
  override val about = "See images from imgur or gfycat in feed"
  override val version = "0.0.1"
  override val contentTypeParam = ContentTypeParam.HTML
  override val priority = 10

  override val contentType = Image

  /**
    * Default setting:
    * RedditImageViewerPlugin {
    *  r = [gifs, diy, EducationalGifs]
    * }
    */

  private val defaultConfig = ConfigFactory.load(getClass.getClassLoader, "default")
    .getConfig(pluginName)

  private val need = catching(classOf[Exception]) either config.getConfig(pluginName).withFallback(defaultConfig) fold(
    _ => defaultConfig,
    identity
  )

  private val configKey = "r"

  private val baseHost = "www.reddit.com"
  private val sources = need.getAnyRefList(configKey).toArray.mkString("|")

  private val rx = ("""(?i)\/r\/("""+sources+""")\/comments\/.+""").r

  override def matchUrl(url: URL): Boolean = {
    if (url.getHost == baseHost) {
      rx.findFirstIn(url.toString).isDefined
    } else {
      false
    }
  }

  // skip albums just now
  override def content(urlOrContent: RequestParam): Response = {
    urlOrContent match {
      case UrlRequest(_) => Left(Errors.UnexpectedError("Pass content please"))

      case HtmlRequest(content) =>
        // imgur, gifcat
        val doc = Jsoup.parse(content)
        val top = doc.select("#siteTable")
        val tm = top.select(".thumbnail")
        if (tm.isEmpty) {
          Right(Some(top.html()))
        } else {
          val href = tm.first().attr("href")
          if (href.contains("imgur.com")) {
            catching(classOf[Exception]) either imgurise(href) fold (
              err => {
                Left(Errors.UnexpectedError(err.getMessage))
              },
              result => {
                Right(Some(result))
              }
            )
          }
          else if (href.contains("gfycat.com")) {
            catching(classOf[Exception]) either gfycatise(href) fold (
              err => {
                Left(Errors.UnexpectedError(err.getMessage))
              },
              result => {
                Right(Some(result))
              }
            )
          }
          else {
            if (href.startsWith("/")) {
              Right(Some(top.select(".usertext-body").html()))
            } else {
              Right(Some(s"<img src='$href'>"))
            }
          }
        }
    }
  }

  private def gfycatise(url: String): String = {
    Jsoup.connect(url).get().body().html()
  }

  // check - image
  // check - gifv|webm
  // check album
  // check gallery -> http://imgur.com/gallery/UADVg
  // check if it's image -> http://imgur.com/ha9zdcp
  private def imgurise(url: String): String = {
    if (url.endsWith(".gif") ||
        url.endsWith(".png") ||
        url.endsWith(".jpg") ||
        url.endsWith(".jpeg")
    ) {
      s"<img src='$url'>"
    } else {
      val body = Jsoup.connect(url).get().body()
      if (url.contains("/gallery/")) {
        body.select(".post-image").html()
      }
      else if (url.contains("/a/")) {
        body.select(".post-images").html()
      }
      else if (!new URL(url).getPath.contains(".")) {
        body.select(".post-image").html()
      }
      else {
        body.html().replaceAll("//", "http://")
      }
    }
  }

}





