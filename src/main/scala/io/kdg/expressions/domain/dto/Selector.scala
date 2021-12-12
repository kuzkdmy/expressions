package io.kdg.expressions.domain.dto

import derevo.cats.show
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.kdg.expressions.domain.Expr
import io.kdg.expressions.domain.types.{IntVersion, SelectorId}
import sttp.tapir.derevo.schema

@derive(show, schema, encoder, decoder) case class Selector(id: SelectorId, expression: Expr, version: IntVersion)
