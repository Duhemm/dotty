package dotty.tools.languageserver.util.actions

import dotty.tools.languageserver.DottyLanguageServer
import dotty.tools.languageserver.util.embedded.CodeMarker
import dotty.tools.languageserver.util.{CodeRange, PositionContext, SymInfo}

import org.junit.Assert.{assertEquals, fail}

import scala.collection.JavaConverters._

class DocumentLinkResolve(override val range: CodeRange, expected: CodeRange) extends ActionOnRange {

  override def onMarker(marker: CodeMarker): Exec[Unit] = {
    val links = server.documentLink(marker.toDocumentLinkParams).get().asScala
    // println("Looking for link:")
    // println("-----> " + marker.file.uri)
    // println("-----> " + range.toRange)
    val matching = links.find { link =>
      link.getData match {
        case s @ DottyLanguageServer.Stuff(uri, commentRange, pos, _) =>
          // println("Link:")
          // println("----> " + uri)
          // println("----> " + commentRange)
          uri.toString == marker.file.uri && commentRange == range.toRange
        case _ =>
          false
      }
    }.getOrElse{ fail("No link found"); ??? }

    val resolved = server.documentLinkResolve(matching).get

    assertEquals(expected.file.uri, resolved.getTarget)
    assertEquals(expected.toRange, resolved.getRange)

    // val results = {
    //   val links = server.documentLink(marker.toDocumentLinkParams).get().asScala
    //   val matchingLink = links.
    //   links.map(l => server.documentLinkResolve(l).get)
    // }
    //
    //
    // assertEquals(expected.length, results.length)
    // expected.zip(results).foreach {
    //   case ((range, data), result) =>
    //     assertEquals(range.toRange, result.getRange)
    //     assertEquals(data, result.getData)
    // }
  }

  override def show: PositionContext.PosCtx[String] =
    s"DocumentLinkResolve(${range.show}, ${expected})"
}
