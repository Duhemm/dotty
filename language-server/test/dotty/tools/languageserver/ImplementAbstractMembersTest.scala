package dotty.tools.languageserver

import dotty.tools.languageserver.util.Code._

import org.junit.Test

class ImplementAbstractMembersTest {
  @Test def implementAbstractClass: Unit = {
    code"""abstract class Foo {
          |  val myVal: Unit
          |  var myVar: Unit
          |  lazy val myLazyVal: Unit
          |  def myDef: Unit
          |}
          |class ${m1}Bar${m2} extends Foo {
          |${m3}}""".withSource
      .implementAbstractMembers(m1 to m2, Set("  override val myVal: Unit = ???",
                                              "  var myVar: Unit = ???",
                                              "  override lazy val myLazyVal: Unit = ???",
                                              "  override def myDef: Unit = ???"), m3)
  }

  @Test def implementTrait: Unit = {
    code"""trait Foo {
          |  def fizz: Unit
          |}
          |class ${m1}Bar${m2} extends Foo {
          |${m3}}""".withSource
      .implementAbstractMembers(m1 to m2, Set("  override def fizz: Unit = ???"), m3)
  }

  @Test def implementMethodParametersImplicit: Unit = {
  code"""trait Foo {
        |  def baz(implicit x: Int): Int
        |}
        |class ${m1}Bar${m2} extends Foo {
        |${m3}}""".withSource
    .implementAbstractMembers(
      m1 to m2,
      Set("  override def baz(implicit x: Int): Int = ???"),
      m3)

  }

  @Test def implementMethodParameters: Unit = {
    code"""trait Foo {
          // |  def foo(x: Int): Int
          // |  def bar(x: Int)(y: String): Int
          |  def baz(x: Int)(y: String)(implicit z: Unit): Double
          |}
          |class ${m1}Bar${m2} extends Foo {
          |${m3}}""".withSource
      .implementAbstractMembers(
        m1 to m2,
        Set(//"  override def foo(x: Int): Int = ???",
            //"  override def bar(x: Int)(y: String): Int = ???",
            "  override def baz(x: Int)(y: String)(implicit z: Unit): Double = ???"),
        m3)
  }
}
