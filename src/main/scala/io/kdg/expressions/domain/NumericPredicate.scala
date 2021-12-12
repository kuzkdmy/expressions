package io.kdg.expressions.domain

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration

import scala.reflect.runtime.universe.typeOf

sealed trait NumericPredicate
case object NumericPredicate {
  @derive(decoder, encoder) case class EQ(values: Seq[NumericString])                extends NumericPredicate
  @derive(decoder, encoder) case class NotEQ(values: Seq[NumericString])             extends NumericPredicate
  @derive(decoder, encoder) case class GT(value: NumericString)                      extends NumericPredicate
  @derive(decoder, encoder) case class GE(value: NumericString)                      extends NumericPredicate
  @derive(decoder, encoder) case class LT(value: NumericString)                      extends NumericPredicate
  @derive(decoder, encoder) case class LE(value: NumericString)                      extends NumericPredicate
  @derive(decoder, encoder) case class Between(ge: NumericString, lt: NumericString) extends NumericPredicate
  @derive(decoder, encoder) case class Exists()                                      extends NumericPredicate
  @derive(decoder, encoder) case class NotExist()                                    extends NumericPredicate

  private val circeDiscriminators = Map(
    typeOf[EQ].typeSymbol.name.toString       -> "Equals",
    typeOf[NotEQ].typeSymbol.name.toString    -> "Does Not Equal",
    typeOf[GT].typeSymbol.name.toString       -> "Greater Than(>)",
    typeOf[GE].typeSymbol.name.toString       -> "Greater Than or Equal To(>=)",
    typeOf[LT].typeSymbol.name.toString       -> "Less Than(<)",
    typeOf[LE].typeSymbol.name.toString       -> "Less Than or Equal To(<=)",
    typeOf[Between].typeSymbol.name.toString  -> "Between",
    typeOf[Exists].typeSymbol.name.toString   -> "Exists",
    typeOf[NotExist].typeSymbol.name.toString -> "Does Not Exist"
  )
  implicit val _circeConfiguration: Configuration  = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[NumericPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[NumericPredicate] = deriveConfiguredEncoder
}
