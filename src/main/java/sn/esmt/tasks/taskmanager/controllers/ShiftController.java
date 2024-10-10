package sn.esmt.tasks.taskmanager.controllers;

import org.springframework.web.bind.annotation.*;
import sn.esmt.tasks.taskmanager.dto.converters.ApiResponse;
import sn.esmt.tasks.taskmanager.entities.tksmanager.Dashboard;
import sn.esmt.tasks.taskmanager.services.TasksService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/shift-manager")
public class ShiftController {
    private final TasksService tasksService;

    public ShiftController(TasksService tasksService) {
        this.tasksService = tasksService;
    }

    @GetMapping("shift")
    public List<Dashboard> getAllDashboards() {
        return this.tasksService.getAllDashboards();
    }

    @PostMapping("shift")
    public Dashboard addDashboard(@RequestBody @Valid Dashboard dashboard) {
        return this.tasksService.addDashboard(dashboard);
    }

    @GetMapping("shift/{shiftId}")
    public Dashboard getDashboard(@PathVariable UUID shiftId) {
        return this.tasksService.getDashboard(shiftId);
    }

    @PutMapping("shift/{shiftId}")
    public Dashboard updateDashboard(@PathVariable UUID shiftId, @RequestBody @Valid Dashboard dashboard) {
        return this.tasksService.updateDashboard(shiftId, dashboard);
    }

    @DeleteMapping("shift/{shiftId}")
    public ApiResponse deleteDashboard(@PathVariable UUID shiftId) {
        return this.tasksService.deleteDashboard(shiftId);
    }
    // API pour rechercher les shifts par point de départ et point d'arrivée
    @GetMapping("shift/search")
    public List<Dashboard> getDashboardsByPoints(@RequestParam String pointRencontre, @RequestParam String pointSeparation) {
        return this.tasksService.getDashboardsByPoints(pointRencontre, pointSeparation);
    }
    // API pour récupérer tous les shifts où il y a au moins une place libre
    @GetMapping("shift/available")
    public List<Dashboard> getDashboardsWithAvailableSeats() {
        return tasksService.getDashboardsWithAvailableSeats();
    }
    // API to get shifts that are NOT voyages and have available seats
    @GetMapping("shift/trajet/available")
    public List<Dashboard> getNonVoyagesWithAvailableSeats() {
        return tasksService.getNonVoyagesWithAvailableSeats();
    }

    // API to get shifts that ARE voyages and have available seats
    @GetMapping("shift/voyages/available")
    public List<Dashboard> getVoyagesWithAvailableSeats() {
        return tasksService.getVoyagesWithAvailableSeats();
    }
    @GetMapping("shift/trajet/user/{userId}")
    public List<Dashboard> getNonVoyagesByUserId(@PathVariable UUID userId) {
        return tasksService.getNonVoyagesByUserId(userId);
    }

    @GetMapping("shift/voyages/user/{userId}")
    public List<Dashboard> getVoyagesByUserId(@PathVariable UUID userId) {
        return tasksService.getVoyagesByUserId(userId);
    }
//
//    @GetMapping("dashboard/{dashboardId}/task-categories")
//    public List<TaskCategory> getTaskCategoryByDashboard(@PathVariable UUID dashboardId) {
//        return this.tasksService.getTaskCategoryByDashboard(dashboardId);
//    }
//
//    @GetMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks")
//    public List<Tasks> getTasksByCategory(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @RequestParam(defaultValue = "", required = false) String search) {
//        return this.tasksService.getTasksByCategory(taskCategoryId, search);
//    }
//
//    @PostMapping("dashboard/{dashboardId}/task-categories")
//    public TaskCategory addTaskCategory(@PathVariable UUID dashboardId, @RequestBody @Valid TaskCategory taskCategory) {
//        return this.tasksService.addTaskCategory(dashboardId, taskCategory);
//    }
//
//    @PutMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}")
//    public TaskCategory updateTaskCategory(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @RequestBody @Valid TaskCategory taskCategory) {
//        return this.tasksService.updateTaskCategory(dashboardId, taskCategoryId, taskCategory);
//    }
//
//    @DeleteMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}")
//    public ApiResponse deleteTaskCategory(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId) {
//        return this.tasksService.deleteTaskCategory(dashboardId, taskCategoryId);
//    }
//
//    @PostMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks")
//    public Tasks addTasks(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @RequestBody @Valid Tasks tasks) {
//        return this.tasksService.addTasks(taskCategoryId, tasks);
//    }
//
//    @PutMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}")
//    public Tasks updateTasks(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestBody @Valid Tasks tasks) {
//        return this.tasksService.updateTasks(taskCategoryId, tasksId, tasks);
//    }
//
//    @DeleteMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}")
//    public ApiResponse deleteTasks(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId) {
//        return this.tasksService.deleteTasks(taskCategoryId, tasksId);
//    }
//
//    @PutMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/add-user")
//    public ApiResponse addUserToTheTask(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestParam UUID profileId) {
//        return this.tasksService.addUserToTheTask(taskCategoryId, profileId);
//    }
//
//    @DeleteMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/add-user")
//    public ApiResponse removeUserFromTask(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestParam UUID profileId) {
//        return this.tasksService.removeUserFromTask(tasksId, profileId);
//    }
//
//
//    @PutMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/attach-file")
//    public ApiResponse attachFileToTheTask(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestParam long mediaFileId) {
//        return this.tasksService.attachFileToTheTask(tasksId, mediaFileId);
//    }
//
//    @DeleteMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/attach-file")
//    public ApiResponse removeFileFromTask(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestParam long mediaFileId) {
//        return this.tasksService.removeFileFromTask(tasksId, mediaFileId);
//    }
//
//
//    @PostMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/comment")
//    public TaskComment addCommentToTheTasks(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestBody TaskComment taskComment) {
//        return this.tasksService.addCommentToTheTasks(tasksId, taskComment);
//    }
//
//    @DeleteMapping("dashboard/{dashboardId}/task-categories/{taskCategoryId}/tasks/{tasksId}/comment")
//    public ApiResponse removeCommentFromTask(@PathVariable UUID dashboardId, @PathVariable UUID taskCategoryId, @PathVariable UUID tasksId, @RequestParam long taskCommentId) {
//        return this.tasksService.removeCommentFromTask(tasksId, taskCommentId);
//    }
}
