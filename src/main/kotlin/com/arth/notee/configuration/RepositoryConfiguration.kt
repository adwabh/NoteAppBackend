package com.arth.notee.configuration

import com.arth.notee.repository.NotesRepository
import com.arth.notee.repository.NotesRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfiguration {
    @Bean
    fun getNotesRepository(): NotesRepository = NotesRepositoryImpl()

}