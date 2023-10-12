package com.izakdvlpr.todolist.todolist.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;

import java.util.List;
import java.util.UUID;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
  List<TaskModel> findManyByUserId(UUID userId);
}
