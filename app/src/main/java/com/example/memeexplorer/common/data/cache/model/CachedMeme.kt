package com.example.memeexplorer.common.data.cache.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.memeexplorer.common.domain.model.meme.Meme
import java.util.*

@Entity(
    tableName = "memes",
    indices = [Index("tag")]
)
data class CachedMeme(
    @PrimaryKey
    val memeId: UUID,
    val location: String,
    val tag: String
) {
    companion object {
        fun fromDomain(domainModel: Meme): CachedMeme {
            return CachedMeme(
                memeId = domainModel.mId,
                location = domainModel.mLocation,
                tag = domainModel.mTag
            )
        }
    }

    fun toDomain(): Meme {
        return Meme(
            mId = memeId,
            mLocation = location,
            mTag = tag
        )
    }
}