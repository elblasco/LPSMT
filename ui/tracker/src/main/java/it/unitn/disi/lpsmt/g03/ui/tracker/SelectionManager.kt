package it.unitn.disi.lpsmt.g03.ui.tracker

import androidx.appcompat.view.ActionMode
import it.unitn.disi.lpsmt.g03.data.library.Series

data class SelectionManager(var actionMode: ActionMode?, var selected: Series?)
