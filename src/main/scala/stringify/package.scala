/*
Copyright (c) 2020 Stefan Holzmüller

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package object stringify {

  /**
   * Replacement for `toString` with a couple of improvements:
   * - the string representation of case classes includes the field names
   * - in many cases generates valid Scala source code, i.e. it can be copy-pasted into the codebase
   * Limitations:
   * - no real pretty-printing with spacing, line-wrapping or indentation
   * - string literals are not properly escaped
   * - only Map values are enhanced, keys are printed with `toString`
   */
  def stringify(any: Any)
               (
                 openParen: Int => String = depth => "(",
                 comma: Int => String = depth => ", ",
                 closeParen: Int => String = depth => ")",
                 equals: String = " = " // TODO limit, align
               ): String = {

    def stringifyAny(depth: Int, any: Any): String = any match {
      case null =>
        "null"
      case map: Map[_, _] =>
        map.view.mapValues(stringifyAny(0, _)).toMap.toString
      case itr: Iterable[_] =>
        itr.map(stringifyAny(0, _)).toString
      case tup: Product if tup.getClass.getCanonicalName.startsWith("scala.Tuple") =>
        tup.productIterator.map(stringifyAny(depth + 1, _)).mkString(openParen(depth), comma(depth + 1), closeParen(depth))
      case prd: Product =>
        val fields = (prd.productElementNames zip prd.productIterator)
          .map { case (name, elem) => name + equals + stringifyAny(depth + 1, elem) }
        if (fields.isEmpty) prd.productPrefix else fields.mkString(prd.productPrefix + openParen(depth), comma(depth), closeParen(depth))
      case str: String =>
        "\"" + str + "\""
      case obj =>
        obj.toString
    }

    stringifyAny(0, any)
  }

  /**
   * Importing this class adds the `toPrettyStrings` method to any object
   */
  implicit class ToPrettyString(any: Any) {
    def toPrettyString = stringify(any)()
  }

}
