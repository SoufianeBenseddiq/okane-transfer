package com.okanetransfer.repository.notification;

import com.okanetransfer.entity.notification.Notification;
import com.okanetransfer.shared.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataireId(Long destinataireId);

    List<Notification> findByDestinataireIdAndLueIsFalse(Long destinataireId);

    List<Notification> findByDestinataireIdAndType(Long destinataireId, TypeNotification type);

    List<Notification> findByLueIsFalse();
}
