package com.example.memeexplorer.common.data.cache.daos

import androidx.room.*
import com.example.memeexplorer.common.data.cache.model.CachedMeme
import io.reactivex.Flowable

@Dao
abstract class MemesDao {

    @Transaction
    @Query("SELECT * FROM memes ORDER BY memeId DESC")
    abstract fun getAllMemes(): Flowable<List<CachedMeme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMeme(
        meme: CachedMeme
    )


    @Delete
    abstract suspend fun deleteMeme(
        meme: CachedMeme
    )

    suspend fun insertMemes(memes: List<CachedMeme>) {
        for (meme in memes) {
            insertMeme(
                meme
            )
        }
    }

    suspend fun deleteMemes(memes: List<CachedMeme>) {
        for (meme in memes) {
            insertMeme(
                meme
            )
        }
    }

    @Transaction
    @Query("UPDATE memes SET tag=:tag WHERE location = :location")
    abstract fun update(location: String, tag: String)


    data class MemeUpdate(
        @ColumnInfo
        val location: String,
        val tag: String
    )

    fun updateMemes(memeMap: Map<String, String>) {
        memeMap.forEach { (key, value) -> update(key, value) }
    }


    @Transaction
    @Query(
        """
      SELECT * FROM memes
      WHERE tag LIKE '%' || :tag || '%' 
  """
    )
    abstract fun searchMemesBy(
        tag: String
    ): Flowable<List<CachedMeme>>
}