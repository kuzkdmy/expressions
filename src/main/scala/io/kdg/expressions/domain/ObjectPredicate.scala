package io.kdg.expressions.domain

import derevo.cats.show
import derevo.circe._
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration
import sttp.tapir.Schema

import scala.reflect.runtime.universe.typeOf

@derive(show) sealed trait ObjectPredicate { def `type`: String }
case object ObjectPredicate {
  @derive(show, decoder, encoder) case class Exists()       extends ObjectPredicate { val `type`: String = ExistsType }
  @derive(show, decoder, encoder) case class DoesNotExist() extends ObjectPredicate { val `type`: String = DoesNotExistType }

  private lazy val ExistsType: String       = "Exists"
  private lazy val DoesNotExistType: String = "Does Not Exist"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[ObjectPredicate] = Schema.oneOfUsingField[ObjectPredicate, String](_.`type`, identity)(
    ExistsType       -> Schema.derived[Exists],
    DoesNotExistType -> Schema.derived[DoesNotExist]
  )

  private val circeDiscriminators = Map(
    typeOf[Exists].typeSymbol.name.toString       -> ExistsType,
    typeOf[DoesNotExist].typeSymbol.name.toString -> DoesNotExistType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[ObjectPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[ObjectPredicate] = deriveConfiguredEncoder
}
