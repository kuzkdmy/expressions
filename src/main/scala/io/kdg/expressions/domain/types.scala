package io.kdg.expressions.domain

import derevo.cats.{order, show}
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object types {
  @derive(show, order, schema, encoder, decoder) @newtype case class SelectorId(value: String)
  @derive(show, order, schema, encoder, decoder) @newtype case class QueryLimit(value: Int)
  @derive(show, order, schema, encoder, decoder) @newtype case class IntVersion(value: Int)

  implicit class IntVersionExt(intVersion: IntVersion) {
    def inc: IntVersion = IntVersion(intVersion.value + 1)
  }
}
