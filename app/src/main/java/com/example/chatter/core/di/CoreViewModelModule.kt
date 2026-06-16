package com.example.chatter.core.di

import com.example.chatter.core.domain.use_case.FormateTime
import com.example.chatter.core.domain.use_case.FromHexCode
import com.example.chatter.core.domain.use_case.GetRandomColor
import com.example.chatter.core.domain.use_case.IsValidPhoneNo
import com.example.chatter.core.domain.use_case.ToHexCode
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.viewmodel.scope.viewModelScope

@OptIn(KoinExperimentalAPI::class)
val coreViewModelModule = module {
    viewModelScope {
        scoped {
            FromHexCode()
        }
        scoped {
            ToHexCode()
        }
        scoped {
            GetRandomColor()
        }
        scoped {
            IsValidPhoneNo()
        }
        scoped {
            FormateTime()
        }
    }
}