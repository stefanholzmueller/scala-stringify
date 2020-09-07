organization := "stefanholzmueller"
name := "scala-stringify"
version := "0.2"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test

githubOwner := "stefanholzmueller"
githubRepository := "scala-stringify"
githubTokenSource := TokenSource.GitConfig("github.token")
