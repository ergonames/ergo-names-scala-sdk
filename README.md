# Ergo Names Scala SDK

A simple SDK for resolving [Ergo Names](https://ergonames.com).

## Installation

**To install the library:**

A published package will be available once Ergo Names is released on mainnet.

Add this to your build.sbt

```scala
lazy val ergonamesSDK = RootProject(uri("https://github.com/ergonames/ergo-names-scala.git"))
lazy val root = (project in file(".")).dependsOn(ergonamesSDK)
```

**To import the functions:**

```scala
import ergonames.ErgoNamesSdk.ErgoNamesSdk._
```

## Documentation

Checking if address exists

```scala
val address = getOwnerAddress("bob.ergo")
println(address)
```

Lookup owner address

```scala
val exists = checkNameExists("bob.ergo")
println(exists)
```