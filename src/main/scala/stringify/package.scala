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

  def stringifyWithConfig(
                           indentation: String,
                           stringQuotes: String,
                           fieldNameValueSeparator: String,
                           // TODO limit, align
                         )(any: Any): String = {

    def stringifyAny(depth: Int, any: Any): String = any match {
      case null =>
        "null"

      case map: Map[_, _] =>
        //val maxLength = map.keys.map(k => stringifyAny(0, k).length).max
        map.view.mapValues(stringifyAny(depth + 1, _)).toMap.toString

      case iterable: Iterable[_] =>
        val spacingBeforeField = if (indentation.isEmpty) "" else "\n" + indentation * (depth + 1)
        iterable.map(elem => spacingBeforeField + stringifyAny(depth + 1, elem)).toString

      case option: Option[_] =>
        option.toString

      case either: Either[_, _] =>
        either.toString

      case tuple: Product if tuple.getClass.getCanonicalName.startsWith("scala.Tuple") =>
        tuple.productIterator.map(stringifyAny(depth + 1, _)).mkString("(", ", ", ")")

      case prd: Product =>
        val spacingBeforeField = if (indentation.isEmpty) "" else "\n" + indentation * (depth + 1)
        val fields = (prd.productElementNames zip prd.productIterator)
          .map { case (name, elem) => spacingBeforeField + name + fieldNameValueSeparator + stringifyAny(depth + 1, elem) }
        if (fields.isEmpty) prd.productPrefix else fields.mkString(prd.productPrefix + "(", ", ", ")")

      case str: String =>
        stringQuotes + str + stringQuotes

      case obj =>
        obj.toString
    }

    stringifyAny(0, any)
  }

  /**
   * Importing this class adds the `stringify` extension method to any object, which is very similar to toString,
   * except the field names of case classes are included in the output.
   */
  implicit class Stringify(any: Any) {
    /**
     * @param stringQuotes what kind of quotes (if any) around literal strings
     * @param fieldNameValueSeparator the string to use for separating field names and values
     */
    def stringify(
                   stringQuotes: String = "",
                   fieldNameValueSeparator: String = ": "
                 ): String = stringifyWithConfig("", stringQuotes, fieldNameValueSeparator)(any)
    def stringify: String = stringify()
  }

  /**
   * Importing this class adds the `toPrettyString` extension method to any object
   */
  implicit class ToPrettyString(any: Any) {
    def toPrettyString(
                        indentation: String = "  ",
                        stringQuotes: String = "\"",
                        fieldNameValueSeparator: String = " = "
                      ) = stringifyWithConfig(indentation, stringQuotes, fieldNameValueSeparator)(any)
    def toPrettyString: String = any.toPrettyString()
  }

}
