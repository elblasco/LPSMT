package it.unitn.disi.lpsmt.g03.mangacheck.utils.http

import android.content.Context
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import it.unitn.disi.lpsmt.g03.mangacheck.R
import it.unitn.disi.lpsmt.g03.mangacheck.utils.formatterQuery.QueryResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.ConnectException

class ServerRequest(private val context: Context, private val mangaID: Int?) {

    private val client = HttpClient()
    private val ipAddr: String = context.getString(R.string.ip_addr)
    private val serverPort: Int = context.getString(R.string.server_port).toInt()

    // This is the only function of this class that has to be called from a routine
    suspend fun queryNames(mangaName: String): Array<Array<String>> {
        lateinit var formattedResponse: Array<Array<String>>
        try {
            val response: HttpResponse = client.get {
                url {
                    host = ipAddr
                    port = serverPort
                    path("search/${mangaName}")
                }
            }
            if (response.status.value in 200..299) {
                formattedResponse = QueryResult().parsing(response.body())
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
        return formattedResponse
    }


    // Make a request to the server for the Manga cover image
    suspend fun queryImage() {
        val filePath = File(context.cacheDir, "image/$mangaID")
        if (!filePath.exists()) {


                try {
                    val response: HttpResponse = client.get {
                        url {
                            host = ipAddr
                            port = serverPort
                            path("image/${mangaID}")
                        }
                    }
                    if (response.status.value in 200..299) {
                        withContext(Dispatchers.IO) {
                            val imageString = response.body<ByteArray>()
                            filePath.writeBytes(imageString)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            toaster("Error ${response.status.value}")
                        }
                    }
                } catch (e: ConnectException) {
                    withContext(Dispatchers.Main) {
                        toaster("Connection Refused")
                    }
                }
            }

    }

    fun queryDescription(): String {
        val scope = CoroutineScope(Dispatchers.IO)
        var responseToReturn = String()

        scope.launch {
            try {
                val response: HttpResponse = client.get {
                    url {
                        host = ipAddr
                        port = serverPort
                        path("description/${mangaID}")
                    }
                }
                if (response.status.value in 200..299) {
                    responseToReturn = response.body()
                }
            } catch (e: ConnectException) {
                withContext(Dispatchers.Main) {
                    toaster("Connection Refused")
                }
            }
        }
        return responseToReturn
    }

    // Prepare a delicious Toast for you
    private fun toaster(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}