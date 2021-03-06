package org.talkingpuffin.ui

import javax.swing.table.{AbstractTableModel, TableModel}
import swing.{FlowPanel, ScrollPane, BorderPanel, Frame, Button, Action}
import javax.swing.JTable
import twitter4j.UserList
import org.jdesktop.swingx.JXTable
import org.talkingpuffin.util.Loggable
import org.jdesktop.swingx.decorator.HighlighterFactory
import org.talkingpuffin.Session
import util.{TableUtil, Cancelable}

class ListsTableModel(lists: Iterable[UserList]) extends AbstractTableModel {
  val listsArray = lists.toArray
  def getValueAt(rowIndex: Int, columnIndex: Int)= {
    val list = listsArray(rowIndex)
    columnIndex match {
      case 0 => list.getUser.getName
      case 1 => list.getName
      case 2 => list.getDescription
      case 3 => list.getMemberCount.asInstanceOf[Object]
      case 4 => list.getSubscriberCount.asInstanceOf[Object]
    }
  }

  def getRowCount = listsArray.length

  def getColumnCount = 5

  override def getColumnClass(col: Int) = List(
    classOf[String], 
    classOf[String], 
    classOf[String],
    classOf[Long], 
    classOf[Long])(col) 

  override def getColumnName(column: Int) = List("Owner", "Name", "Description", "Fllwing", "Fllrs")(column)
}

class ListsTable(model: TableModel) extends JXTable(model) with Loggable {
  setHighlighters(HighlighterFactory.createSimpleStriping)
  val ownerNameCol       = getColumnExt(0)
  val nameCol            = getColumnExt(1)
  val descriptionCol     = getColumnExt(2)
  val memberCountCol     = getColumnExt(3)
  val subscriberCountCol = getColumnExt(4)
  List(nameCol, ownerNameCol).foreach(_.setPreferredWidth(140))
  descriptionCol.setPreferredWidth(400)
  List(memberCountCol, subscriberCountCol).foreach(_.setPreferredWidth(50))
}

class ListsFrame(session: Session, lists: Iterable[UserList]) extends Frame with Cancelable {
  val listsArray = lists.toArray
  title = "Lists"
  contents = new BorderPanel {
    val table = new ListsTable(new ListsTableModel(listsArray))
    add(new ScrollPane {
      peer.setViewportView(table)
    }, BorderPanel.Position.Center)
    add(new FlowPanel {
      contents += new Button(Action("View") {
        TwitterListsDisplayer.viewLists(session, selectedLists(table))
      })
      contents += new Button(Action("View Statuses") {
        TwitterListsDisplayer.viewListsStatuses(session, selectedLists(table))
      })
      contents += new Button(Action("Import") {
        TwitterListsDisplayer.importLists(session, selectedLists(table))
      })
    }, BorderPanel.Position.South)
  }
  peer.setLocationRelativeTo(null)
  visible = true
  
  private def selectedLists(table: JTable) = TableUtil.getSelectedModelIndexes(table).map(listsArray)
}