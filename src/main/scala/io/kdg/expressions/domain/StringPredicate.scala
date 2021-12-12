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

@derive(show) sealed trait StringPredicate { def `type`: String }
case object StringPredicate {
  @derive(show, decoder, encoder) case class Contains(values: Seq[String])         extends StringPredicate { val `type`: String = ContainsType }
  @derive(show, decoder, encoder) case class DoesNotContain(values: Seq[String])   extends StringPredicate { val `type`: String = DoesNotContainType }
  @derive(show, decoder, encoder) case class Equals(values: Seq[String])           extends StringPredicate { val `type`: String = EqualsType }
  @derive(show, decoder, encoder) case class DoesNotEqual(values: Seq[String])     extends StringPredicate { val `type`: String = DoesNotEqualType }
  @derive(show, decoder, encoder) case class BeginsWith(values: Seq[String])       extends StringPredicate { val `type`: String = BeginsWithType }
  @derive(show, decoder, encoder) case class DoesNotBeginWith(values: Seq[String]) extends StringPredicate { val `type`: String = DoesNotBeginWithType }
  @derive(show, decoder, encoder) case class EndsWith(values: Seq[String])         extends StringPredicate { val `type`: String = EndsWithType }
  @derive(show, decoder, encoder) case class DoesNotEndWith(values: Seq[String])   extends StringPredicate { val `type`: String = DoesNotEndWithType }
  @derive(show, decoder, encoder) case class Exists()                              extends StringPredicate { val `type`: String = ExistsType }
  @derive(show, decoder, encoder) case class DoesNotExist()                        extends StringPredicate { val `type`: String = DoesNotExistType }

  private lazy val ContainsType: String         = "Contains"
  private lazy val DoesNotContainType: String   = "Does Not Contain"
  private lazy val EqualsType: String           = "Equals"
  private lazy val DoesNotEqualType: String     = "Does Not Equal"
  private lazy val BeginsWithType: String       = "Begins With"
  private lazy val DoesNotBeginWithType: String = "Does Not Begin With"
  private lazy val EndsWithType: String         = "Ends With"
  private lazy val DoesNotEndWithType: String   = "Does Not End With"
  private lazy val ExistsType: String           = "Exists"
  private lazy val DoesNotExistType: String     = "Does Not Exist"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[StringPredicate] = Schema.oneOfUsingField[StringPredicate, String](_.`type`, identity)(
    ContainsType         -> Schema.derived[Contains],
    DoesNotContainType   -> Schema.derived[DoesNotContain],
    EqualsType           -> Schema.derived[Equals],
    DoesNotEqualType     -> Schema.derived[DoesNotEqual],
    BeginsWithType       -> Schema.derived[BeginsWith],
    DoesNotBeginWithType -> Schema.derived[DoesNotBeginWith],
    EndsWithType         -> Schema.derived[EndsWith],
    DoesNotEndWithType   -> Schema.derived[DoesNotEndWith],
    ExistsType           -> Schema.derived[Exists],
    DoesNotExistType     -> Schema.derived[DoesNotExist]
  )
  private val circeDiscriminators = Map(
    typeOf[Contains].typeSymbol.name.toString         -> ContainsType,
    typeOf[DoesNotContain].typeSymbol.name.toString   -> DoesNotContainType,
    typeOf[Equals].typeSymbol.name.toString           -> EqualsType,
    typeOf[DoesNotEqual].typeSymbol.name.toString     -> DoesNotEqualType,
    typeOf[BeginsWith].typeSymbol.name.toString       -> BeginsWithType,
    typeOf[DoesNotBeginWith].typeSymbol.name.toString -> DoesNotBeginWithType,
    typeOf[EndsWith].typeSymbol.name.toString         -> EndsWithType,
    typeOf[DoesNotEndWith].typeSymbol.name.toString   -> DoesNotEndWithType,
    typeOf[Exists].typeSymbol.name.toString           -> ExistsType,
    typeOf[DoesNotExist].typeSymbol.name.toString     -> DoesNotExistType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[StringPredicate] = deriveConfiguredDecoder
  implicit val _encoder: Encoder[StringPredicate] = deriveConfiguredEncoder
}
