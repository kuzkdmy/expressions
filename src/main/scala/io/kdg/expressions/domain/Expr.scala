package io.kdg.expressions.domain

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.extras._
import io.circe.generic.extras.semiauto._
import io.circe.{Decoder, Encoder}
import io.kdg.expressions.domain.CirceUtil.circeConfiguration

import scala.reflect.runtime.universe.typeOf

sealed trait Expr
object Expr {
  @derive(encoder, decoder) case class CombineExpr(conjunction: Boolean, expressions: Seq[Expr]) extends Expr
  @derive(encoder, decoder) case class EvalExpr(obj: String, rules: Seq[ExpRule])                extends Expr

  private val circeDiscriminators = Map(
    typeOf[CombineExpr].typeSymbol.name.toString -> "combine",
    typeOf[EvalExpr].typeSymbol.name.toString    -> "eval"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[Expr]            = deriveConfiguredDecoder
  implicit val _encoder: Encoder[Expr]            = deriveConfiguredEncoder
}

sealed trait ExpRule
object ExpRule {
  @derive(encoder, decoder) case class ObjectRule(rule: ObjectPredicate) extends ExpRule
  @derive(encoder, decoder) case class FieldRule(rule: FieldPredicate)   extends ExpRule

  private val circeDiscriminators = Map(
    typeOf[ObjectRule].typeSymbol.name.toString -> "obj",
    typeOf[FieldRule].typeSymbol.name.toString  -> "field"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[ExpRule]         = deriveConfiguredDecoder
  implicit val _encoder: Encoder[ExpRule]         = deriveConfiguredEncoder
}

sealed trait FieldPredicate { def fieldName: String }
object FieldPredicate {
  @derive(encoder, decoder) case class ObjectField(fieldName: String, obj: String, expression: Expr) extends FieldPredicate
  @derive(encoder, decoder) case class StringField(fieldName: String, rule: StringPredicate)         extends FieldPredicate
  @derive(encoder, decoder) case class DateField(fieldName: String, rule: DatePredicate)             extends FieldPredicate
  @derive(encoder, decoder) case class NumericField(fieldName: String, rule: NumericPredicate)       extends FieldPredicate

  private val circeDiscriminators = Map(
    typeOf[ObjectField].typeSymbol.name.toString  -> "obj",
    typeOf[StringField].typeSymbol.name.toString  -> "string",
    typeOf[DateField].typeSymbol.name.toString    -> "date",
    typeOf[NumericField].typeSymbol.name.toString -> "number"
  )
  implicit val _circeConfiguration: Configuration = circeConfiguration(circeDiscriminators)
  implicit val _decoder: Decoder[FieldPredicate]  = deriveConfiguredDecoder
  implicit val _encoder: Encoder[FieldPredicate]  = deriveConfiguredEncoder
}
