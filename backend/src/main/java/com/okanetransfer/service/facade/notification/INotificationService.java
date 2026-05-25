package com.okanetransfer.service.facade.notification;

import com.okanetransfer.service.dto.notification.request.CreateNotificationRequest;
import com.okanetransfer.service.dto.notification.request.UpdateNotificationRequest;
import com.okanetransfer.service.dto.notification.response.NotificationResponse;
import com.okanetransfer.shared.enums.TypeNotification;

import java.util.List;

public interface INotificationService {

    List<NotificationResponse> getAllNotifications();

    NotificationResponse getNotificationById(Long id);

    NotificationResponse createNotification(CreateNotificationRequest request);

    NotificationResponse updateNotification(Long id, UpdateNotificationRequest request);

    void deleteNotification(Long id);

    List<NotificationResponse> getNotificationsByDestinataire(Long destinataireId);

    List<NotificationResponse> getUnreadNotificationsByDestinataire(Long destinataireId);

    List<NotificationResponse> getNotificationsByType(TypeNotification type);

    List<NotificationResponse> getAllUnreadNotifications();

    long countUnreadNotifications();

    void markAsRead(Long notificationId);

    void markAllAsRead(Long destinataireId);
}
