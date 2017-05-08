package com.github.truerss.base

sealed trait BaseType
case object Text extends BaseType
case object Image extends BaseType
case object Video extends BaseType
case object Unknown extends BaseType

trait ContentType {
  val contentType: BaseType
}
