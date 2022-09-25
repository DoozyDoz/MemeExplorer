package com.example.memeexplorer.utilities

import android.content.Context
import com.CodeBoy.MediaFacer.MediaFacer.withPictureContex
import com.CodeBoy.MediaFacer.PictureGet
import com.example.memeexplorer.helpers.TinyDB
import com.example.memeexplorer.memeClasses.ImageDataModel
import com.example.memeexplorer.memeClasses.MemeLab
import kotlinx.coroutines.*
import java.io.File
import java.net.URLConnection

class ArrayListSaverInterfaceKT(context: Context) {

    private var newArray: ArrayList<String>? = null
    var allImages = ArrayList<ImageDataModel>()

    private var mContext: Context? = null
    private val c = 0
    private var tinydb: TinyDB? = null

    init {
        mContext = context
        newArray = ArrayList()
        tinydb = TinyDB(mContext)
        runBlocking {
            allImages = withContext(Dispatchers.Default) {
                gettAllImages(mContext!!)
            }
        }
    }


    fun getUnFilteredImageListPaths(): ArrayList<String>? {
        val pathArray = java.util.ArrayList<String>()
        var images: ArrayList<ImageDataModel> = allImages
        for (image in images) {
            if (File(image.imagePath).exists() && isImageFile(image.imagePath)) {
                pathArray.add(image.imagePath)
            }
        }
        return pathArray
    }

    fun getPathsArray(): ArrayList<String>? {
        for (image in allImages) {
            if (File(image.imagePath).exists()) {
                newArray!!.add(image.imagePath)
            }
        }
        return readArraylist()
    }

    fun saveArraylist(paths: ArrayList<String>?) {
        tinydb!!.clear()
        tinydb!!.putListString(Constants.SHARED_PREFS_FILE, paths)
    }

    fun readArraylist(): ArrayList<String>? {
        val oldArrayFromDb = ArrayList<String>()
        val oldArrayMemes = MemeLab.get(mContext).memes
        for (i in oldArrayMemes.indices) {
            oldArrayFromDb.add(oldArrayMemes[i].location)
        }
        val oldArray: ArrayList<String> = tinydb!!.getListString(Constants.SHARED_PREFS_FILE)
        saveArraylist(newArray!!)
        return if (oldArrayFromDb.size == 0) {
            newArray
        } else if (oldArrayFromDb.size != oldArray.size) {
            compareArraylist(newArray!!, oldArrayFromDb)
        } else {
            val newA: ArrayList<String> = newArray!!.clone() as ArrayList<String>
            compareArraylist(newA, oldArray)
        }
    }

    fun compareArraylist(
        nArray: ArrayList<String>,
        oArray: ArrayList<String>?
    ): ArrayList<String>? {
        nArray.removeAll(oArray!!.toSet())
        return nArray
    }

    fun gettAllImages(activity: Context): ArrayList<ImageDataModel> {
        allImages.clear()
        val allPhotos =
            withPictureContex(activity)
                .getAllPictureContents(PictureGet.externalContentUri)
                .map {
                    ImageDataModel(
                        it.picturName,
                        it.picturePath
                    )
                } as ArrayList<ImageDataModel>
        allImages.addAll(allPhotos)
        return allImages
    }

    fun isImageFile(path: String?): Boolean {
        val mimeType = URLConnection.guessContentTypeFromName(path)
        return mimeType != null && mimeType.startsWith("image")
    }
}