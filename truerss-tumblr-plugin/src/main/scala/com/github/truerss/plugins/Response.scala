package com.github.truerss.plugins


/*
text, quote, link, answer, video, audio, photo, chat
 */

object TumblrModel {

  import com.tumblr.jumblr.types.{Post => P, Dialogue => D,
  TextPost => TP, QuotePost => QP, LinkPost => LP, AnswerPost => AP,
  VideoPost => VP, AudioPost => AuP, PhotoPost => PP, ChatPost => CP}

  sealed trait PostType
  case object Text extends PostType
  case object Quote extends PostType
  case object Link extends PostType
  case object Answer extends PostType
  case object Video extends PostType
  case object Audio extends PostType
  case object Photo extends PostType
  case object Chat extends PostType


  sealed trait PostFormat
  case object Html extends PostFormat
  case object Markdown extends PostFormat

  sealed trait Post {
    def html: String
  }

  case class TextPost(title: String,
                      body: String) extends Post {
    override def html = {
      s"<h3>$title</h3><p>$body</p>"
    }
  }

  case class Size(width: Int, height: Int, url: String)
  case class Image(caption: Option[String], alt_sizes: Vector[Size])

  case class PhotoPost(
                        photos: Vector[Image],
                        caption: String
                      ) extends Post {
    override def html = {
      val p = photos.map { photo =>
        val sz = photo.alt_sizes.head
        s"""<img src=${sz.url} width='${sz.width}' height='${sz.height}' />"""
      }.mkString("<br/>")
      s"""
        <h3>${caption}</h3>
        <div>${p}</div>
      """.stripMargin
    }
  }

  case class QuotePost(
                        text: String,
                        source: String
                      ) extends Post {
    override def html = {
      s"<blockquote>${text}</blockquote><city>${source}</city>"
    }
  }
  case class LinkPost(
                       title: String,
                       url: String,
                       description: String
                     ) extends Post {
    override def html = {
      s"<h3><a href='$url'>$title</a></h3><p>$description</p>"
    }
  }

  case class Dialogue(name: String, label: String, phrase: String)

  case class ChatPost(
                       title: String,
                       body: String,
                       dialogue: Vector[Dialogue]
                     ) extends Post {
    override def html = {
      s"<h3>$title</h3><p>$body</p>"
    }
  }


  case class AudioPost(
                        caption: String,
                        player: String
                      ) extends Post {
    override def html = {
      s"<h3>$caption</h3><div>$player</div>"
    }
  }

  case class Player(width: Int, embed_code: String)

  case class VideoPost(
                        caption: String,
                        player: Vector[Player]
                      ) extends Post {
    override def html = {
      val p = player.maxBy(_.width).embed_code
      s"<h3>$caption</h3><div>$p</div>"
    }
  }

  case class AnswerPost(
                         asking_name: String,
                         asking_url: String,
                         question: String,
                         answer: String
                       ) extends Post {
    override def html = {
      s"<p><span>$asking_name</span><p>$question</p></p><div>$answer</div>"
    }
  }


  object Converter {
    import scala.collection.JavaConversions._
    def convert(x: P): Post = {
      //text, quote, link, answer, video, audio, photo, chat
      x.getType.toLowerCase match {
        case "text" =>
          val t = x.asInstanceOf[TP]
          TextPost(t.getTitle, t.getBody)

        case "quote" =>
          val t = x.asInstanceOf[QP]
          QuotePost(t.getText, t.getSource)

        case "link" =>
          val t = x.asInstanceOf[LP]
          LinkPost(t.getTitle, t.getLinkUrl, t.getDescription)

        case "answer" =>
          val t = x.asInstanceOf[AP]
          AnswerPost(t.getAskingName, t.getAskingUrl, t.getQuestion, t.getAnswer)

        case "video" =>
          val t = x.asInstanceOf[VP]
          val v = t.getVideos.map(v => Player(v.getWidth, v.getEmbedCode)).toVector
          VideoPost(t.getCaption, v)

        case "audio" =>
          val t = x.asInstanceOf[AuP]
          AudioPost(t.getCaption, t.getEmbedCode)

        case "photo" =>
          val t = x.asInstanceOf[PP]
          val p = t.getPhotos.map { x => Image(Some(t.getCaption),
            x.getSizes.map(r => Size(r.getWidth, r.getHeight, r.getUrl)).toVector)}
          .toVector
          PhotoPost(p, t.getCaption)

        case "chat" =>
          val t = x.asInstanceOf[CP]
          val d = t.getDialogue.map(x => Dialogue(x.getName, x.getLabel, x.getPhrase)).toVector
          ChatPost(t.getTitle, t.getBody, d)
      }
    }
  }


}



