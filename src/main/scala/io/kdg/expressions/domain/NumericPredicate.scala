package io.kdg.expressions.domain

import derevo.cats.show
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration
import sttp.tapir.Schema

import scala.reflect.runtime.universe.typeOf

@derive(show) sealed trait NumericPredicate { def `type`: String }
case object NumericPredicate {
  @derive(show, decoder, encoder) case class EQ(values: Seq[NumericString])                extends NumericPredicate { val `type`: String = EQType }
  @derive(show, decoder, encoder) case class NotEQ(values: Seq[NumericString])             extends NumericPredicate { val `type`: String = NotEQType }
  @derive(show, decoder, encoder) case class GT(value: NumericString)                      extends NumericPredicate { val `type`: String = GTType }
  @derive(show, decoder, encoder) case class GE(value: NumericString)                      extends NumericPredicate { val `type`: String = GEType }
  @derive(show, decoder, encoder) case class LT(value: NumericString)                      extends NumericPredicate { val `type`: String = LTType }
  @derive(show, decoder, encoder) case class LE(value: NumericString)                      extends NumericPredicate { val `type`: String = LEType }
  @derive(show, decoder, encoder) case class Between(ge: NumericString, lt: NumericString) extends NumericPredicate { val `type`: String = BetweenType }
  @derive(show, decoder, encoder) case class Exists()                                      extends NumericPredicate { val `type`: String = ExistsType }
  @derive(show, decoder, encoder) case class NotExist()                                    extends NumericPredicate { val `type`: String = NotExistType }

  private lazy val EQType: String       = "Equals"
  private lazy val NotEQType: String    = "Does Not Equal"
  private lazy val GTType: String       = "Greater Than(>)"
  private lazy val GEType: String       = "Greater Than or Equal To(>=)"
  private lazy val LTType: String       = "Less Than(<)"
  private lazy val LEType: String       = "Less Than or Equal To(<=)"
  private lazy val BetweenType: String  = "Between"
  private lazy val ExistsType: String   = "Exists"
  private lazy val NotExistType: String = "Does Not Exist"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[NumericPredicate] = Schema.oneOfUsingField[NumericPredicate, String](_.`type`, identity)(
    EQType       -> Schema.derived[EQ],
    NotEQType    -> Schema.derived[NotEQ],
    GTType       -> Schema.derived[GT],
    GEType       -> Schema.derived[GE],
    LTType       -> Schema.derived[LT],
    LEType       -> Schema.derived[LE],
    BetweenType  -> Schema.derived[Between],
    ExistsType   -> Schema.derived[Exists],
    NotExistType -> Schema.derived[NotExist]
  )

  private val circeDiscriminators = Map(
    typeOf[EQ].typeSymbol.name.toString       -> EQType,
    typeOf[NotEQ].typeSymbol.name.toString    -> NotEQType,
    typeOf[GT].typeSymbol.name.toString       -> GTType,
    typeOf[GE].typeSymbol.name.toString       -> GEType,
    typeOf[LT].typeSymbol.name.toString       -> LTType,
    typeOf[LE].typeSymbol.name.toString       -> LEType,
    typeOf[Between].typeSymbol.name.toString  -> BetweenType,
    typeOf[Exists].typeSymbol.name.toString   -> ExistsType,
    typeOf[NotExist].typeSymbol.name.toString -> NotExistType
  )
  implicit val _circeConfiguration: Configuration  = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[NumericPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[NumericPredicate] = deriveConfiguredEncoder
}
