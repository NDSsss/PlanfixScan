package ru.nds.planfix.scan.di

import org.koin.dsl.module
import ru.nds.planfix.scan.ui.notifications.NotificationsManager
import ru.nds.planfix.scan.ui.notifications.NotificationsManagerImpl
import ru.nds.planfix.scan.ui.notifications.NotificationsManagerSetUp

val notificationsModule = module {
    single { NotificationsManagerImpl() }

    factory<NotificationsManagerSetUp> { get<NotificationsManagerImpl>() }
    factory<NotificationsManager> { get<NotificationsManagerImpl>() }
}