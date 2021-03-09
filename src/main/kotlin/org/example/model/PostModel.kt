package org.example.model

data class PostModel(

    val id: Long,
    val author: String,
    val content: String,
    val date: Long,

    // Location
    var address: String? = null,
    var lat: Double? = null,
    var lng: Double? = null,

    // Likes
    var likedCount: Int = 0,
    var dislikedCount: Int = 0,
    var likedByMe: Int = 0,     // 1 like, -1 dislike, 0 nothing

    // Reposts
    var repostCount: Int = 0,
    var repostByMe: Boolean = false,

    // Share
    var sharedCount: Int = 0,
    var sharedByMe: Boolean = false,

    // Comment
    // var commentsCount: Int = 0,
    // var commentsByMe: Boolean = false,
    // var comments: List<Comment>? = null

    // Other
    var videoUrl: String? = null,
    var advUrl: String? = null,

    var countViews: Int = 0,
    var postType: PostType = PostType.POST,


///tmp
    val source: PostModel? = null,
    val location: Location? = null,
    val link: String? = null,
    val attachment: AttachmentModel? = null
)
