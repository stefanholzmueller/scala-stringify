package stringify

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class StringifySpec extends AnyFunSuite with Matchers {

  test("null safe") {
    val s: String = null
    s.toPrettyString shouldEqual "null"
  }

  test("primitive type") {
    1234.toPrettyString shouldEqual "1234"
  }

  test("String literal") {
    "test".toPrettyString shouldEqual """"test""""
  }

  test("sealed trait class") {
    Active(123, "test").toPrettyString shouldEqual """Active(i = 123, s = "test")"""
  }

  test("sealed trait object") {
    Inactive.toPrettyString shouldEqual "Inactive"
  }

  test("case classes") {
    val cc = CaseClass(true, sd = StringAndDouble("s", 123.45))
    cc.toPrettyString shouldEqual """CaseClass(flag = true, sd = StringAndDouble(s = "s", d = 123.45))"""
  }

  test("tuples") {
    (1, ("a", true, 0)).toPrettyString shouldEqual """(1, ("a", true, 0))"""
  }

  test("lists") {
    List(List(1, 2, 3), List(4, 5, 6)).toPrettyString shouldEqual """List(List(1, 2, 3), List(4, 5, 6))"""
  }

  test("sets") {
    Set(1,2,3).toPrettyString shouldEqual """Set(1, 2, 3)"""
  }

  test("Map values") {
    val m = Map(1 -> StringAndDouble("a", 1.0), 2 -> StringAndDouble("b", 2.0))
    m.toPrettyString shouldEqual """Map(1 -> StringAndDouble(s = "a", d = 1.0), 2 -> StringAndDouble(s = "b", d = 2.0))"""
  }

  case class CaseClass(flag: Boolean, sd: StringAndDouble)
  case class StringAndDouble(s: String, d: Double)

  sealed trait Status
  case object Inactive extends Status
  case class Active(i: Int, s: String) extends Status

}
