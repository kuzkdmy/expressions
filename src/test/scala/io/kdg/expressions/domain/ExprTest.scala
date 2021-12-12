package io.kdg.expressions.domain

import io.kdg.expressions.domain.ExpRule.{FieldRule, ObjectRule}
import io.kdg.expressions.domain.Expr.EvalExpr
import io.kdg.expressions.domain.FieldPredicate.{DateField, NumericField, ObjectField, StringField}
import zio.durationInt
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, TestAspect, TestEnvironment, ZSpec, assert}

import java.time.{LocalDate, Month}
import scala.util.control.NoStackTrace

object ExprTest extends DefaultRunnableSpec {
  override def aspects = List(TestAspect.timeout(1.minutes), TestAspect.parallel)

  override def spec: ZSpec[TestEnvironment, Any] = suite("Expressions Parse")(
    test("person exists, check simple rule on object") {
      assert(exp {
        """{
              | "type": "eval",
              | "obj": "Person",
              | "rules": [
              |   { "type": "obj", "rule": { "type": "Exists" } }
              | ]
              |}""".stripMargin
      })(equalTo(EvalExpr(obj = "Person", rules = Seq(ObjectRule(ObjectPredicate.Exists())))))
    },
    test("person email exists, check simple rule on object field") {
      assert(exp {
        """{
          | "type": "eval",
          | "obj": "Person",
          | "rules": [
          |   { "type": "field", "rule": { "type": "string", "fieldName": "email", "rule": { "type": "Exists" } } }
          | ]
          |}""".stripMargin
      })(equalTo(EvalExpr(obj = "Person", rules = Seq(FieldRule(StringField("email", StringPredicate.Exists()))))))
    },
    test("person age >= 25, check simple rule on object field") {
      val actual = exp {
        """{
          | "type": "eval",
          | "obj": "Person",
          | "rules": [
          |   { "type": "field", "rule": { "type": "number", "fieldName": "age", "rule": { "type": "Greater Than or Equal To(>=)", "value": "25" } } }
          | ]
          |}""".stripMargin
      }
      val expected = EvalExpr(obj = "Person", rules = Seq(FieldRule(NumericField("age", NumericPredicate.GE(25)))))
      assert(actual)(equalTo(expected))
    },
    test("person insurance created date in January 2021, check rule on object field that is also object") {
      val actual = exp {
        """{
          |  "obj": "Person",
          |  "type": "eval",
          |  "rules": [
          |    {
          |      "type": "field",
          |      "rule": {
          |        "type": "obj",
          |        "fieldName": "insurance",
          |        "obj": "Insurance",
          |        "expression": {
          |          "type": "eval",
          |          "obj": "Insurance",
          |          "rules": [
          |            {
          |              "type": "field",
          |              "rule": {
          |                "type": "date",
          |                "fieldName": "createdDate",
          |                "rule": {
          |                  "type": "Between",
          |                  "ge": "2021-01-01",
          |                  "lt": "2021-02-01"
          |                }
          |              }
          |            }
          |          ]
          |        }
          |      }
          |    }
          |  ]
          |}""".stripMargin
      }
      val expected = EvalExpr(
        obj = "Person",
        rules = Seq(
          FieldRule(
            ObjectField(
              fieldName = "insurance",
              obj       = "Insurance",
              expression = EvalExpr(
                obj = "Insurance",
                rules = Seq(
                  FieldRule(
                    DateField(
                      fieldName = "createdDate",
                      rule = DatePredicate.Between(
                        ge = LocalDate.of(2021, Month.JANUARY, 1),
                        lt = LocalDate.of(2021, Month.FEBRUARY, 1)
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
      assert(actual)(equalTo(expected))
    }
  )

  private def exp(str: String): Expr = {
    import io.circe.parser._
    val res = for {
      js  <- parse(str)
      res <- js.as[Expr]
    } yield res
    res.fold(err => throw new RuntimeException(s"Parse failed: $err") with NoStackTrace, identity)
  }
}
