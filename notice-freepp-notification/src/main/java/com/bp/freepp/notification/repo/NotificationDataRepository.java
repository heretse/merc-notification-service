package com.bp.freepp.notification.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bp.freepp.notification.model.NotificationData;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationDataRepository extends CrudRepository<NotificationData, Integer> {
    @Query(value = "SELECT * FROM notification_data d WHERE d.delivery_sucess = (:status)", nativeQuery = true)
    List<NotificationData> findAllByStatus(@Param("status") Integer status);
}
