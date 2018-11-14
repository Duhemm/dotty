package dotty.tools.languageserver.util.actions

import dotty.tools.languageserver.util.embedded.CodeMarker
import dotty.tools.languageserver.util.{CodeRange, PositionContext, SymInfo}
import org.junit.Assert.assertEquals

import dotty.tools.languageserver.DottyLanguageServer.Stuff

import scala.collection.JavaConverters._

class DocumentLink(override val marker: CodeMarker, expected: Seq[CodeRange]) extends ActionOnMarker {

  private implicit val RangeOrdering: Ordering[org.eclipse.lsp4j.Range] = Ordering.by(_.toString)

  override def execute(): Exec[Unit] = {
    val results: Seq[org.eclipse.lsp4j.Range] = server.documentLink(marker.toDocumentLinkParams).get().asScala.map { link =>
      link.getData.asInstanceOf[Stuff].contentPos
    }.sorted

    val expectedRanges: Seq[org.eclipse.lsp4j.Range] = expected.map(_.toRange).sorted

    assertEquals(expectedRanges.length, results.length)
    expectedRanges.zip(results).foreach { (exp, res) =>
      assertEquals(exp, res)
    }
  }

  override def show: PositionContext.PosCtx[String] =
    s"DocumentLink(${marker.show}, ${expected})"
}
