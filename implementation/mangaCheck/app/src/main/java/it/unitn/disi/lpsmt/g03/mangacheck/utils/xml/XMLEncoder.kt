package it.unitn.disi.lpsmt.g03.mangacheck.utils.xml

import android.content.Context
import android.util.Log
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.reading_list.ReadingListFragment
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class XMLEncoder(private val context: Context) {

    // Manipulate the XML to add a new entry given the nav args
    fun addMangaEntry(
        mangaList: String,
        mangaName: String,
        mangaId: Int,
        mangaImageBase64: String,
        mangaDescription: String
    ) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.XML_file)))
        val parentElement: Element = doc.getElementsByTagName("comics").item(0) as Element

        val newManga = doc.createElement("comic")

        val mangaListInXml = doc.createElement("list")
        mangaListInXml.textContent = mangaList

        val newTitle = doc.createElement("title")
        newTitle.textContent = mangaName

        val newId = doc.createElement("id")
        newId.textContent = mangaId.toString()

        val newImage = doc.createElement("image")
        newImage.textContent = mangaImageBase64

        val newDescription = doc.createElement("description")
        newDescription.textContent = mangaDescription

        newManga.appendChild(mangaListInXml)
        newManga.appendChild(newTitle)
        newManga.appendChild(newId)
        newManga.appendChild(newImage)
        newManga.appendChild(newDescription)

        parentElement.appendChild(newManga)


        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.XML_file)))
        )

        Log.v(
            ReadingListFragment::class.simpleName,
            context.applicationContext!!.openFileInput(context.getString(R.string.XML_file))
                .bufferedReader().readText()
        )
    }

    fun addLibraryEntry(libraryName : String, libraryId : Int, libraryImageBase64 : String){
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.library_XML)))
        val parentElement: Element = doc.getElementsByTagName("libraries").item(0) as Element

        val newLibrary = doc.createElement("library")

        val newTitle = doc.createElement("title")
        newTitle.textContent = libraryName

        val newId = doc.createElement("id")
        newId.textContent = libraryId.toString()

        val newImage = doc.createElement("image")
        newImage.textContent = libraryImageBase64

        newLibrary.appendChild(newTitle)
        newLibrary.appendChild(newId)
        newLibrary.appendChild(newImage)

        parentElement.appendChild(newLibrary)


        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.library_XML)))
        )
    }

    // Remove a selected entry from the library list file
    fun removeLibraryEntry(libraryName : String){
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.library_XML)))
        val parentElement: NodeList = doc.getElementsByTagName("library")

        for(index in 0 until parentElement.length){
            val element = parentElement.item(index) as Element
            if(element.getElementsByTagName("title").item(0).textContent == libraryName){
                element.parentNode.removeChild(element)
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.library_XML)))
        )
    }

    // Modify the list of a comic/library
    fun modifyEntry(comic: MangaEntry, newList: String) {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document =
            builder.parse(context.openFileInput(context.getString(R.string.XML_file)))

        // Find the comic to modify
        val listOfAllComics: NodeList = doc.getElementsByTagName("comic")

        Log.e("test","test")

        for (index in 0 until listOfAllComics.length){
            val currentMangaEntry = listOfAllComics.item(index) as Element
            if(currentMangaEntry.getElementsByTagName("title").item(0).textContent == comic.title){
                currentMangaEntry.getElementsByTagName("list").item(0).textContent = newList
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(
            DOMSource(doc),
            StreamResult(File(context.filesDir, context.getString(R.string.XML_file)))
        )
    }
}