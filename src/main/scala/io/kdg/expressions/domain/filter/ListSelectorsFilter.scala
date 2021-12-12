package io.kdg.expressions.domain.filter

import cats.data.NonEmptyList
import io.kdg.expressions.domain.types.{QueryLimit, SelectorId}

case class ListSelectorsFilter(
    ids: Option[NonEmptyList[SelectorId]] = None,
    limit: Option[QueryLimit]             = None
)
