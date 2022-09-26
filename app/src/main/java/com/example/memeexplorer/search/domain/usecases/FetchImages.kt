package com.example.memeexplorer.search.domain.usecases

import com.CodeBoy.MediaFacer.MediaFacer
import com.CodeBoy.MediaFacer.PictureGet
import com.example.memeexplorer.MemeExplorerApplication
import com.example.memeexplorer.activities.MainActivity
import javax.inject.Inject


class FetchImages @Inject constructor() {
    operator fun invoke() = MediaFacer.withPictureContex(MemeExplorerApplication.sAppContext)
        .getAllPictureContents(PictureGet.externalContentUri)
        .map { it.picturePath } as ArrayList<String>
}