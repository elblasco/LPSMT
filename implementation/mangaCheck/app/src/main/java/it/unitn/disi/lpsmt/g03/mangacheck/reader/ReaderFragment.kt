package it.unitn.disi.lpsmt.g03.mangacheck.reader

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ReaderLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.reader.data.ReaderAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ReaderFragment : Fragment() {

    private var currentPage: Int = 0
    private var _binding: ReaderLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var readerAdapter: ReaderAdapter
    private val argument: ReaderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ReaderLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val uri = Uri.fromFile(File(argument.zipPath))
            readerAdapter = ReaderAdapter(uri, requireContext())

            withContext(Dispatchers.Main) {
                binding.mangaView.addView(
                    onPageChange(0), 0
                )
                binding.bottomBar.previous.setOnClickListener {
                    if (currentPage > 0) {
                        onPageChange(currentPage - 1)
                    }
                }
                binding.bottomBar.search.setOnClickListener {
                    val dialog = SearchDialogFragment(currentPage)
                    childFragmentManager.setFragmentResultListener(
                        "search", viewLifecycleOwner
                    ) { result, bundle ->
                        val page = bundle.getInt("page")
                        if (result == "search" && page in 0..readerAdapter.getCount()) onPageChange(page)
                    }
                    dialog.show(childFragmentManager, "search")
                }
                binding.bottomBar.forward.setOnClickListener {
                    if (currentPage < readerAdapter.getCount()) {
                        onPageChange(currentPage + 1)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String? = ReaderFragment::class.simpleName
    }

    private fun onPageChange(page: Int): View {
        currentPage = page
        return if (binding.mangaView.childCount > 0) readerAdapter.getView(
            page, binding.mangaView[0], binding.mangaView.width, binding.mangaView.height
        ) else readerAdapter.getView(page, null, binding.mangaView.width, binding.mangaView.height)
    }
}