package com.izakdvlpr.todolist.task;

import com.izakdvlpr.todolist.utils.ObjectUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping
  public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");

    task.setUserId(userId);

    var currentDate = LocalDateTime.now();

    if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start date and the end date must be greater than the current date.");
    }

    if (task.getStartAt().isAfter(task.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start date must be less than the end date.");
    }

    this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.CREATED).body(task);
  }

  @GetMapping
  public List<TaskModel> list(HttpServletRequest request) {
    var userId = (UUID) request.getAttribute("userId");

    var tasks = this.taskRepository.findManyByUserId(userId);

    return tasks;
  }

  @PutMapping("/{id}")
  public ResponseEntity update(@PathVariable UUID id, @RequestBody TaskModel data, HttpServletRequest request) {
    var task = this.taskRepository.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task not found.");
    }

    var userId = (UUID) request.getAttribute("userId");

    if (!task.getUserId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to change this task.");
    }

    ObjectUtils.copyNonNullProperties(data, task);

    this.taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.OK).body(task);
  }
}