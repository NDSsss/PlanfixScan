package ru.nds.planfix.yametric.di

import org.koin.dsl.module
import ru.nds.planfix.yametric.IYandexMetricaActions
import ru.nds.planfix.yametric.YandexMetricaActionsImpl

val yaMetricModule = module {
    factory<IYandexMetricaActions> { YandexMetricaActionsImpl() }
}