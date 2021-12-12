package io.kdg.expressions.domain

import derevo.cats.show
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration
import sttp.tapir.Schema

import scala.reflect.runtime.universe.typeOf

@derive(show) sealed trait Expr { def `type`: String }
object Expr {
  @derive(show, encoder, decoder) case class CombineExpr(conjunction: Boolean, expressions: Seq[Expr]) extends Expr { val `type`: String = CombineExprType }
  @derive(show, encoder, decoder) case class EvalExpr(obj: String, rules: Seq[ExpRule])                extends Expr { val `type`: String = EvalExprType }

  private lazy val CombineExprType: String = "combine"
  private lazy val EvalExprType: String    = "eval"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[Expr] = Schema.oneOfUsingField[Expr, String](_.`type`, identity)(
    CombineExprType -> Schema.derived[CombineExpr],
    EvalExprType    -> Schema.derived[EvalExpr]
  )
  private val circeDiscriminators = Map(
    typeOf[CombineExpr].typeSymbol.name.toString -> CombineExprType,
    typeOf[EvalExpr].typeSymbol.name.toString    -> EvalExprType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[Expr]            = deriveConfiguredDecoder
  implicit val _encoder: Encoder[Expr]            = deriveConfiguredEncoder
}

@derive(show) sealed trait ExpRule { def `type`: String }
object ExpRule {
  @derive(show, encoder, decoder) case class ObjectRule(rule: ObjectPredicate) extends ExpRule { val `type`: String = ObjectRuleType }
  @derive(show, encoder, decoder) case class FieldRule(rule: FieldPredicate)   extends ExpRule { val `type`: String = FieldRuleType }

  private lazy val ObjectRuleType: String                     = "obj"
  private lazy val FieldRuleType: String                      = "field"
  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[ExpRule] = Schema.oneOfUsingField[ExpRule, String](_.`type`, identity)(
    ObjectRuleType -> Schema.derived[ObjectRule],
    FieldRuleType  -> Schema.derived[FieldRule]
  )
  private val circeDiscriminators = Map(
    typeOf[ObjectRule].typeSymbol.name.toString -> ObjectRuleType,
    typeOf[FieldRule].typeSymbol.name.toString  -> FieldRuleType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[ExpRule]         = deriveConfiguredDecoder
  implicit val _encoder: Encoder[ExpRule]         = deriveConfiguredEncoder
}

@derive(show) sealed trait FieldPredicate { def `type`: String; def fieldName: String }
object FieldPredicate {
  @derive(show, encoder, decoder) case class ObjectField(fieldName: String, obj: String, expression: Expr) extends FieldPredicate { val `type`: String = ObjectFieldType }
  @derive(show, encoder, decoder) case class StringField(fieldName: String, rule: StringPredicate)         extends FieldPredicate { val `type`: String = StringFieldType }
  @derive(show, encoder, decoder) case class DateField(fieldName: String, rule: DatePredicate)             extends FieldPredicate { val `type`: String = DateFieldType }
  @derive(show, encoder, decoder) case class NumericField(fieldName: String, rule: NumericPredicate)       extends FieldPredicate { val `type`: String = NumericFieldType }

  private lazy val ObjectFieldType: String  = "obj"
  private lazy val StringFieldType: String  = "string"
  private lazy val DateFieldType: String    = "date"
  private lazy val NumericFieldType: String = "number"

  implicit val genDevConfig: sttp.tapir.generic.Configuration = sttp.tapir.generic.Configuration.default.withDiscriminator("type")
  implicit lazy val sEntity: Schema[FieldPredicate] = Schema.oneOfUsingField[FieldPredicate, String](_.`type`, identity)(
    ObjectFieldType  -> Schema.derived[StringField].description("Actual schema is for ObjectField"), // TODO Recursive not works
    StringFieldType  -> Schema.derived[StringField],
    DateFieldType    -> Schema.derived[DateField],
    NumericFieldType -> Schema.derived[NumericField]
  )
  private val circeDiscriminators = Map(
    typeOf[ObjectField].typeSymbol.name.toString  -> ObjectFieldType,
    typeOf[StringField].typeSymbol.name.toString  -> StringFieldType,
    typeOf[DateField].typeSymbol.name.toString    -> DateFieldType,
    typeOf[NumericField].typeSymbol.name.toString -> NumericFieldType
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[FieldPredicate]  = deriveConfiguredDecoder
  implicit val _encoder: Encoder[FieldPredicate]  = deriveConfiguredEncoder
}
