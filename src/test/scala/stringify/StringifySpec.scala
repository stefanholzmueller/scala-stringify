package stringify

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class StringifySpec extends AnyWordSpec with Matchers {

  val nullString: String = null
  val testString: String = "test"
  val integer1234: Int = 1234

  sealed trait Employee
  case object CEO extends Employee
  case class Manager(name: String, salary: Double, reportsTo: Employee) extends Employee

  val frank: Manager = Manager(name = "Frank", salary = 200000, reportsTo = CEO)
  val bill: Employee = Manager("Bill", 100000, frank)


  "example for README" should {

    "include toString for comparision" in {
      assertString(bill.toString, "Manager(Bill,100000.0,Manager(Frank,200000.0,CEO))")
    }
    "give an example of stringify" in {
      assertString(bill.stringify, "Manager(name: Bill, salary: 100000.0, reportsTo: Manager(name: Frank, salary: 200000.0, reportsTo: CEO))")
    }
    "give an example for overriding the formatting" in {
      assertString(bill.stringify(fieldNameValueSeparator = " = "), "Manager(name = Bill, salary = 100000.0, reportsTo = Manager(name = Frank, salary = 200000.0, reportsTo = CEO))")
    }
    "give an example of toPrettyString" in {
      assertString(bill.toPrettyString, "Manager(\n  name = \"Bill\",\n  salary = 100000.0,\n  reportsTo = Manager(\n    name = \"Frank\",\n    salary = 200000.0,\n    reportsTo = CEO))")
    }

  }

  "replace toString (with a few changes)" should {

    "handle null safely" in {
      nullString.stringify shouldEqual "null"
    }
    "NOT quote strings" in {
      testString.stringify shouldEqual "test"
    }
    "return toString of primitive values" in {
      integer1234.stringify shouldEqual "1234"
    }
    "return toString of option" in {
      Some(integer1234).stringify shouldEqual "Some(1234)"
    }
    "return toString of either" in {
      Right(integer1234).stringify shouldEqual "Right(1234)"
      Left("error").stringify shouldEqual "Left(error)"
    }
    "override string quotes" in {
      testString.stringify(stringQuotes = "\"") shouldEqual "\"test\""
    }
    "display case object name" in {
      CEO.stringify shouldEqual "CEO"
    }
    "display case class with field names" in {
      frank.stringify shouldEqual """Manager(name: Frank, salary: 200000.0, reportsTo: CEO)"""
    }
    "display nested case class with field names" in {
      bill.stringify shouldEqual "Manager(name: Bill, salary: 100000.0, reportsTo: Manager(name: Frank, salary: 200000.0, reportsTo: CEO))"
    }
    "display tuples" in {
      (1.0, (testString, true, 0)).stringify shouldEqual """(1.0, (test, true, 0))"""
      (frank, CEO).stringify shouldEqual """(Manager(name: Frank, salary: 200000.0, reportsTo: CEO), CEO)"""
    }
    "display lists" in {
      List(List(1, 2, 3), List(4, 5, 6)).stringify shouldEqual "List(List(1, 2, 3), List(4, 5, 6))"
    }
    "display sets" in {
      Set(1, 2, 3).stringify shouldEqual """Set(1, 2, 3)"""
      mutable.LinkedHashSet(CEO, frank).stringify shouldEqual """LinkedHashSet(CEO, Manager(name: Frank, salary: 200000.0, reportsTo: CEO))"""
    }
    "display maps" in {
      Map(1 -> CEO, 2 -> frank, 3 -> bill).stringify shouldEqual
        """Map(1 -> CEO, 2 -> Manager(name: Frank, salary: 200000.0, reportsTo: CEO), 3 -> Manager(name: Bill, salary: 100000.0, reportsTo: Manager(name: Frank, salary: 200000.0, reportsTo: CEO)))"""
    }

  }

  "indent nested objects with toPrettyString" should {

    "handle null safely" in {
      nullString.toPrettyString shouldEqual "null"
    }
    "quote strings" in {
      testString.toPrettyString shouldEqual "\"test\""
    }
    "return toString of primitive values" in {
      integer1234.toPrettyString shouldEqual "1234"
    }
    "override string quotes" in {
      testString.toPrettyString(stringQuotes = "\"\"\"") shouldEqual "\"\"\"test\"\"\""
    }
    "display case object name" in {
      CEO.toPrettyString shouldEqual "CEO"
    }
    "display case class with field names" in {
      frank.toPrettyString shouldEqual "Manager(\n  name = \"Frank\", \n  salary = 200000.0, \n  reportsTo = CEO)"

      assertString(
        bill.toPrettyString,
        """Manager(
          |  name = "Bill",
          |  salary = 100000.0,
          |  reportsTo = Manager(
          |    name = "Frank",
          |    salary = 200000.0,
          |    reportsTo = CEO))"""
          .stripMargin
      )
    }
    "display vector of case classes" in {
      assertString(
        Vector(bill, frank).toPrettyString,
        """Vector(
          |  Manager(
          |    name = "Bill",
          |    salary = 100000.0,
          |    reportsTo = Manager(
          |      name = "Frank",
          |      salary = 200000.0,
          |      reportsTo = CEO)),
          |  Manager(
          |    name = "Frank",
          |    salary = 200000.0,
          |    reportsTo = CEO))"""
          .stripMargin
      )
    }
    "display lists" in {
      assertString(
        List(List(1, 2, 3), List(4, 5, 6)).toPrettyString,
        """List(
          |  List(
          |    1,
          |    2,
          |    3),
          |  List(
          |    4,
          |    5,
          |    6))"""
          .stripMargin
      )
    }
    "display sets" in {
      Set(1, 2, 3).toPrettyString shouldEqual "Set(\n  1, \n  2, \n  3)" // TODO space after comma
      assertString(
        mutable.LinkedHashSet(CEO, frank).toPrettyString,
        """LinkedHashSet(
          |  CEO,
          |  Manager(
          |    name = "Frank",
          |    salary = 200000.0,
          |    reportsTo = CEO))"""
          .stripMargin
      )
    }
    "display maps" ignore {
      assertString(
        Map(1 -> CEO, 2 -> frank, 3 -> bill).toPrettyString,
        """Map(
          |  1 -> CEO,
          |  2 -> Manager(
          |    name = "Frank",
          |    salary = 200000.0,
          |    reportsTo = CEO),
          |  3 -> Manager(
          |    name = "Bill",
          |    salary = 100000.0,
          |    reportsTo = Manager(
          |      name = "Frank",
          |      salary = 200000.0,
          |      reportsTo = CEO)))"""
          .stripMargin
      )
    }

  }

  private def assertString(actual: String, expected: String) = {
    val actualWithoutSpaceBeforeNewline = actual.replaceAll(" \n", "\n")
    val expectedWithoutWindowsCRLF = expected.replaceAll("\r\n", "\n")
    actualWithoutSpaceBeforeNewline shouldEqual expectedWithoutWindowsCRLF
  }

}
