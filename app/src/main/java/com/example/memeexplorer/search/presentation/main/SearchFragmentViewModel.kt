package com.example.memeexplorer.search.presentation.main

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.memeexplorer.MemeExplorerApplication
import com.example.memeexplorer.common.domain.model.NoMoreMemesException
import com.example.memeexplorer.common.domain.model.meme.Meme
import com.example.memeexplorer.common.domain.model.pagination.Pagination
import com.example.memeexplorer.common.presentation.model.mappers.UiMemeMapper
import com.example.memeexplorer.common.utils.createExceptionHandler
import com.example.memeexplorer.common.utils.workers.GetLocalImagesWorker
import com.example.memeexplorer.common.utils.workers.OCRWorker
import com.example.memeexplorer.common.utils.workers.WorkerConstants.KEY_IMAGE_PATH
import com.example.memeexplorer.search.domain.Constants.KEY_IMAGE_INDEX
import com.example.memeexplorer.search.domain.Constants.KEY_IMAGE_URI
import com.example.memeexplorer.search.domain.Constants.OCR_WORK_NAME
import com.example.memeexplorer.search.domain.model.SearchParameters
import com.example.memeexplorer.search.domain.model.SearchResults
import com.example.memeexplorer.search.domain.usecases.*
import com.kh69.logging.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val uiMemeMapper: UiMemeMapper,
    private val searchMemes: SearchMemes,
    private val getMemes: GetMemes,
    private val deleteMemes: DeleteMemes,
    private val fetchImages: FetchImages,
    private val requestNextPageOfMemes: RequestNextPageOfMemes,
    private val storeMemes: StoreMemes,
    private val updateMemeTags: UpdateMemeTags,
    private val compositeDisposable: CompositeDisposable
) : ViewModel(), CoroutineScope {

    private var currentPage = 0
    private var runCount = 0
    private var oldMemeList = emptyList<Meme>()
    private var searchJob: Job = Job()

    private val _state = MutableStateFlow(SearchViewState())
    private val querySubject = BehaviorSubject.create<String>()
    private val idSubject = BehaviorSubject.createDefault("")
    private val pathSubject = BehaviorSubject.createDefault("")

    private val workManager = WorkManager.getInstance(MemeExplorerApplication.sAppContext)

    internal fun cancelWork() {
        workManager.cancelUniqueWork(OCR_WORK_NAME)
    }

    val state: StateFlow<SearchViewState> = _state.asStateFlow()

    var isLoadingMoreMemes: Boolean = false
        private set

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.RequestInitialMemesList -> loadMemes()
            is SearchEvent.FetchImages -> getImages()
            is SearchEvent.PrepareForSearch -> prepareForSearch()
            is SearchEvent.IsLoadingMemes -> updateLoading(event.isLoadingMemes)
            else -> onSearchParametersUpdate(event)
        }
    }

    private fun getImages() {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("starting to scan")
            val paths = async { fetchImages() }
            syncWithDB(paths.await())
            Logger.d("done scanning got ${paths.await().size}")
        }
    }

    private fun buildInputDataForOCR(memeUri: String?, index: Int): Data {
        val builder = Data.Builder()
        if (memeUri != null) {
            builder.putString(KEY_IMAGE_URI, memeUri.toString())
            builder.putInt(KEY_IMAGE_INDEX, index)
        }
        return builder.build()
    }

    private fun buildOcrRequests(memes: List<Meme>): List<OneTimeWorkRequest> {
        val ocrRequests = mutableListOf<OneTimeWorkRequest>()

        for (i in memes.indices) {
            val memeUri = memes[i].mLocation

            val ocrRequest = OneTimeWorkRequest.Builder(OCRWorker::class.java)
                .setInputData(buildInputDataForOCR(memeUri, i))
                .build()
            ocrRequests.add(ocrRequest)
        }
        return ocrRequests
    }

    private fun doOCRWork(memes: List<Meme>) {
        if (memes.isEmpty()) return

        val recognizeText = buildOcrRequests(memes)
        val workManager = WorkManager.getInstance(MemeExplorerApplication.sAppContext)
        workManager.beginWith(recognizeText).enqueue()

//        var continuation = workManager.beginUniqueWork(
//            OCR_WORK_NAME,
//            ExistingWorkPolicy.APPEND_OR_REPLACE,
//            OneTimeWorkRequest.from(GetLocalImagesWorker::class.java)
//        )

//        for (meme in memes) {
//            val ocrBuilder = OneTimeWorkRequestBuilder<OCRWorker>()
//            ocrBuilder.setInputData(
//                createInputDataForPath(
//                    meme.mLocation
//                )
//            )
//
//            continuation = continuation.then(ocrBuilder.build())
////            val save = OneTimeWorkRequestBuilder<SyncDBWorker>().build()
////            continuation = continuation.then(save)
//        }
//        continuation.enqueue()
    }

    private fun syncWithDB(newPaths: ArrayList<String>) {
        compareWithSavedPaths(
            ArrayList(_state.value.memes.map { it.location }), newPaths
        )
    }


    private fun createInputDataForPath(path: String): Data =
        Data.Builder().putString(KEY_IMAGE_PATH, path).build()


    private fun compareWithSavedPaths(
        oldPaths: ArrayList<String>, newPaths: ArrayList<String>
    ) {
        val newImages = newPaths.toSet().minus(oldPaths.toSet())
        val deletedImages = oldPaths.toSet().minus(newPaths.toSet())

        val errorMessage = "Failed to sync"
        val exceptionHandler = viewModelScope.createExceptionHandler(errorMessage) { onFailure(it) }

        viewModelScope.launch(exceptionHandler) {
            storeMemes(ArrayList(newImages))
            deleteMemes(ArrayList(deletedImages))
        }

    }

    private fun updateLoading(isLoading: Boolean) {
        isLoadingMoreMemes = isLoading
    }

    init {
//        cancelWork()
        _state.value = SearchViewState()
        subscribeToMemeUpdates()
    }

    private fun subscribeToMemeUpdates() {
        getMemes().observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onNewMemeList(it) }, { onFailure(it) }).addTo(compositeDisposable)
    }

    private fun onNewMemeList(memes: List<Meme>) {
        runCount++
        Logger.d("Got more memes!")

        val meems = memes.map { uiMemeMapper.mapToView(it) }

        val currentList = state.value.memes
        val newMemes = meems.subtract(currentList.toSet())
        val updatedList = currentList + newMemes

        _state.value = state.value.copy(loading = false, memes = updatedList)

        if (runCount == 1) doOCRWork(memes.filter { it.mTag == "NO_TAG" })

    }

    suspend fun saveMemes(paths: List<String>) {
        storeMemes(paths)
//        updateMemeTags(paths)
    }

    private fun loadMemes() {
        if (state.value.memes.isEmpty()) {
            loadNextMemePage()
        }
    }

    private fun loadNextMemePage() {
        isLoadingMoreMemes = true
        val errorMessage = "Failed to fetch memes"
        val exceptionHandler = viewModelScope.createExceptionHandler(errorMessage) { onFailure(it) }

        viewModelScope.launch(exceptionHandler) {
            Logger.d("Requesting more memes.")
            val pagination = requestNextPageOfMemes(++currentPage)

            onPaginationInfoObtained(pagination)
            isLoadingMoreMemes = false
        }
    }

    private fun onPaginationInfoObtained(pagination: Pagination) {
        currentPage = pagination.currentPage
    }

    private fun prepareForSearch() {
        setupSearchSubscription()
    }

    private fun setupSearchSubscription() {
        searchMemes(querySubject, idSubject, pathSubject).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ onSearchResults(it) }, { onFailure(it) }).addTo(compositeDisposable)
    }

    private fun onSearchResults(searchResults: SearchResults) {
        val (memes, searchParameters) = searchResults

        if (memes.isEmpty()) {
            onEmptyCacheResults(searchParameters)
        } else {
            onMemeList(memes)
        }
    }

    private fun onMemeList(memes: List<Meme>) {
        _state.update { oldState ->
            oldState.updateToHasSearchResults(memes.map { uiMemeMapper.mapToView(it) })
        }
    }

    private fun onEmptyCacheResults(searchParameters: SearchParameters) {
        _state.update { oldState ->
            oldState.updateToSearchingRemotely()
        }
    }

    private fun onSearchParametersUpdate(event: SearchEvent) {
        searchJob.cancel( // cancels the job
            CancellationException("New search parameters incoming!")
        )

        when (event) {
            is SearchEvent.QueryInput -> updateQuery(event.input)
            else -> Logger.d("Wrong SearchEvent in onSearchParametersUpdate!")
        }
    }

    private fun createExceptionHandler(message: String): CoroutineExceptionHandler {
        return viewModelScope.createExceptionHandler(message) {
            onFailure(it)
        }
    }

    private fun updateQuery(input: String) {
        resetPagination()

        querySubject.onNext(input)

        if (input.isEmpty()) {
            setNoSearchQueryState()
        } else {
            setSearchingState()
        }
    }

    private fun resetPagination() {
        currentPage = 0
    }

    private fun setNoSearchQueryState() {
        _state.update { oldState -> oldState.updateToNoSearchQuery() }
    }

    private fun setSearchingState() {
        _state.update { oldState -> oldState.updateToSearching() }
    }


    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private var imagesLiveData: MutableLiveData<List<String>> = MutableLiveData()

    private fun onFailure(throwable: Throwable) {
        _state.update { oldState ->
            if (throwable is NoMoreMemesException) {
                oldState.updateToNoResultsAvailable()
            } else {
                oldState.updateToHasFailure(throwable)
            }
        }
    }

}