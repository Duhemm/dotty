package dotty.tools.languageserver

import org.junit.Test

import dotty.tools.languageserver.util.Code._

class DocumentLinkResolveTest {

  @Test def resolveInClassMember: Unit = {
    code"""class A {
             /**
              * Hello world
              *
              * @see ${m1}#buzz${m2}
              * @see ${m3}#bar${m4}
              */
             val fizz = 0
             val ${m5}buzz${m6} = 1
             def ${m7}bar${m8} = 2
           }""".withSource
      .documentLinkResolve(m1 to m2, m5 to m6)
      .documentLinkResolve(m3 to m4, m7 to m8)
  }

  @Test def resolveFullyQualifiedClass: Unit = {
    withSources(
      code"""package a.b.c
             /**
              * @see ${m1}d.e.f.Bar${m2}
              */
             class Foo""",
      code"""package d.e.f
             class ${m3}Bar${m4}"""
    ).documentLinkResolve(m1 to m2, m3 to m4)
  }
}
