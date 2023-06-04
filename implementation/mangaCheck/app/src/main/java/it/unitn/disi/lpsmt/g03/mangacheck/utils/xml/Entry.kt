package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

// The basic XML entry.
// It contains all the data that can be useful to manipulate a  comic.

data class Entry(var list: String, val title: String?, val id: Int?, val image: String?, val description : String?)