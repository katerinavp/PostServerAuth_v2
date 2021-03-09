package org.example.dto.request

import org.example.model.AttachmentModel

data class CreatePostRequestDto(val content: String, val attachment: AttachmentModel?)