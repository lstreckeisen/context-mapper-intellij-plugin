package org.contextmapper.intellij.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

private const val NOTIFICATION_GROUP_ID = "Context Mapper DSL"

fun showInfoNotification(
    project: Project,
    content: String
) = NotificationGroupManager.getInstance()
    .getNotificationGroup(NOTIFICATION_GROUP_ID)
    .createNotification(content, NotificationType.INFORMATION)
    .notify(project)

fun showErrorNotification(
    project: Project,
    content: String
) = NotificationGroupManager.getInstance()
    .getNotificationGroup(NOTIFICATION_GROUP_ID)
    .createNotification(content, NotificationType.ERROR)
    .notify(project)
