package it.unitn.disi.lpsmt.g03.mangacheck.add_library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.add_library.data.AddLibraryAdapter
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.AddReadingSelectByNameBinding
import it.unitn.disi.lpsmt.g03.mangacheck.utils.manipulators.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddLibraryFragment : Fragment() {

    private var _binding: AddReadingSelectByNameBinding? = null

    private lateinit var searchButton: Button
    private lateinit var textBox: EditText
    private lateinit var linearLayout: LinearLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddReadingSelectByNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchButton = binding.submitButton
        textBox = binding.comicName
        linearLayout = binding.listsResultsQueryByName

        searchButton.setOnClickListener {
            // This trigger a waterfall of function
            // queryServerAndUpdateUI -> parsing -> updateUI
            linearLayout.post {
                queryServerAndUpdateUI()
            }
        }
    }

    private fun queryServerAndUpdateUI() {
        val client = HttpClient()
        lateinit var formattedResponse: Array<Array<String>>
        val scope = CoroutineScope(Dispatchers.IO)
        val ipAddr: String = this.requireContext().getString(R.string.ip_addr)
        val serverPort: Int = this.requireContext().getString(R.string.server_port).toInt()
        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("search/${textBox.text}")
                    }
                }
                if (response.status.value in 200..299) {
                    formattedResponse = QueryResult().parsing(response.body())
                    updateUI(formattedResponse)
                } else {
                    withContext(Dispatchers.Main) {
                        toaster("Error ${response.status.value}")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
    }

    // If the response is empty it creates a dummy button with ID -1 and an error as a text
    private fun updateUI(response: Array<Array<String>>) {
        linearLayout.post {
            linearLayout.removeAllViews()
            if (response.isNotEmpty()) {
                response.forEachIndexed { index, internalArray ->
                    val libraryEntry = AddLibraryAdapter(
                        internalArray[1],
                        this
                    )
                    linearLayout.addView(libraryEntry.getView(internalArray[0].toInt(), null, null))

                }
            } else {
                val comicEntry = AddLibraryAdapter(
                    "Manga doesn't exist",
                    this
                )
                linearLayout.addView(comicEntry.getView(-1, null, null))
                toaster("Manga doesn't exist")
            }
        }
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}