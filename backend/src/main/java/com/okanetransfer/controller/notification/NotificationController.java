package com.okanetransfer.controller.notification;

import com.okanetransfer.service.dto.notification.request.CreateNotificationRequest;
import com.okanetransfer.service.dto.notification.request.UpdateNotificationRequest;
import com.okanetransfer.service.dto.notification.response.NotificationResponse;
import com.okanetransfer.service.facade.notification.INotificationService;
import com.okanetransfer.shared.enums.TypeNotification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Notifications", description = "Lecture et gestion des notifications utilisateur (alertes, statuts de transferts, etc.)")
public class NotificationController {

    private final INotificationService notificationService;

    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les notifications", description = "Retourne toutes les notifications. Réservé ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste retournée")
    })
    public ResponseEntity<List<NotificationResponse>> getAll() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Trouver une notification par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification trouvée"),
        @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    public ResponseEntity<NotificationResponse> getById(
            @Parameter(description = "ID de la notification", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une notification", description = "Crée et envoie une notification à un utilisateur.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<NotificationResponse> create(@RequestBody CreateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une notification")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notification mise à jour"),
        @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    public ResponseEntity<NotificationResponse> update(
            @Parameter(description = "ID de la notification", example = "1")
            @PathVariable("id") Long id,
            @RequestBody UpdateNotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notification supprimée"),
        @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la notification", example = "1")
            @PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/destinataire/{destinataireId}")
    @Operation(summary = "Notifications d'un utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications retournées")
    })
    public ResponseEntity<List<NotificationResponse>> getByDestinataire(
            @Parameter(description = "ID du destinataire", example = "7")
            @PathVariable("destinataireId") Long destinataireId) {
        return ResponseEntity.ok(notificationService.getNotificationsByDestinataire(destinataireId));
    }

    @GetMapping("/destinataire/{destinataireId}/unread")
    @Operation(summary = "Notifications non lues d'un utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications non lues retournées")
    })
    public ResponseEntity<List<NotificationResponse>> getUnreadByDestinataire(
            @Parameter(description = "ID du destinataire", example = "7")
            @PathVariable("destinataireId") Long destinataireId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByDestinataire(destinataireId));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Notifications par type", description = "Filtre par type : TRANSFERT, ALERTE, SYSTEME, etc.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications filtrées retournées")
    })
    public ResponseEntity<List<NotificationResponse>> getByType(
            @Parameter(description = "Type de notification", example = "TRANSFERT")
            @PathVariable("type") TypeNotification type) {
        return ResponseEntity.ok(notificationService.getNotificationsByType(type));
    }

    @GetMapping("/unread")
    @Operation(summary = "Toutes les notifications non lues (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications non lues retournées")
    })
    public ResponseEntity<List<NotificationResponse>> getAllUnread() {
        return ResponseEntity.ok(notificationService.getAllUnreadNotifications());
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Compter les notifications non lues (ADMIN)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nombre retourné")
    })
    public ResponseEntity<Long> countUnread() {
        return ResponseEntity.ok(notificationService.countUnreadNotifications());
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marquer une notification comme lue")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notification marquée lue"),
        @ApiResponse(responseCode = "404", description = "Notification introuvable")
    })
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID de la notification", example = "1")
            @PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/destinataire/{destinataireId}/read-all")
    @Operation(summary = "Marquer toutes les notifications d'un utilisateur comme lues")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Toutes marquées lues")
    })
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "ID du destinataire", example = "7")
            @PathVariable("destinataireId") Long destinataireId) {
        notificationService.markAllAsRead(destinataireId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Mes notifications", description = "Retourne toutes les notifications du compte connecté.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notifications retournées")
    })
    public ResponseEntity<List<NotificationResponse>> getMes(Authentication auth) {
        return ResponseEntity.ok(notificationService.getMesNotifications(auth.getName()));
    }

    @GetMapping("/me/unread/count")
    @Operation(summary = "Nombre de mes notifications non lues")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nombre retourné")
    })
    public ResponseEntity<Long> countMesNonLues(Authentication auth) {
        return ResponseEntity.ok(notificationService.countMesNonLues(auth.getName()));
    }

    @PutMapping("/me/read-all")
    @Operation(summary = "Marquer toutes mes notifications comme lues")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Toutes marquées lues")
    })
    public ResponseEntity<Void> marquerToutLu(Authentication auth) {
        notificationService.marquerToutLu(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
