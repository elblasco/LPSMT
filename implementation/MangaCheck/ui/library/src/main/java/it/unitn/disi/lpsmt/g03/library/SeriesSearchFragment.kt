package it.unitn.disi.lpsmt.g03.library

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import it.unitn.disi.lpsmt.g03.anilist.Anilist
import it.unitn.disi.lpsmt.g03.appdatabase.AppDatabase
import it.unitn.disi.lpsmt.g03.graphql.SearchByNameQuery
import it.unitn.disi.lpsmt.g03.library.databinding.SeriesSearchLayoutBinding
import it.unitn.disi.lpsmt.g03.mangacheck.MainActivity
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.SeriesSearchSelectorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Date
import java.time.Instant
import java.util.InputMismatchException

class SeriesSearchFragment : Fragment() {

    private var _binding: SeriesSearchLayoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val model: SeriesSearchModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).hideNavBar()
        _binding = SeriesSearchLayoutBinding.inflate(inflater, null, false)

        initAutocomplete()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MainActivity).showNavBar()
    }

    private fun initAutocomplete() {
        if (!binding.searchView.isSetupWithSearchBar) binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.addTransitionListener { _: SearchView, _: SearchView.TransitionState, newState: SearchView.TransitionState ->
            when (newState) {
                SearchView.TransitionState.SHOWING -> (requireActivity() as MainActivity).hideBars()
                SearchView.TransitionState.HIDING -> (requireActivity() as MainActivity).showBars()

                else -> return@addTransitionListener
            }
        }
        binding.searchView.editText.setOnEditorActionListener { it, _, _ ->
            queryDB(it, it.text.toString())
            false
        }

        binding.manualButton.setOnClickListener {
            it.visibility = View.GONE
            binding.searchBar.visibility = View.GONE
            binding.form.root.visibility = View.VISIBLE
            binding.form.addImageButton.visibility = View.VISIBLE
            enableView(
                binding.form.title,
                binding.form.description,
                binding.form.numberOfChapter,
                binding.form.addImageButton
            )
        }

        binding.addButton.setOnClickListener {
            try {
                testAndSetInputError(binding.form.title)
                addSeries()
                findNavController().popBackStack()
            } catch (mismatchException: InputMismatchException) {
                binding.form.root.visibility = View.VISIBLE
                Snackbar.make(
                    requireContext(), binding.root, mismatchException.message.toString(), Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        model.title.observe(viewLifecycleOwner) { newData -> binding.form.title.setText(newData) }
        model.description.observe(viewLifecycleOwner) { newData: String? ->
            binding.form.description.setText(Html.fromHtml(newData, Html.FROM_HTML_MODE_COMPACT))
        }
        model.chapters.observe(viewLifecycleOwner) { newData: Int? ->
            binding.form.numberOfChapter.setText(
                newData?.toString() ?: ""
            )
        }
        model.imageUrl.observe(viewLifecycleOwner) { newData: String? ->
            Glide.with(this).load(newData).into(binding.form.imageView)
        }

        val adapter = QueryAdapter(model) {
            binding.form.root.visibility = View.VISIBLE
            disableView(
                binding.form.title,
                binding.form.description,
                binding.form.numberOfChapter,
                binding.form.addImageButton
            )
            binding.searchView.hide()
        }
        binding.result.adapter = adapter
        binding.result.layoutManager = LinearLayoutManager(context)
        model.selectorList.observe(viewLifecycleOwner) { newData -> adapter.updateData(newData) }
    }

    private fun disableView(vararg views: View) {
        views.forEach { it.isEnabled = false }
    }

    private fun enableView(vararg views: View) {
        views.forEach { it.isEnabled = true }
    }

    private fun addSeries() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(context).seriesDao().insertAll(
                    Series(
                        title = binding.form.title.text!!.toString(),
                        status = ReadingState.READING,
                        isOne_shot = (binding.form.numberOfChapter.text!!.toString() != "") && (binding.form.numberOfChapter.text!!.toString()
                            .toInt() == 1),
                        lastAccess = Date(
                            Instant.now().toEpochMilli()
                        ),
                        description = binding.form.description.text?.toString(),
                        isOnline = true,
                        imageUrl = "",
                        chapters = try {
                            binding.form.numberOfChapter.text?.toString()?.toInt()
                        } catch (_: NumberFormatException) {
                            null
                        }
                    )
                )
            } catch (constraintException: SQLiteConstraintException) {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    "${getString(R.string.entry_already_exist)} ${binding.form.title.text.toString()}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun testAndSetInputError(input: EditText) {
        if (input.text.toString() != "") return

        input.error = getString(R.string.required_input)
        throw InputMismatchException(getString(R.string.required_inputs))
    }

    private fun queryDB(view: View, search: String) {
        view.isEnabled = false
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val res = Anilist.getInstance().searchByName(search).data?.Page?.media
                withContext(Dispatchers.Main) {
                    model.selectorList.value = res
                    view.isEnabled = true
                }
            }
        }
    }

    /**
     * ViewModel class to store and manage liveData
     */
    class SeriesSearchModel : ViewModel() {
        val selectorList: MutableLiveData<List<SearchByNameQuery.Medium?>> by lazy {
            MutableLiveData<List<SearchByNameQuery.Medium?>>()
        }
        val title: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val description: MutableLiveData<String> by lazy { MutableLiveData<String>() }
        val chapters: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
        val imageUrl: MutableLiveData<String> by lazy { MutableLiveData<String>() }

        val isRemoteImage: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    }

    /**
     * ViewModel class to store and manage liveData
     */
    class QueryAdapter(private val model: SeriesSearchModel, private val resultAction: () -> Unit) :
        RecyclerView.Adapter<QueryAdapter.ViewHolder>() {
        private var dataSet = List<SearchByNameQuery.Medium?>(0) { null }

        data class ViewHolder(val view: SeriesSearchSelectorBinding) : RecyclerView.ViewHolder(view.root)

        fun updateData(newData: List<SearchByNameQuery.Medium?>) {
            dataSet = newData
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = SeriesSearchSelectorBinding.inflate(LayoutInflater.from(parent.context))
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.view

            val englishTitle = dataSet[position]?.title?.english
            val romajiTitle = dataSet[position]?.title?.romaji
            val nativeTitle = dataSet[position]?.title?.native

            val title = englishTitle ?: romajiTitle ?: nativeTitle
            val description = dataSet[position]?.description
            val chapters = dataSet[position]?.chapters
            val imageUrl = dataSet[position]?.coverImage?.large

            view.containerMangaName.text = title

            view.containerMangaName.setOnClickListener {
                model.title.value = title
                model.description.value = description
                model.chapters.value = chapters
                model.imageUrl.value = imageUrl
                model.isRemoteImage.value = imageUrl.isNullOrBlank()
                resultAction()
            }
        }
    }
}