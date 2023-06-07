package it.unitn.disi.lpsmt.g03.mangacheck.library.xml

import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.LibraryEntry
import it.unitn.disi.lpsmt.g03.mangacheck.utils.xml.XMLEncoder
import android.content.Context
import it.unitn.disi.lpsmt.g03.mangacheck.R
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLEncoder(private val context: Context) : XMLEncoder<LibraryEntry> {

    override fun addEntry(entry: LibraryEntry){
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.library_XML)))
        val parentElement: Element = doc.getElementsByTagName("libraries").item(0) as Element

        val newLibrary = doc.createElement("library")

        val newTitle = doc.createElement("title")
        newTitle.textContent = entry.title

        val newId = doc.createElement("id")
        newId.textContent = entry.id.toString()


        newLibrary.appendChild(newTitle)
        newLibrary.appendChild(newId)

        parentElement.appendChild(newLibrary)


        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.library_XML)))
        )
    }

    // Remove a selected entry from the library list file
    override fun removeEntry(entry: LibraryEntry){
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.library_XML)))
        val parentElement: NodeList = doc.getElementsByTagName("library")

        for(index in 0 until parentElement.length){
            val element = parentElement.item(index) as Element
            if(element.getElementsByTagName("id").item(0).textContent == entry.id.toString()){
                element.parentNode.removeChild(element)
            }
        }
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.library_XML)))
        )
    }

    override fun modifyEntry(entry: LibraryEntry, fieldName: String, newValue: String) {
        TODO("Not yet implemented")
    }
}