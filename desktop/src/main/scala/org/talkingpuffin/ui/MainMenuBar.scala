package org.talkingpuffin.ui

import _root_.scala.swing.event.ButtonClicked
import _root_.scala.swing.{MenuItem, MenuBar, Menu, CheckMenuItem, Action}
import javax.swing.KeyStroke
import java.awt.event.KeyEvent
import java.awt.Toolkit
import state.{GlobalPrefs, PrefKeys}

/**
 * Main menu bar
 */
class MainMenuBar extends MenuBar {
  val prefs = GlobalPrefs.prefs

  contents += new Menu("File") {
    contents += new MenuItem(new Action("Close Window") {
        accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                                                  Toolkit.getDefaultToolkit.getMenuShortcutKeyMask))
        def apply = TopFrames.closeCurrentWindow()
    })
  }
  contents += new Menu("Session") {
    contents += new MenuItem(new Action("New...") {
      accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
        Toolkit.getDefaultToolkit.getMenuShortcutKeyMask))
      def apply = Main.launchSession 
    })
  }
  contents += new Menu("Options") {
    object ItemFactory {
      def apply(title: String, tooltip: String, prefKey: String): MenuItem = {
        val item = new CheckMenuItem(title) {this.tooltip = tooltip; selected = prefs.getBoolean(prefKey, false)}
        listenTo(item)
        reactions += {
          case r: ButtonClicked => if (r.source == item) prefs.putBoolean(prefKey, item.selected)
          case _ =>
        }
        item
      }
    }
    contents += ItemFactory("Use animations", "Enables simple, useful animations", PrefKeys.USE_ANIMATIONS)
    contents += ItemFactory("Look up locations", "Enables lookup of locations from latitude and longitude", 
      PrefKeys.LOOK_UP_LOCATIONS)
    contents += ItemFactory("Expand URLs", "Enables fetching original URL from shortened form", 
      PrefKeys.EXPAND_URLS)
  }
  
}
