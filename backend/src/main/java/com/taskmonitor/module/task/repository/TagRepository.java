package com.taskmonitor.module.task.repository;

import com.taskmonitor.module.task.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {

    List<Tag> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<Tag> findByNameAndUserId(String name, UUID userId);

    Set<Tag> findAllByIdInAndUserId(Set<UUID> ids, UUID userId);
}
