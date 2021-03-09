package org.example.service

import io.ktor.features.*
import org.example.dto.request.CreatePostRequestDto
import org.example.dto.request.PostRequestDto
import org.example.dto.request.PostsCreatedBeforeRequestDto
import org.example.dto.request.RepostRequestDto
import org.example.dto.responce.PostResponseDto
import org.example.exception.ForbiddenException
import org.example.model.PostModel
import org.example.model.PostType
import org.example.model.UserModel
import org.example.repository.PostRepository

class PostService(private val repo: PostRepository) {

    suspend fun getAll(): List<PostResponseDto> = repo.getAll().map(PostResponseDto.Companion::fromModel)

    suspend fun getById(id: Long): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()
        return PostResponseDto.fromModel(model)
    }

    suspend fun save(request: PostRequestDto, user: UserModel): PostResponseDto {
        if (user.username != request.author) { //TODO реализовать через id
            throw ForbiddenException("Невозможно редактировать!")
        }
        return PostResponseDto.fromModel(PostRequestDto.toModel(request))
    }

    suspend fun deleteById(id: Long, user: UserModel): PostResponseDto {
        val model = repo.getById(id) ?: throw NotFoundException()

        if (user.username != model.author) {
            throw ForbiddenException("Невозможно удалить пост!")
        }

        val response = PostResponseDto.fromModel(model)
        repo.removeById(id)
        return response
    }

    suspend fun likeById(id: Long): PostResponseDto {
        return PostResponseDto.fromModel(repo.getById(id) ?: throw NotFoundException())
    }

    suspend fun dislikeById(id: Long): PostResponseDto {
        return PostResponseDto.fromModel(repo.getById(id) ?: throw NotFoundException())
    }

    suspend fun repostById(id: Long, user: UserModel, repostRequestDto: RepostRequestDto): PostResponseDto {
        val reposted = repo.getById(id)
        val newPostForSave = PostModel(
            id = -1,
            author = user.username,
            content = repostRequestDto.content,
            date = System.currentTimeMillis(),
            postType = PostType.REPOST,
            source = reposted
        )
        val repost = repo.save(newPostForSave)
        return PostResponseDto.fromModel(repost)
    }

    suspend fun getRecent(count: Int): List<PostResponseDto> {
      //  val recent = repo.getRecent(count)
       // return recent.map(PostResponseDto.Companion::fromModel)
       TODO( "Not impl")
    }

    suspend fun getPostsAfter(id: Long): List<PostResponseDto> {
     //   val newPosts = repo.getPostsAfter(id)
     //   return newPosts.map(PostResponseDto.Companion::fromModel)
        TODO( "Not impl")
    }

    suspend fun getPostsCreatedBefore(dto: PostsCreatedBeforeRequestDto): List<PostResponseDto> {
       // val oldPosts = repo.getPostsCreatedBefore(dto.idCurPost, dto.countUploadedPosts)
      //  return oldPosts.map(PostResponseDto.Companion::fromModel)
        TODO( "Not impl")
    }

    suspend fun createPost(createPostRequestDto: CreatePostRequestDto, user: UserModel): PostResponseDto {
        val newPost = PostModel(
            id= -1,
            author = user.username,
            content = createPostRequestDto.content,
            date = System.currentTimeMillis(),
            attachment = createPostRequestDto.attachment
        )

        return PostResponseDto.fromModel(repo.save(newPost))
    }
}