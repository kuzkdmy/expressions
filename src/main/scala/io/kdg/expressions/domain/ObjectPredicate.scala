package io.kdg.expressions.domain

import derevo.circe._
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration

import scala.reflect.runtime.universe.typeOf

sealed trait ObjectPredicate
case object ObjectPredicate {
  @derive(decoder, encoder) case class Exists()       extends ObjectPredicate
  @derive(decoder, encoder) case class DoesNotExist() extends ObjectPredicate

  private val circeDiscriminators = Map(
    typeOf[Exists].typeSymbol.name.toString       -> "Exists",
    typeOf[DoesNotExist].typeSymbol.name.toString -> "Does Not Exist"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[ObjectPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[ObjectPredicate] = deriveConfiguredEncoder
}
