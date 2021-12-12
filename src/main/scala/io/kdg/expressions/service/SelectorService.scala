package io.kdg.expressions.service

import cats.implicits.{catsSyntaxOptionId, toShow}
import io.kdg.expressions.domain.dto.Selector
import io.kdg.expressions.domain.error.EntryModifiedError
import io.kdg.expressions.domain.filter.ListSelectorsFilter
import io.kdg.expressions.domain.types.{IntVersion, SelectorId}
import io.kdg.expressions.trace._
import org.slf4j.LoggerFactory
import zio._

trait SelectorService {
  def upsert(cmd: Selector)(implicit ctx: Ctx): IO[EntryModifiedError, Selector]
  def get(id: SelectorId)(implicit ctx: Ctx): Task[Option[Selector]]
  def list(filter: ListSelectorsFilter)(implicit ctx: Ctx): Task[List[Selector]]
  def delete(id: SelectorId, version: IntVersion)(implicit ctx: Ctx): IO[EntryModifiedError, Option[Selector]]
}
object SelectorService extends Accessible[SelectorService]

case class SelectorServiceLive(ref: Ref[Map[SelectorId, Selector]]) extends SelectorService {
  implicit private val logger: org.slf4j.Logger = LoggerFactory.getLogger(this.getClass)

  override def upsert(cmd: Selector)(implicit ctx: Ctx): IO[EntryModifiedError, Selector] = {
    ref
      .modify { map =>
        val nextState = cmd.copy(version = cmd.version.inc)
        map.get(cmd.id) match {
          case None                                      => (UpsertRes.Upserted(None, nextState), map + (cmd.id -> nextState))
          case Some(prev) if prev.version == cmd.version => (UpsertRes.Upserted(prev.some, nextState), map + (cmd.id -> nextState))
          case Some(state)                               => (UpsertRes.EntryModified(state.version), map)
        }
      }
      .flatMap {
        case UpsertRes.EntryModified(stateV) =>
          log.info(s"upsert project config fail, server version:[$stateV], cmd version:[${cmd.version}]") *>
            IO.fail(EntryModifiedError("Project Config"))
        case UpsertRes.Upserted(prev, upserted) =>
          log.info(s"upsert project config, prev:[${prev.map(_.show).getOrElse("")}], current:[${upserted.show}]").as(upserted)
      }
  }
  override def get(id: SelectorId)(implicit ctx: Ctx): Task[Option[Selector]] = {
    ref.get.map(_.get(id))
  }
  override def list(filter: ListSelectorsFilter)(implicit ctx: Ctx): Task[List[Selector]] = {
    for {
      data <- ref.get
      res <- ZIO.succeed {
               val idsF = filter.ids.map(_.toList.toSet)
               data.values.toList
                 .flatMap(t => if (idsF.forall(_.contains(t.id))) t.some else None)
             }
    } yield res.sortBy(_.id.value).take(filter.limit.map(_.value).getOrElse(res.size))
  }
  override def delete(id: SelectorId, version: IntVersion)(implicit ctx: Ctx): IO[EntryModifiedError, Option[Selector]] = {
    ref
      .modify { map =>
        map.get(id) match {
          case Some(prev) if prev.version == version => (DeleteRes.Deleted(prev), map - id)
          case Some(state)                           => (DeleteRes.EntryModified(state.version), map)
          case None                                  => (DeleteRes.NotFound, map)
        }
      }
      .flatMap {
        case DeleteRes.NotFound => IO.none
        case DeleteRes.EntryModified(stateV) =>
          log.info(s"delete project config fail, server version:[$version], cmd version:[${stateV}]") *>
            IO.fail(EntryModifiedError("Project Config"))
        case DeleteRes.Deleted(prev) =>
          log.info(s"deleted project: ${prev.show}") *> IO.some(prev)
      }
  }
  sealed private trait UpsertRes
  private object UpsertRes {
    case class EntryModified(stateV: IntVersion)                    extends UpsertRes
    case class Upserted(prev: Option[Selector], upserted: Selector) extends UpsertRes
  }

  sealed private trait DeleteRes
  private object DeleteRes {
    case object NotFound                         extends DeleteRes
    case class EntryModified(stateV: IntVersion) extends DeleteRes
    case class Deleted(prev: Selector)           extends DeleteRes
  }
}

object SelectorServiceLive {
  val layer = (for {
    ref <- Ref.makeManaged(Map.empty[SelectorId, Selector])
  } yield SelectorServiceLive(ref)).toLayer[SelectorService]
}
