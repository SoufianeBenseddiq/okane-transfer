package com.okanetransfer.controller.notification;

import com.okanetransfer.service.dto.notification.request.CreateNotificationRequest;
import com.okanetransfer.service.dto.notification.request.UpdateNotificationRequest;
import com.okanetransfer.service.dto.notification.response.NotificationResponse;
import com.okanetransfer.service.facade.notification.INotificationService;
import com.okanetransfer.shared.enums.TypeNotification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> update(
            @PathVariable("id") Long id,
            @RequestBody UpdateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/destinataire/{destinataireId}")
    public ResponseEntity<List<NotificationResponse>> getByDestinataire(@PathVariable("destinataireId") Long destinataireId) {
        return ResponseEntity.ok(notificationService.getNotificationsByDestinataire(destinataireId));
    }

    @GetMapping("/destinataire/{destinataireId}/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadByDestinataire(@PathVariable("destinataireId") Long destinataireId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByDestinataire(destinataireId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getByType(@PathVariable("type") TypeNotification type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getAllUnread() {
        return ResponseEntity.ok(notificationService.getAllUnreadNotifications());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnread() {
        return ResponseEntity.ok(notificationService.countUnreadNotifications());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/destinataire/{destinataireId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable("destinataireId") Long destinataireId) {
        notificationService.markAllAsRead(destinataireId);
        return ResponseEntity.noContent().build();
    }
}
