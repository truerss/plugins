package com.github.truerss.plugins

import com.github.truerss.base.ContentTypeParam.RequestParam
import org.jsoup.Jsoup

/**
  * Created by mike on 5.1.16.
  */
object Boot extends App {

  val p = new RedditImageViewerPlugin()

  val url = "https://www.reddit.com/r/gifs/comments/3zh4qt/owl_floating_in_the_sink_bath_time/"

  import scalaj.http._

  val response = scalaj.http.Http(url)
    .option(HttpOptions.connTimeout(6000))
    .option(HttpOptions.readTimeout(6000))
    .option(HttpOptions.allowUnsafeSSL)
    .option(HttpOptions.followRedirects(true))
    .header("Accept", "*/*")
    .compress(true)
    .asString

  val html = Jsoup.parse(response.body).body().html()

  val x = p.content(RequestParam.apply(html))
  println(x)

}
