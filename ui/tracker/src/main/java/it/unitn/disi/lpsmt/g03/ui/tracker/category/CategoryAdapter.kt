package it.unitn.disi.lpsmt.g03.ui.tracker.category

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import it.unitn.disi.lpsmt.g03.core.ImageLoader
import it.unitn.disi.lpsmt.g03.data.library.Chapter
import it.unitn.disi.lpsmt.g03.data.library.ChapterDao
import it.unitn.disi.lpsmt.g03.data.library.ReadingState
import it.unitn.disi.lpsmt.g03.data.library.Series
import it.unitn.disi.lpsmt.g03.data.library.SeriesDao
import it.unitn.disi.lpsmt.g03.ui.tracker.R
import it.unitn.disi.lpsmt.g03.ui.tracker.SelectionManager
import it.unitn.disi.lpsmt.g03.ui.tracker.TrackerFragmentDirections
import it.unitn.disi.lpsmt.g03.ui.tracker.databinding.TrackerCardBinding
import it.unitn.disi.lpsmt.g03.ui.tracker.dialog.ModifyDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Integer.max
import it.unitn.disi.lpsmt.g03.core.R as Rc

class CategoryAdapter(
    private val ctx: Context,
    private val activity: AppCompatActivity,
    val name: ReadingState,
    private val glide: RequestManager,
    private val manager: FragmentManager,
    private val lifeCycle: LifecycleOwner,
    private val navController: NavController,
    private val selectionManager: SelectionManager
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ModifyDialogEntryPoint {
        fun provideSeriesDao(): SeriesDao
        fun provideChapterDao(): ChapterDao
    }

    private var seriesDao: SeriesDao
    private var chapterDao: ChapterDao

    lateinit var view: View
    private var dataSet: List<Series> = emptyList()
    private val liveDataSet: LiveData<List<Series>>

    private val requestOptions = RequestOptions().transform(FitCenter(),
        RoundedCorners(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            8f,
            Resources.getSystem().displayMetrics).toInt()))

    init {
        val myLibraryAdapterEntryPoint = EntryPointAccessors.fromApplication(ctx,
            ModifyDialogEntryPoint::class.java)
        seriesDao = myLibraryAdapterEntryPoint.provideSeriesDao()

        val myCategoryAdapterEntryPoint = EntryPointAccessors.fromApplication(ctx,
            ModifyDialogEntryPoint::class.java)
        chapterDao = myCategoryAdapterEntryPoint.provideChapterDao()

        liveDataSet = seriesDao.getAllByStatus(name)
    }

    fun start() {
        liveDataSet.observe(lifeCycle) { newData ->
            if (newData.isEmpty()) view.visibility = View.GONE
            else view.visibility = View.VISIBLE
            val len = max(dataSet.size, newData.size)
            dataSet = newData
            notifyItemRangeChanged(0, len)
        }
    }

    /**
     * Provide a reference to the type of views that you are using
     */
    inner class ViewHolder(val view: TrackerCardBinding) : RecyclerView.ViewHolder(view.root) {
        private var seriesCover: ImageView = view.seriesCover
        private var seriesTitle: TextView = view.seriesTitle
        private var chCounter: TextView = view.chCounter
        private var modifyButton: Button = view.modifyButton

        private inner class SelectionCallback(val color: SurfaceColor) : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(Rc.menu.menu_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                view.root.setCardBackgroundColor(color.colorSurface)
                return when (item?.itemId) {
                    Rc.id.action_delete -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            selectionManager.selected?.let { seriesDao.delete(it) }
                        }
                        selectionManager.actionMode?.finish()
                        selectionManager.actionMode = null
                        true
                    }

                    Rc.id.action_modify -> {
                        val dialogFragment = selectionManager.selected?.let {
                            ModifyDialog(ctx,
                                it)
                        }
                        dialogFragment?.show(manager, "ModifyDialog")
                        selectionManager.actionMode?.finish()
                        selectionManager.actionMode = null
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                view.root.setCardBackgroundColor(color.colorSurface)
                selectionManager.selected = null
                selectionManager.actionMode?.finish()
                selectionManager.actionMode = null
            }
        }

        private inner class SurfaceColor(val colorSurface: Int, val colorSurfaceVariant: Int)

        private fun getColor(): SurfaceColor {
            val typedValue = TypedValue()
            val a: TypedArray = view.root.context.obtainStyledAttributes(typedValue.data,
                intArrayOf(com.google.android.material.R.attr.colorSurface,
                    com.google.android.material.R.attr.colorSurfaceVariant))
            val colorSurface = a.getColor(0, 0)
            val colorSurfaceVariant = a.getColor(a.getIndex(1), 0)
            a.recycle()
            return SurfaceColor(colorSurface, colorSurfaceVariant)
        }

        fun bind(item: Series) {

            val color = getColor()

            glide.load(item.imageUri)
                .error(glide.load(R.drawable.baseline_broken_image_24))
                .apply(requestOptions)
                .into(seriesCover)

            // Set the Series title in the card
            seriesTitle.text = item.title

            // Set the Series chapter counter in the card
            if (item.chapters != null) chCounter.text = "ch. ${item.chapters.toString()}"
            else chCounter.text = null

            modifyButton.setOnClickListener {
                val dialogFragment = ModifyDialog(ctx, item)
                dialogFragment.show(manager, "CustomDialog")
            }

            view.root.setOnClickListener {
                onClickToReader(item)
            }

            view.root.setOnLongClickListener {
                view.root.setCardBackgroundColor(color.colorSurfaceVariant)
                if (selectionManager.actionMode == null) {
                    val currentActivity = activity
                    selectionManager.actionMode = currentActivity.startSupportActionMode(
                        SelectionCallback(color))
                    selectionManager.selected = item
                    selectionManager.actionMode?.title = "${selectionManager.selected?.title} selected"
                } else {
                    selectionManager.selected = item
                    selectionManager.actionMode?.title = "${selectionManager.selected?.title} selected"
                }
                true
            }
        }

        private fun onClickToReader(item: Series) {
            if (name == ReadingState.READING) {
                val queryChapter: LiveData<Chapter> = chapterDao.getChapterFromChNum(item.uid,
                    item.lastChapterRead)

                queryChapter.observe(lifeCycle) { chapter ->
                    if (chapter != null) {
                        if(chapter.file?.let { ImageLoader.fileIsAccessible(it,ctx) } != true) return@observe
                        val direction: NavDirections = TrackerFragmentDirections.actionTrackerToLastRead()
                        val bundle = bundleOf("chapter" to chapter)
                        direction.arguments.putAll(bundle)
                        navController.navigate(direction)
                    } else {
                        val bundle: Bundle = Bundle().apply { putParcelable("Series", item) }
                        navController.createDeepLink()
                            .setDestination(it.unitn.disi.lpsmt.g03.ui.library.R.id.chapter_list,
                                bundle)
                            .createPendingIntent()
                            .send()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TrackerCardBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }
}