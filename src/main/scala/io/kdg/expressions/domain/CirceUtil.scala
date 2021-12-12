package io.kdg.expressions.domain

object CirceUtil {
  def circeConfiguration(snakeCaseConstructorNameToDiscriminator: Map[String, String]): io.circe.generic.extras.Configuration =
    io.circe.generic.extras.Configuration.default.withSnakeCaseConstructorNames
      .withDiscriminator("type")
      .copy(transformConstructorNames = name => snakeCaseConstructorNameToDiscriminator.getOrElse(name, name))
}
