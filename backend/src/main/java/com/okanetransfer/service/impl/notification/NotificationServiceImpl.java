package com.okanetransfer.service.impl.notification;

import com.okanetransfer.entity.notification.Notification;
import com.okanetransfer.entity.user.Utilisateur;
import com.okanetransfer.repository.notification.NotificationRepository;
import com.okanetransfer.repository.user.UtilisateurRepository;
import com.okanetransfer.service.converter.notification.NotificationConverter;
import com.okanetransfer.service.dto.notification.request.CreateNotificationRequest;
import com.okanetransfer.service.dto.notification.request.UpdateNotificationRequest;
import com.okanetransfer.service.dto.notification.response.NotificationResponse;
import com.okanetransfer.service.facade.notification.INotificationService;
import com.okanetransfer.shared.enums.TypeNotification;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(NotificationConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        return NotificationConverter.toResponse(notification);
    }

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        if (request.getDestinataireId() == null) {
            throw new RuntimeException("Destinataire requis");
        }

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Message requis");
        }

        if (request.getType() == null) {
            throw new RuntimeException("Type de notification requis");
        }

        Utilisateur destinataire = utilisateurRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Notification notification = NotificationConverter.toEntity(request);
        notification.setDestinataire(destinataire);
        notification = notificationRepository.save(notification);

        return NotificationConverter.toResponse(notification);
    }

    @Override
    public NotificationResponse updateNotification(Long id, UpdateNotificationRequest request) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (request.getMessage() != null && !request.getMessage().trim().isEmpty()) {
            notification.setMessage(request.getMessage());
        }

        if (request.getLue() != null) {
            notification.setLue(request.getLue());
        }

        notification = notificationRepository.save(notification);
        return NotificationConverter.toResponse(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByDestinataire(Long destinataireId) {
        return notificationRepository.findByDestinataireId(destinataireId)
                .stream()
                .map(NotificationConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotificationsByDestinataire(Long destinataireId) {
        return notificationRepository.findByDestinataireIdAndLueIsFalse(destinataireId)
                .stream()
                .map(NotificationConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByType(TypeNotification type) {
        return notificationRepository.findAll()
                .stream()
                .filter(n -> n.getType().equals(type))
                .map(NotificationConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllUnreadNotifications() {
        return notificationRepository.findByLueIsFalse()
                .stream()
                .map(NotificationConverter::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadNotifications() {
        return notificationRepository.findByLueIsFalse().size();
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));
        notification.setLue(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long destinataireId) {
        List<Notification> notifications = notificationRepository.findByDestinataireIdAndLueIsFalse(destinataireId);
        notifications.forEach(n -> n.setLue(true));
        notificationRepository.saveAll(notifications);
    }

    // ── Event-driven dispatch ─────────────────────────────────────────────────

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void creerAlerte(String titre, String message, List<Long> destinataireIds) {
        if (destinataireIds == null || destinataireIds.isEmpty()) return;
        String fullMessage = titre + "\n" + message;
        for (Long id : destinataireIds) {
            utilisateurRepository.findById(id).ifPresent(destinataire -> {
                Notification n = new Notification();
                n.setDestinataire(destinataire);
                n.setMessage(fullMessage);
                n.setType(TypeNotification.ALERTE);
                n.setLue(false);
                notificationRepository.save(n);
            });
        }
    }

    // ── Current-user scoped ───────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMesNotifications(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(u -> notificationRepository.findByDestinataireId(u.getId())
                        .stream().map(NotificationConverter::toResponse).toList())
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public long countMesNonLues(String email) {
        return utilisateurRepository.findByEmail(email)
                .map(u -> (long) notificationRepository.findByDestinataireIdAndLueIsFalse(u.getId()).size())
                .orElse(0L);
    }

    @Override
    public void marquerToutLu(String email) {
        utilisateurRepository.findByEmail(email).ifPresent(u -> {
            List<Notification> unread = notificationRepository.findByDestinataireIdAndLueIsFalse(u.getId());
            unread.forEach(n -> n.setLue(true));
            notificationRepository.saveAll(unread);
        });
    }
}
