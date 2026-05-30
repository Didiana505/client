package com.example.onlinecoursesclient.domain.usecase

class GetShortDescriptionUseCase {

    operator fun invoke(fullDescription: String, maxSentences: Int = 2): String {
        // Очищаем текст от переносов строк и лишних пробелов
        val cleanText = fullDescription
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val sentenceEndings = listOf('.', '!', '?')
        var sentenceCount = 0
        var lastIndex = 0

        // Ищем концы предложений
        for (i in cleanText.indices) {
            if (cleanText[i] in sentenceEndings) {
                sentenceCount++
                lastIndex = i + 1
                if (sentenceCount >= maxSentences) break
            }
        }

        // Если нашли нужное количество предложений обрезаем либо возвращаем первые 150 символов
        return if (lastIndex > 0) {
            cleanText.substring(0, lastIndex).trim()
        } else {
            cleanText.take(150)
        }
    }
}