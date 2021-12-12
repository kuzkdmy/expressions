package io.kdg.expressions.domain

import io.circe.syntax.EncoderOps
import io.circe.{Decoder, Encoder}

case class NumericString private (value: String)
object NumericString {
  implicit def apply[T <: Number: Encoder](value: T): NumericString = new NumericString(value.asJson.toString())
  def apply(value: String): Either[IllegalArgumentException, NumericString] =
    Either.cond(
      test  = value.toDoubleOption.isDefined,
      right = new NumericString(value),
      left  = new IllegalArgumentException(s"not a valid number : $value")
    )
  implicit def apply(num: Int): NumericString        = new NumericString(num.toString)
  implicit def apply(num: Long): NumericString       = new NumericString(num.toString)
  implicit def apply(num: Float): NumericString      = new NumericString(num.toString)
  implicit def apply(num: Double): NumericString     = new NumericString(num.toString)
  implicit def apply(num: BigDecimal): NumericString = new NumericString(num.toString)

  implicit val encoder: Encoder[NumericString] = Encoder.encodeString.contramap[NumericString](_.toString)
  implicit val decoder: Decoder[NumericString] = Decoder.decodeString.emapTry(str => NumericString(str).toTry)
}
