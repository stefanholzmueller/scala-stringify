# Scala Stringify
An alternative to the `toString` method with the following main differences:
* includes field names for case classes
* can "pretty print" nested objects

It is meant to be used for logging or println-debugging of case classes and collections.

## Installation
Add this to your `build.sbt`:
```
externalResolvers += "scala-stringify packages" at "https://maven.pkg.github.com/stefanholzmueller/scala-stringify"
libraryDependencies += "stefanholzmueller" %% "scala-stringify" % "0.1"
```

## Usage
```scala
import stringify._

myCaseClass.toString
// Manager(Bill,100000.0,Manager(Frank,200000.0,CEO))

myCaseClass.stringify
// Manager(name: Bill, salary: 100000.0, reportsTo: Manager(name: Frank, salary: 200000.0, reportsTo: CEO))

myCaseClass.stringify(fieldNameValueSeparator = " = ")
// Manager(name = Bill, salary = 100000.0, reportsTo = Manager(name = Frank, salary = 200000.0, reportsTo = CEO))

myCaseClass.toPrettyString
/*
Manager(
  name = "Bill",
  salary = 100000.0,
  reportsTo = Manager(
    name = "Frank",
    salary = 200000.0,
    reportsTo = CEO))
*/
```

See the tests for more examples.

In case you want to adjust the formatting or use a different name for the extension method, you can write your own implicit class (and potentially create an issue or PR here).

## Remarks
* Before Scala 2.13, it was not possible to access the fields of case classes (without resorting to reflection or macros). So there is no 2.12 version of this library.
* If you have a custom `toString` implementation (or a `Show` instance), you should probably just use that. 
* This software is freely available under the terms of the *MIT license*.
