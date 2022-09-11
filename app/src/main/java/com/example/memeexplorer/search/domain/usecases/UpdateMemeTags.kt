package com.example.memeexplorer.search.domain.usecases

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.example.memeexplorer.common.domain.repositories.MemeRepository
import com.example.memeexplorer.common.utils.DispatchersProvider
import com.example.memeexplorer.common.utils.tess.TessEngine
import com.example.memeexplorer.common.utils.toMeme
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class UpdateMemeTags @Inject constructor(
    private val memeRepository: MemeRepository,
    private val dispatchersProvider: DispatchersProvider
) {
    suspend operator fun invoke(
        imagePaths: List<String>
    ) {
        return withContext(dispatchersProvider.io()) {
            val tessEngine: TessEngine = TessEngine.Generate()
            imagePaths.forEach {
//                val bitmap = convertPathToBitmap(it)
//                val result = tessEngine.detectText(bitmap)
                val result = tessEngine.detectText(it)
            }


//            memeRepository.storeMemes(imagePaths.map { it.toMeme() })
        }
    }

    fun convertPathToBitmap(filepath: String?): Bitmap? {
        val sd = Environment.getExternalStorageDirectory()
        val image = File(filepath)
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(image.absolutePath, bmOptions)
        return if (bitmap != null) {
            Bitmap.createScaledBitmap(bitmap, 400, 400, true)
        } else null
    }
}