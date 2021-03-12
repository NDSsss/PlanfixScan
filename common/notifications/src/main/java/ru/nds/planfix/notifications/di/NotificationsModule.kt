package ru.nds.planfix.notifications.di

import org.koin.dsl.module
import ru.nds.planfix.notifications.NotificationsManager
import ru.nds.planfix.notifications.NotificationsManagerImpl
import ru.nds.planfix.notifications.NotificationsManagerSetUp

val notificationsModule = module {
    single { NotificationsManagerImpl() }

    factory<NotificationsManagerSetUp> { get<NotificationsManagerImpl>() }
    factory<NotificationsManager> { get<NotificationsManagerImpl>() }
}