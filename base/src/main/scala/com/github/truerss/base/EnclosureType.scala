package com.github.truerss.base

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

sealed trait EnclosureType extends EnumEntry

object EnclosureType extends Enum[EnclosureType] with PlayJsonEnum[EnclosureType] {

  sealed trait Audio extends EnclosureType
  sealed trait Video extends EnclosureType

  object Audio {
    case object `audio/mpeg` extends Audio
    case object `audio/x-m4a` extends Audio
  }

  object Video {
    case object `video/mp4` extends Video
    case object `video/quicktime` extends Video
    case object `video/wmv` extends Video
  }

  val values: IndexedSeq[EnclosureType] = findValues
}
