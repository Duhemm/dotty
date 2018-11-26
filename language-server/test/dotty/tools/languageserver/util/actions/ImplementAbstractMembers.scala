package dotty.tools.languageserver.util.actions

import dotty.tools.languageserver.DottyLanguageServer.ImplementAbstractMembersParams
import dotty.tools.languageserver.util.{CodeRange, PositionContext}
import dotty.tools.languageserver.util.embedded.CodeMarker

import org.eclipse.lsp4j.{ExecuteCommandParams, ApplyWorkspaceEditResponse}

import org.junit.Assert.{assertEquals, assertTrue}

import scala.collection.JavaConverters._

class ImplementAbstractMembers(override val range: CodeRange, expected: Set[String], position: CodeMarker) extends ActionOnRange {
  override def onMarker(marker: CodeMarker): Exec[Unit] = {
    val params = new ImplementAbstractMembersParams(marker.uri, marker.toPosition, 0)
    val queryParams = new ExecuteCommandParams("implementAbstractMembers", List(params).asJava)
    val query = server.executeCommand(queryParams).get
    val edits = client.workspaceEdits.get
    val r = new ApplyWorkspaceEditResponse(true)
    edits.foreach((_, future) => future.complete(r))
    edits.foreach { (edit, _) =>
      val documentChanges = edit.getEdit.getDocumentChanges.asScala
      documentChanges.foreach { change =>
        val textEdits = change.getEdits.asScala
        assertEquals(marker.uri, change.getTextDocument.getUri)
        textEdits.foreach { textEdit =>
          val lines = textEdit.getNewText.linesIterator
          assertEquals((position to position).toRange, textEdit.getRange)
          lines.foreach(l => assertTrue(s"Unexpected addition: '$l'", expected.contains(l)))
        }
      }
    }
  }

  override def show: PositionContext.PosCtx[String] =
    s"ImplementAbstractMembers($range, $expected, $position)"
}
