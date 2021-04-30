package com.github.truerss.base

import java.util.Date

case class Entry(
  url: String,
  title: String,
  author: String,
  publishedDate: Date,
  description: Option[String],
  content: Option[String],
  forceUpdate: Boolean = false,
  enclosure: Option[Enclosure]
)
