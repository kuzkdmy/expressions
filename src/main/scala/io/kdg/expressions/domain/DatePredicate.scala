package io.kdg.expressions.domain

import cats.Show
import derevo.cats.show
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.{deriveConfiguredDecoder, deriveConfiguredEncoder}
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration
import sttp.tapir.Schema
import sttp.tapir.derevo.schema

import java.time.LocalDate
import scala.reflect.runtime.universe.typeOf

@derive(show) sealed trait DatePredicate { def `type`: String }
case object DatePredicate {
  implicit val localDateShow: Show[LocalDate] = _.toString
  @derive(show, decoder, encoder, schema) case class Yesterday()                           extends DatePredicate { val `type`: String = YesterdayType }
  @derive(show, decoder, encoder, schema) case class Today()                               extends DatePredicate { val `type`: String = TodayType }
  @derive(show, decoder, encoder, schema) case class Tomorrow()                            extends DatePredicate { val `type`: String = TomorrowType }
  @derive(show, decoder, encoder, schema) case class Equals(values: Seq[LocalDate])        extends DatePredicate { val `type`: String = EqualsType }
  @derive(show, decoder, encoder, schema) case class DoesNotEqual(values: Seq[LocalDate])  extends DatePredicate { val `type`: String = DoesNotEqualType }
  @derive(show, decoder, encoder, schema) case class Before(value: LocalDate)              extends DatePredicate { val `type`: String = BeforeType }
  @derive(show, decoder, encoder, schema) case class After(value: LocalDate)               extends DatePredicate { val `type`: String = AfterType }
  @derive(show, decoder, encoder, schema) case class Between(ge: LocalDate, lt: LocalDate) extends DatePredicate { val `type`: String = BetweenType }
  @derive(show, decoder, encoder, schema) case class InThePast()                           extends DatePredicate { val `type`: String = InThePastType }
  @derive(show, decoder, encoder, schema) case class InThePastBefore(value: LocalDate)     extends DatePredicate { val `type`: String = InThePastBeforeType }
  @derive(show, decoder, encoder, schema) case class InTheFuture()                         extends DatePredicate { val `type`: String = InTheFutureType }
  @derive(show, decoder, encoder, schema) case class InTheFutureAfter(value: LocalDate)    extends DatePredicate { val `type`: String = InTheFutureAfterType }
  @derive(show, decoder, encoder, schema) case class Exists()                              extends DatePredicate { val `type`: String = ExistsType }
  @derive(show, decoder, encoder, schema) case class DoesNotExist()                        extends DatePredicate { val `type`: String = DoesNotExistType }

  private lazy val YesterdayType: String        = "Yesterday"
  private lazy val TodayType: String            = "Today"
  private lazy val TomorrowType: String         = "Tomorrow"
  private lazy val EqualsType: String           = "Equals"
  private lazy val DoesNotEqualType: String     = "Does Not Equal"
  private lazy val BeforeType: String           = "Before"
  private lazy val AfterType: String            = "After"
  private lazy val BetweenType: String          = "Between"
  private lazy val InThePastType: String        = "In the Past"
  private lazy val InThePastBeforeType: String  = "In the Past Before"
  private lazy val InTheFutureType: String      = "In the Future"
  private lazy val InTheFutureAfterType: String = "In the Future After"
  private lazy val ExistsType: String           = "Exists"
  private lazy val DoesNotExistType: String     = "Does Not Exist"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[DatePredicate] = Schema.oneOfUsingField[DatePredicate, String](_.`type`, identity)(
    YesterdayType        -> Schema.derived[Yesterday],
    TodayType            -> Schema.derived[Today],
    TomorrowType         -> Schema.derived[Tomorrow],
    EqualsType           -> Schema.derived[Equals],
    DoesNotEqualType     -> Schema.derived[DoesNotEqual],
    BeforeType           -> Schema.derived[Before],
    AfterType            -> Schema.derived[After],
    BetweenType          -> Schema.derived[Between],
    InThePastType        -> Schema.derived[InThePast],
    InThePastBeforeType  -> Schema.derived[InThePastBefore],
    InTheFutureType      -> Schema.derived[InTheFuture],
    InTheFutureAfterType -> Schema.derived[InTheFutureAfter],
    ExistsType           -> Schema.derived[Exists],
    DoesNotExistType     -> Schema.derived[DoesNotExist]
  )

  private val circeDiscriminators = Map(
    typeOf[Yesterday].typeSymbol.name.toString        -> YesterdayType,
    typeOf[Today].typeSymbol.name.toString            -> TodayType,
    typeOf[Tomorrow].typeSymbol.name.toString         -> TomorrowType,
    typeOf[Equals].typeSymbol.name.toString           -> EqualsType,
    typeOf[DoesNotEqual].typeSymbol.name.toString     -> DoesNotEqualType,
    typeOf[Before].typeSymbol.name.toString           -> BeforeType,
    typeOf[After].typeSymbol.name.toString            -> AfterType,
    typeOf[Between].typeSymbol.name.toString          -> BetweenType,
    typeOf[InThePast].typeSymbol.name.toString        -> InThePastType,
    typeOf[InThePastBefore].typeSymbol.name.toString  -> InThePastBeforeType,
    typeOf[InTheFuture].typeSymbol.name.toString      -> InTheFutureType,
    typeOf[InTheFutureAfter].typeSymbol.name.toString -> InTheFutureAfterType,
    typeOf[Exists].typeSymbol.name.toString           -> ExistsType,
    typeOf[DoesNotExist].typeSymbol.name.toString     -> DoesNotExistType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[DatePredicate]   = deriveConfiguredDecoder
  implicit val _encoder: Encoder[DatePredicate]   = deriveConfiguredEncoder
}
