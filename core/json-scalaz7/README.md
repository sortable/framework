Scalaz support for Lift JSON
============================

This project adds a type class to parse JSON:

    trait JSON[A] {
      def read(json: JValue): Result[A]
      def write(value: A): JValue
    }

    type Result[+A] = ValidationNel[Error, A]

Function 'read' returns an Applicative Functor, enabling parsing in an applicative style.

Simple example
--------------

    scala> import scalaz._
    scala> import Scalaz._
    scala> import net.liftweb.json.scalaz.JsonScalaz._
    scala> import net.liftweb.json._

    scala> case class Address(street: String, zipCode: String)
    scala> case class Person(name: String, age: Int, address: Address)
  
    scala> val json = parse(""" {"street": "Manhattan 2", "zip": "00223" } """)
    scala> (field[String]("street")(json) |@| field[String]("zip")(json)) { Address }
    res0: Success(Address(Manhattan 2,00223))

    scala> (field[String]("streets")(json) |@| field[String]("zip")(json)) { Address }
    res1: Failure("no such field 'streets'")

Notice the required explicit types when reading fields from JSON. The library comes with helpers which
can lift functions with pure values into "parsing context". This works well with Scala's type inferencer:

    scala> Address.applyJSON(field[String]("street"), field[String]("zip"))(json)
    res2: Success(Address(Manhattan 2,00223))

Function 'applyJSON' above lifts function 

    (String, String) => Address 

to

    (JValue => Result[String], JValue => Result[String]) => (JValue => Result[Address])

Example which adds a new type class instance
--------------------------------------------

    scala> implicit def addrJSONR: JSONR[Address] = Address.applyJSON(field[String]("street"), field[String]("zip"))

    scala> val p = JsonParser.parse(""" {"name":"joe","age":34,"address":{"street": "Manhattan 2", "zip": "00223" }} """)
    scala> Person.applyJSON(field[String]("name"), field[Int]("age"), field[Address]("address"))(p)
    res0: Success(Person(joe,34,Address(Manhattan 2,00223)))

Validation
----------

Applicative style parsing works nicely with validation and data conversion. It is easy to compose 
validations using a for comprehension.

    def min(x: Int): Int => Result[Int] = (y: Int) => 
      if (y < x) Fail("min", y + " < " + x) else y.success

    def max(x: Int): Int => Result[Int] = (y: Int) => 
      if (y > x) Fail("max", y + " > " + x) else y.success

    val ageResult = (jValue: JValue) => for {
      age <- field[Int]("age")(jValue)
      _ <- min(16)(age)
      _ <- max(60)(age)
    } yield age

    // Creates a function JValue => Result[Person]
    Person.applyJSON(field[String]("name"), ageResult, field[Address]("address"))

Installation
------------

Add dependency to your SBT project description:

    val lift_json_scalaz = "net.liftweb" %% "lift-json-scalaz" % "XXX"

Links
-----

* [More examples](https://github.com/lift/framework/tree/master/core/json-scalaz7/src/test/scala/net/liftweb/json/scalaz)
* [Scalaz](http://code.google.com/p/scalaz/)
