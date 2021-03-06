package io.kdg.expressions.config

import zio.{Has, TaskLayer, ZIO}

import scala.util.control.NoStackTrace

case class MainConfig(server: AppHttpServerConf)
case class AppHttpServerConf(port: Int)

object MainConfig {
  import pureconfig._
  import pureconfig.generic.auto._
  val live: TaskLayer[Has[MainConfig]] = {
    ZIO
      .fromEither(ConfigSource.default.load[MainConfig])
      .foldM(
        err => ZIO.fail(new IllegalArgumentException(s"config error: $err") with NoStackTrace),
        v => ZIO(v)
      )
      .toLayer
  }
}
