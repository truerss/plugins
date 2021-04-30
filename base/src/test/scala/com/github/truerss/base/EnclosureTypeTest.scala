package com.github.truerss.base

import utest._

object EnclosureTypeTest extends TestSuite {
  override val tests: Tests = Tests {
    test("decode audio formats") {
      test("audio/mpeg") {
        assert(EnclosureType.withName("audio/mpeg") == EnclosureType.Audio.`audio/mpeg`)
      }
      test("audio/x-m4a") {
        assert(EnclosureType.withName("audio/x-m4a") == EnclosureType.Audio.`audio/x-m4a`)
      }
    }

    test("decode video formats") {
      test("video/mp4") {
        assert(EnclosureType.withName("video/mp4") == EnclosureType.Video.`video/mp4`)
      }
      test("video/quicktime") {
        assert(EnclosureType.withName("video/quicktime") == EnclosureType.Video.`video/quicktime`)
      }
      test("video/wmv") {
        assert(EnclosureType.withName("video/wmv") == EnclosureType.Video.`video/wmv`)
      }
    }

    test("fail to decode non-existing formats") {
      test("audio/m4a") {
        assert(EnclosureType.withNameEither("audio/m4a").isLeft)
      }
      test("video/mkv") {
        assert(EnclosureType.withNameEither("video/mkv").isLeft)
      }
    }
  }
}
