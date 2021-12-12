package io.kdg.expressions.domain

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration

import java.time.LocalDate
import scala.reflect.runtime.universe.typeOf

sealed trait DatePredicate
case object DatePredicate {
  @derive(decoder, encoder) case class Yesterday()                           extends DatePredicate
  @derive(decoder, encoder) case class Today()                               extends DatePredicate
  @derive(decoder, encoder) case class Tomorrow()                            extends DatePredicate
  @derive(decoder, encoder) case class Equals(values: Seq[LocalDate])        extends DatePredicate
  @derive(decoder, encoder) case class DoesNotEqual(values: Seq[LocalDate])  extends DatePredicate
  @derive(decoder, encoder) case class Before(value: LocalDate)              extends DatePredicate
  @derive(decoder, encoder) case class After(value: LocalDate)               extends DatePredicate
  @derive(decoder, encoder) case class Between(ge: LocalDate, lt: LocalDate) extends DatePredicate
  @derive(decoder, encoder) case class InThePast()                           extends DatePredicate
  @derive(decoder, encoder) case class InThePastBefore(value: LocalDate)     extends DatePredicate
  @derive(decoder, encoder) case class InTheFuture()                         extends DatePredicate
  @derive(decoder, encoder) case class InTheFutureAfter(value: LocalDate)    extends DatePredicate
  @derive(decoder, encoder) case class Exists()                              extends DatePredicate
  @derive(decoder, encoder) case class DoesNotExist()                        extends DatePredicate

  private val circeDiscriminators = Map(
    typeOf[Yesterday].typeSymbol.name.toString        -> "Yesterday",
    typeOf[Today].typeSymbol.name.toString            -> "Today",
    typeOf[Tomorrow].typeSymbol.name.toString         -> "Tomorrow",
    typeOf[Equals].typeSymbol.name.toString           -> "Equals",
    typeOf[DoesNotEqual].typeSymbol.name.toString     -> "Does Not Equal",
    typeOf[Before].typeSymbol.name.toString           -> "Before",
    typeOf[After].typeSymbol.name.toString            -> "After",
    typeOf[Between].typeSymbol.name.toString          -> "Between",
    typeOf[InThePast].typeSymbol.name.toString        -> "In the Past",
    typeOf[InThePastBefore].typeSymbol.name.toString  -> "In the Past Before",
    typeOf[InTheFuture].typeSymbol.name.toString      -> "In the Future",
    typeOf[InTheFutureAfter].typeSymbol.name.toString -> "In the Future After",
    typeOf[Exists].typeSymbol.name.toString           -> "Exists",
    typeOf[DoesNotExist].typeSymbol.name.toString     -> "Does Not Exist"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[DatePredicate]   = deriveConfiguredDecoder
  implicit val _encoder: Encoder[DatePredicate]   = deriveConfiguredEncoder
}
