package com.github.truerss.plugins

import com.github.truerss.base.ContentTypeParam.{HtmlRequest, RequestParam, UrlRequest}
import org.jsoup.Jsoup

import scala.util.control.Exception._
import com.github.truerss.base.{BaseContentPlugin, ContentTypeParam, Errors, Text}
import com.typesafe.config.{Config, ConfigFactory}

import java.net.URI
import scala.jdk.CollectionConverters._

class StackoverflowPlugin(config: Config = ConfigFactory.empty())
    extends BaseContentPlugin(config) {

  private val onlyQuestion = "onlyQuestion"
  private val onlyBestAnswer = "onlyBestAnswer"
  private val allAnswers = "allAnswers"

  /** Default setting:
    *
    * StackoverflowPlugin { onlyQuestion = false onlyBestAnswer = false allAnswers = true }
    */

  override val pluginName = "StackoverflowPlugin"
  override val author = "fntz <mike.fch1@gmail.com>"
  override val about = "Read stackexchange questions and answers"
  override val version = "1.1.0"
  override val contentTypeParam = ContentTypeParam.HTML
  override val priority = 10

  val defaultConfig = ConfigFactory
    .load(getClass.getClassLoader, "default")
    .getConfig(pluginName)

  val need = catching(classOf[Exception]) either config
    .getConfig(pluginName)
    .withFallback(defaultConfig) fold (
    _ => defaultConfig,
    c => c
  )

  override val contentType = Text

  val rx = """/questions/[0-9]+/.+""".r

  override def matchUrl(url: URI): Boolean = {
    if (sites.exists(url.toString.contains)) {
      rx.findFirstIn(url.toString).isDefined
    } else {
      false
    }
  }

  private val qSelector = ".question"
  private val txtSelect = ".post-text"
  private val answersSelector = "#answers"
  private val answerSelector = ".answer"
  private val accepted = "accepted-answer"
  private val acceptedSelector = s".$accepted"

  private val acceptStyle = "'border:1px solid #3ADF00; padding:1%;'"
  private val simpleStyle = "'border:1px solid #000000; padding:1%;'"

  private val wrap = (html: String, style: String) => s"<div style=$style>$html</div>"

  private val br = "<br/>"
  private val hr = "<hr/>"

  override def content(urlOrContent: RequestParam): Response = {
    urlOrContent match {
      case UrlRequest(_) => Left(Errors.UnexpectedError("Pass content please"))
      case HtmlRequest(content) =>
        val doc = Jsoup.parse(content)
        val result = if (need.getBoolean(onlyQuestion)) {
          doc.select(s"$qSelector $txtSelect").html()
        } else {
          val q = doc.select(s"$qSelector $txtSelect")
          val a = doc.select(s"$answersSelector $answerSelector")
          val question = s"${q.html()}$hr$br"
          if (need.getBoolean(onlyBestAnswer)) {
            val best = a.select(acceptedSelector)
            if (best.isEmpty) {
              question
            } else {
              s"$question${wrap(best.select(txtSelect).html(), acceptStyle)}"
            }
          } else {
            if (need.getBoolean(allAnswers)) {
              val answers = a.asScala
                .map { a =>
                  val style = if (a.hasClass(accepted)) {
                    acceptStyle
                  } else {
                    simpleStyle
                  }
                  wrap(a.select(txtSelect).html(), style)
                }
                .mkString(br)
              s"$question$answers"
            } else {
              s"$question"
            }
          }
        }
        Right(Some(result))
    }
  }

  val sites =
    """stackoverflow.com
      |serverfault.com
      |superuser.com
      |meta.stackexchange.com
      |webapps.stackexchange.com
      |gaming.stackexchange.com
      |webmasters.stackexchange.com
      |cooking.stackexchange.com
      |gamedev.stackexchange.com
      |photo.stackexchange.com
      |stats.stackexchange.com
      |math.stackexchange.com
      |diy.stackexchange.com
      |gis.stackexchange.com
      |tex.stackexchange.com
      |askubuntu.com
      |money.stackexchange.com
      |english.stackexchange.com
      |stackapps.com
      |ux.stackexchange.com
      |unix.stackexchange.com
      |wordpress.stackexchange.com
      |cstheory.stackexchange.com
      |apple.stackexchange.com
      |rpg.stackexchange.com
      |bicycles.stackexchange.com
      |programmers.stackexchange.com
      |electronics.stackexchange.com
      |android.stackexchange.com
      |boardgames.stackexchange.com
      |physics.stackexchange.com
      |homebrew.stackexchange.com
      |security.stackexchange.com
      |writers.stackexchange.com
      |video.stackexchange.com
      |graphicdesign.stackexchange.com
      |dba.stackexchange.com
      |scifi.stackexchange.com
      |codereview.stackexchange.com
      |codegolf.stackexchange.com
      |quant.stackexchange.com
      |pm.stackexchange.com
      |skeptics.stackexchange.com
      |fitness.stackexchange.com
      |drupal.stackexchange.com
      |mechanics.stackexchange.com
      |parenting.stackexchange.com
      |sharepoint.stackexchange.com
      |music.stackexchange.com
      |sqa.stackexchange.com
      |judaism.stackexchange.com
      |german.stackexchange.com
      |japanese.stackexchange.com
      |philosophy.stackexchange.com
      |gardening.stackexchange.com
      |travel.stackexchange.com
      |productivity.stackexchange.com
      |crypto.stackexchange.com
      |dsp.stackexchange.com
      |french.stackexchange.com
      |christianity.stackexchange.com
      |bitcoin.stackexchange.com
      |linguistics.stackexchange.com
      |hermeneutics.stackexchange.com
      |history.stackexchange.com
      |bricks.stackexchange.com
      |spanish.stackexchange.com
      |scicomp.stackexchange.com
      |movies.stackexchange.com
      |chinese.stackexchange.com
      |biology.stackexchange.com
      |poker.stackexchange.com
      |mathematica.stackexchange.com
      |cogsci.stackexchange.com
      |outdoors.stackexchange.com
      |martialarts.stackexchange.com
      |sports.stackexchange.com
      |academia.stackexchange.com
      |cs.stackexchange.com
      |workplace.stackexchange.com
      |windowsphone.stackexchange.com
      |chemistry.stackexchange.com
      |chess.stackexchange.com
      |raspberrypi.stackexchange.com
      |russian.stackexchange.com
      |islam.stackexchange.com
      |salesforce.stackexchange.com
      |patents.stackexchange.com
      |genealogy.stackexchange.com
      |robotics.stackexchange.com
      |expressionengine.stackexchange.com
      |politics.stackexchange.com
      |anime.stackexchange.com
      |magento.stackexchange.com
      |ell.stackexchange.com
      |sustainability.stackexchange.com
      |tridion.stackexchange.com
      |reverseengineering.stackexchange.com
      |networkengineering.stackexchange.com
      |opendata.stackexchange.com
      |freelancing.stackexchange.com
      |blender.stackexchange.com
      |mathoverflow.net
      |space.stackexchange.com
      |sound.stackexchange.com
      |astronomy.stackexchange.com
      |tor.stackexchange.com
      |pets.stackexchange.com
      |ham.stackexchange.com
      |italian.stackexchange.com
      |pt.stackoverflow.com
      |aviation.stackexchange.com
      |ebooks.stackexchange.com
      |beer.stackexchange.com
      |softwarerecs.stackexchange.com
      |arduino.stackexchange.com
      |expatriates.stackexchange.com
      |matheducators.stackexchange.com
      |earthscience.stackexchange.com
      |joomla.stackexchange.com
      |datascience.stackexchange.com
      |puzzling.stackexchange.com
      |craftcms.stackexchange.com
      |buddhism.stackexchange.com
      |hinduism.stackexchange.com
      |communitybuilding.stackexchange.com
      |startups.stackexchange.com
      |worldbuilding.stackexchange.com
      |ja.stackoverflow.com
      |emacs.stackexchange.com
      |hsm.stackexchange.com
      |economics.stackexchange.com
      |lifehacks.stackexchange.com
      |engineering.stackexchange.com
      |coffee.stackexchange.com
      |vi.stackexchange.com
      |musicfans.stackexchange.com
      |woodworking.stackexchange.com
      |civicrm.stackexchange.com
      |health.stackexchange.com
      |ru.stackoverflow.com
      |rus.stackexchange.com
      |mythology.stackexchange.com
      |law.stackexchange.com
      |opensource.stackexchange.com
      |elementaryos.stackexchange.com
      |portuguese.stackexchange.com
      |computergraphics.stackexchange.com
      |hardwarerecs.stackexchange.com
    """.stripMargin.split("\n")

}
