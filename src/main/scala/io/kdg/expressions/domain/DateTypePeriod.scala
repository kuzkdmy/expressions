package io.kdg.expressions.domain

import enumeratum.values._

sealed trait DateTypePeriod extends StringEnumEntry
case object DateTypePeriod extends StringEnum[DateTypePeriod] with StringCirceEnum[DateTypePeriod] {
  case object Day   extends DateTypePeriod { val value: String = "Day" }
  case object Week  extends DateTypePeriod { val value: String = "Week" }
  case object Month extends DateTypePeriod { val value: String = "Month" }
  case object Year  extends DateTypePeriod { val value: String = "Year" }
  def values: IndexedSeq[DateTypePeriod] = findValues
}
