package org.example.dto.request

import org.example.model.Location
import org.example.model.PostModel
import org.example.model.PostType

data class PostRequestDto(
    val id: Long,
    val author: String,
    val content: String,
    val created: Long,
    val likedByMe: Boolean = false,
    val countLikes: Int = 10,
    val commentedByMe: Boolean = false,
    val countComments: Int = 0,
    val sharedByMe: Boolean = false,
    val countShares: Int = 0,
    val videoUrl: String? = null,
    val type: PostType = PostType.POST,
    val source: PostModel? = null,
    val address: String? = null,
    val location: Location? = null,
    val link: String? = null
) {
    companion object {
        fun toModel(dto: PostRequestDto) = PostModel(
            id = dto.id,
            author = dto.author,
            content = dto.content,
            date = dto.created,
            likedByMe = if(dto.likedByMe) 1 else 0,
            likedCount = dto.countLikes,
            // commentedByMe = dto.commentedByMe,
            //  countComments = dto.countComments,
            sharedByMe = dto.sharedByMe,
            sharedCount = dto.countShares,
            videoUrl = dto.videoUrl,
            postType = dto.type,
            source = dto.source,
            address = dto.address,
            location = dto.location,
            link = dto.link
        )
    }
}