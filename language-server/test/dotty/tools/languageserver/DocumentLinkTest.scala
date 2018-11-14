package dotty.tools.languageserver

import org.junit.Test

import dotty.tools.languageserver.util.Code._

class DocumentLinkTest {
  @Test def singleLink: Unit = {
    code"""/**
            * Hello world
            *
            * @see ${m1}foo${m2}
            */
           class X""".withSource
      .documentLink(m1, List(m1 to m2))
  }

  @Test def multipleLinks: Unit = {
    code"""/**
            * Hello world
            *
            * @see ${m1}foo${m2}
            * @see ${m3}bar${m4}
            */
           class X""".withSource
      .documentLink(m1, List(m1 to m2, m3 to m4))
  }
}
