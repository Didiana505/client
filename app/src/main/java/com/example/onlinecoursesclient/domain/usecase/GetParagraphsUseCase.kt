package com.example.onlinecoursesclient.domain.usecase

class GetParagraphsUseCase {

    operator fun invoke(text: String): List<String> {
        return text
            .split(Regex("\\n\\s*\\n")) // Разделяем по пустым строкам
            .filter { it.isNotBlank() }
            .map { it.trim() }
    }
}