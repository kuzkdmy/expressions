package io.kdg.expressions.route

import cats.data.NonEmptyList
import cats.implicits.catsSyntaxEitherId
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.kdg.expressions.domain.dto.Selector
import io.kdg.expressions.domain.filter.ListSelectorsFilter
import io.kdg.expressions.domain.types._
import io.kdg.expressions.route.middleware.syntax._
import io.kdg.expressions.service.SelectorService
import io.kdg.expressions.trace.Ctx
import sttp.model.StatusCode
import sttp.tapir
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zhttp.http.HttpApp
import zio._

object SelectorRoute {
  import sttp.tapir._
  type Env = Has[SelectorRouteService]

  def endpoints[R <: Env](interpreter: ZioHttpInterpreter[R]): HttpApp[R, Throwable] =
    interpreter.toHttp(listE)  { i => SelectorRouteService(_.list(i)) } <>
      interpreter.toHttp(getE) { i => SelectorRouteService(_.get(i)) } <>
      interpreter.toHttp(deleteE) { i => SelectorRouteService(_.delete(i)) } <>
      interpreter.toHttp(createE) { i => SelectorRouteService(_.create(i)) } <>
      interpreter.toHttp(updateE) { i => SelectorRouteService(_.update(i)) }

  lazy val swaggerEndpoints = List(getE, deleteE, updateE, createE, listE)

  private val getE: Endpoint[(SelectorId, Ctx), GetErr, Selector, Any] = endpoint.get
    .in("api" / "v1.0" / "selector" / path[SelectorId]("id"))
    .out(jsonBody[Selector])
    .withRequestContext()
    .errorOut(
      tapir.oneOf[GetErr](
        oneOfMappingFromMatchType(StatusCode.NotFound, jsonBody[GetErr.NotFound].description("not found"))
      )
    )
  private val listE: Endpoint[(List[SelectorId], Option[QueryLimit], Ctx), Unit, List[Selector], Any] = endpoint.get
    .in("api" / "v1.0" / "selector")
    .out(jsonBody[List[Selector]])
    .in(query[List[SelectorId]]("id"))
    .in(query[Option[QueryLimit]]("limit"))
    .withRequestContext()
  private val deleteE: Endpoint[(SelectorId, IntVersion, Ctx), DeleteErr, Unit, Any] = endpoint.delete
    .in("api" / "v1.0" / "selector" / path[SelectorId]("id"))
    .in(jsonBody[IntVersion])
    .out(emptyOutput)
    .withRequestContext()
    .errorOut(
      tapir.oneOf[DeleteErr](
        oneOfMappingFromMatchType(StatusCode.NotFound, jsonBody[DeleteErr.NotFound].description("not found")),
        oneOfMappingFromMatchType(StatusCode.Conflict, jsonBody[DeleteErr.Conflict].description("modified"))
      )
    )
  private val createE: Endpoint[(Selector, Ctx), CreateErr, Selector, Any] = endpoint.post
    .in("api" / "v1.0" / "selector")
    .in(jsonBody[Selector])
    .out(jsonBody[Selector])
    .withRequestContext()
    .errorOut(
      tapir.oneOf[CreateErr](
        oneOfMappingFromMatchType(StatusCode.Conflict, jsonBody[CreateErr.Conflict].description("modified"))
      )
    )
  private val updateE: Endpoint[(SelectorId, Selector, Ctx), UpdateErr, Selector, Any] = endpoint.put
    .in("api" / "v1.0" / "selector" / path[SelectorId]("id"))
    .in(jsonBody[Selector])
    .out(jsonBody[Selector])
    .withRequestContext()
    .errorOut(
      tapir.oneOf[UpdateErr](
        oneOfMappingFromMatchType(StatusCode.NotFound, jsonBody[UpdateErr.NotFound].description("not found")),
        oneOfMappingFromMatchType(StatusCode.Conflict, jsonBody[UpdateErr.Conflict].description("modified"))
      )
    )

  @derive(schema, encoder, decoder) sealed trait DeleteErr
  object DeleteErr {
    @derive(schema, encoder, decoder) case class NotFound(message: String) extends DeleteErr
    @derive(schema, encoder, decoder) case class Conflict(message: String) extends DeleteErr
  }
  @derive(schema, encoder, decoder) sealed trait GetErr
  object GetErr {
    @derive(schema, encoder, decoder) case class NotFound(message: String) extends GetErr
  }
  @derive(schema, encoder, decoder) sealed trait CreateErr
  object CreateErr {
    @derive(schema, encoder, decoder) case class Conflict(message: String) extends CreateErr
  }
  @derive(schema, encoder, decoder) sealed trait UpdateErr
  object UpdateErr {
    @derive(schema, encoder, decoder) case class NotFound(message: String) extends UpdateErr
    @derive(schema, encoder, decoder) case class Conflict(message: String) extends UpdateErr
  }
}

trait SelectorRouteService {
  import io.kdg.expressions.route.SelectorRoute._
  def create(input: (Selector, Ctx)): Task[Either[CreateErr, Selector]]
  def update(input: (SelectorId, Selector, Ctx)): Task[Either[UpdateErr, Selector]]
  def get(input: (SelectorId, Ctx)): Task[Either[GetErr, Selector]]
  def list(input: (List[SelectorId], Option[QueryLimit], Ctx)): Task[Either[Unit, List[Selector]]]
  def delete(input: (SelectorId, IntVersion, Ctx)): Task[Either[DeleteErr, Unit]]
}
object SelectorRouteService extends Accessible[SelectorRouteService]
private class SelectorRouteServiceLive(service: SelectorService) extends SelectorRouteService {
  import io.kdg.expressions.route.SelectorRoute._
  override def create(input: (Selector, Ctx)): Task[Either[CreateErr, Selector]] = {
    val (selector, ctx) = input
    for {
      notExists <- service.get(selector.id)(ctx).map(_.isEmpty)
      res <- if (notExists) {
               service
                 .upsert(selector)(ctx)
                 .foldM(
                   err => ZIO.left(CreateErr.Conflict(err.getMessage)),
                   res => ZIO.right(res)
                 )
             } else ZIO.succeed(CreateErr.Conflict(conflict(selector.id)).asLeft[Selector])
    } yield res
  }

  override def update(input: (SelectorId, Selector, Ctx)): Task[Either[UpdateErr, Selector]] = {
    val (id, selector, ctx) = input
    for {
      exists <- service.get(id)(ctx).map(_.nonEmpty)
      res <- if (exists) {
               service
                 .upsert(selector.copy(id = id))(ctx)
                 .foldM(
                   err => ZIO.left(UpdateErr.Conflict(err.getMessage)),
                   res => ZIO.right(res)
                 )
             } else ZIO.succeed(UpdateErr.NotFound(notFound(id)).asLeft[Selector])
    } yield res
  }

  override def get(input: (SelectorId, Ctx)): Task[Either[GetErr.NotFound, Selector]] = {
    val (id, ctx) = input
    for {
      resOpt <- service.get(id)(ctx)
    } yield resOpt match {
      case Some(r) => Right(r)
      case None    => Left(GetErr.NotFound(notFound(id)))
    }
  }
  override def list(input: (List[SelectorId], Option[QueryLimit], Ctx)): Task[Either[Unit, List[Selector]]] = {
    val (ids, queryLimit, ctx) = input
    for {
      res <- service.list(ListSelectorsFilter(NonEmptyList.fromList(ids), queryLimit))(ctx)
    } yield Right(res)
  }
  override def delete(input: (SelectorId, IntVersion, Ctx)): Task[Either[DeleteErr, Unit]] = {
    val (id, hasV, ctx) = input
    for {
      res <- service
               .delete(id, hasV)(ctx)
               .foldM(
                 err => ZIO.succeed(DeleteErr.Conflict(err.getMessage).asLeft[Unit]),
                 {
                   case Some(_) => ZIO.succeed(().asRight[DeleteErr])
                   case None    => ZIO.left(DeleteErr.NotFound(notFound(id)))
                 }
               )
    } yield res
  }
  private def notFound(id: SelectorId) = s"selector with id:$id not found"
  private def conflict(id: SelectorId) = s"selector with id:$id already exists"
}
object SelectorRouteServiceLive {
  val layer = ZLayer.fromService[SelectorService, SelectorRouteService](new SelectorRouteServiceLive(_))
}
