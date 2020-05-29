package com.bp.freepp.notification.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bp.freepp.notification.model.NotificationGroup;
import org.springframework.data.repository.query.Param;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface NotificationGroupRepository extends CrudRepository<NotificationGroup, Integer> {
    @Query(value = "SELECT * FROM notification_group g WHERE g.group_name = (:name)", nativeQuery = true)
    NotificationGroup findGroupByName(@Param("name") String name);
}
