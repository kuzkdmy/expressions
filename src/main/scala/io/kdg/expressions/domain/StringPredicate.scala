package io.kdg.expressions.domain

import derevo.circe._
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration

import scala.reflect.runtime.universe.typeOf

sealed trait StringPredicate
case object StringPredicate {
  @derive(decoder, encoder) case class Contains(values: Seq[String])         extends StringPredicate
  @derive(decoder, encoder) case class DoesNotContain(values: Seq[String])   extends StringPredicate
  @derive(decoder, encoder) case class Equals(values: Seq[String])           extends StringPredicate
  @derive(decoder, encoder) case class DoesNotEqual(values: Seq[String])     extends StringPredicate
  @derive(decoder, encoder) case class BeginsWith(values: Seq[String])       extends StringPredicate
  @derive(decoder, encoder) case class DoesNotBeginWith(values: Seq[String]) extends StringPredicate
  @derive(decoder, encoder) case class EndsWith(values: Seq[String])         extends StringPredicate
  @derive(decoder, encoder) case class DoesNotEndWith(values: Seq[String])   extends StringPredicate
  @derive(decoder, encoder) case class Exists()                              extends StringPredicate
  @derive(decoder, encoder) case class DoesNotExist()                        extends StringPredicate

  private val circeDiscriminators = Map(
    typeOf[Contains].typeSymbol.name.toString         -> "Contains",
    typeOf[DoesNotContain].typeSymbol.name.toString   -> "Does Not Contain",
    typeOf[Equals].typeSymbol.name.toString           -> "Equals",
    typeOf[DoesNotEqual].typeSymbol.name.toString     -> "Does Not Equal",
    typeOf[BeginsWith].typeSymbol.name.toString       -> "Begins With",
    typeOf[DoesNotBeginWith].typeSymbol.name.toString -> "Does Not Begin With",
    typeOf[EndsWith].typeSymbol.name.toString         -> "Ends With",
    typeOf[DoesNotEndWith].typeSymbol.name.toString   -> "Does Not End With",
    typeOf[Exists].typeSymbol.name.toString           -> "Exists",
    typeOf[DoesNotExist].typeSymbol.name.toString     -> "Does Not Exist"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[StringPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[StringPredicate] = deriveConfiguredEncoder
}
