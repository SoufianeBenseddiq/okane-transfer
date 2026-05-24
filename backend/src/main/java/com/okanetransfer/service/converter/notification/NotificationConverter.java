package com.okanetransfer.service.converter.notification;

import com.okanetransfer.entity.notification.Notification;
import com.okanetransfer.service.dto.notification.request.CreateNotificationRequest;
import com.okanetransfer.service.dto.notification.response.NotificationResponse;

public class NotificationConverter {

    public static Notification toEntity(CreateNotificationRequest request) {
        Notification notification = new Notification();
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        return notification;
    }

    public static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getDestinataire().getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getLue(),
                notification.getEnvoyeLe()
        );
    }
}
