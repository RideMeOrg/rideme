package sn.esmt.tasks.taskmanager.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sn.esmt.tasks.taskmanager.dto.converters.ApiResponse;
import sn.esmt.tasks.taskmanager.dto.converters.RouteRequest;
import sn.esmt.tasks.taskmanager.entities.MediaFile;
import sn.esmt.tasks.taskmanager.entities.Profile;
import sn.esmt.tasks.taskmanager.entities.User;
import sn.esmt.tasks.taskmanager.entities.tksmanager.Dashboard;
import sn.esmt.tasks.taskmanager.entities.tksmanager.TaskCategory;
import sn.esmt.tasks.taskmanager.entities.tksmanager.TaskComment;
import sn.esmt.tasks.taskmanager.entities.tksmanager.Tasks;
import sn.esmt.tasks.taskmanager.exceptions.RequestNotAcceptableException;
import sn.esmt.tasks.taskmanager.exceptions.ResourceNotFoundException;
import sn.esmt.tasks.taskmanager.repositories.MediaFileRepository;
import sn.esmt.tasks.taskmanager.repositories.ProfileRepository;
import sn.esmt.tasks.taskmanager.repositories.tksmanager.DashboardRepository;
import sn.esmt.tasks.taskmanager.repositories.tksmanager.TaskCategoryRepository;
import sn.esmt.tasks.taskmanager.repositories.tksmanager.TaskCommentRepository;
import sn.esmt.tasks.taskmanager.repositories.tksmanager.TasksRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class TasksServiceImpl implements TasksService {

    private final DashboardRepository dashboardRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TasksRepository tasksRepository;
    private final ProfileRepository profileRepository;
    private final MediaFileRepository mediaFileRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final LoggerUser loggerUser;
    private final String GOOGLE_MAPS_API_KEY = "AIzaSyC0WeR-cu0O7B-71NABjvI1hQiE1XEiY9k"; // Mets ta clé API ici

    @PersistenceContext
    private EntityManager entityManager;


    public TasksServiceImpl(DashboardRepository dashboardRepository, TaskCategoryRepository taskCategoryRepository, TasksRepository tasksRepository, ProfileRepository profileRepository, MediaFileRepository mediaFileRepository, TaskCommentRepository taskCommentRepository, LoggerUser loggerUser) {
        this.dashboardRepository = dashboardRepository;
        this.taskCategoryRepository = taskCategoryRepository;
        this.tasksRepository = tasksRepository;
        this.profileRepository = profileRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.loggerUser = loggerUser;
    }

    @Override
    public List<Dashboard> getAllDashboards() {
        return dashboardRepository.findAll();
    }

    @Transactional
    @Override
    public Dashboard addDashboard(Dashboard dashboard) {
        // S'assurer que nombrePlaceOccupee est défini à 0 par défaut si aucune valeur n'est fournie
        if (dashboard.getNombrePlaceOccupee() == 0) {
            dashboard.setNombrePlaceOccupee(0);
        }

        // Vérification d'existence d'un shift avec le même point de rencontre, de séparation et userId
//        if (dashboardRepository.existsByPointRencontreAndPointSeparationAndUserId(
//                dashboard.getPointRencontre(),
//                dashboard.getPointSeparation(),
//                dashboard.getUserId())) {
//            throw new RequestNotAcceptableException("The shift is already created");
//        }

        return dashboardRepository.save(dashboard);
    }


    @Override
    public Dashboard getDashboard(UUID dashboardId) {
        return dashboardRepository.findById(dashboardId).orElseThrow(() -> new ResourceNotFoundException("Shift", "id", dashboardId));
    }

    @Override
    public Dashboard updateDashboard(UUID dashboardId, Dashboard dashboard) {
        Dashboard dashboardDb = dashboardRepository.findById(dashboardId).orElseThrow(() -> new ResourceNotFoundException("Shift", "id", dashboardId));
        dashboardDb.setDate(dashboard.getDate());
        dashboardDb.setHeure(dashboard.getHeure());
        dashboardDb.setAllerRetour(dashboard.isAllerRetour());
        dashboardDb.setPrive(dashboard.isPrive());
        dashboardDb.setNombrePlace(dashboard.getNombrePlace());
        dashboardDb.setPointRencontre(dashboard.getPointRencontre());
        dashboardDb.setPointSeparation(dashboard.getPointSeparation());
        dashboardDb.setStatut(dashboard.isStatut());
        dashboardDb.setVoyage(dashboard.isVoyage());
        dashboardDb.setTarif(dashboard.getTarif());
        dashboardDb.setUserId(dashboard.getUserId());
        return dashboardRepository.save(dashboardDb);
    }

    @Override
    public ApiResponse deleteDashboard(UUID dashboardId) {
        Dashboard dashboardDb = dashboardRepository.findById(dashboardId).orElseThrow(() -> new ResourceNotFoundException("Shift", "id", dashboardId));
//        List<TaskCategory> taskCategories = getTaskCategoryByDashboard(dashboardId);
//        for (TaskCategory taskCategory : taskCategories) {
//            deleteTaskCategory(dashboardId, taskCategory.getId());
//        }
        dashboardRepository.delete(dashboardDb);
        return new ApiResponse(true, "Shift deleted successfully");
    }

    // Implémentation de la méthode pour rechercher les shifts par point de départ et d'arrivée
    @Override
    public List<Dashboard> getDashboardsByPoints(String pointRencontre, String pointSeparation) {
        return dashboardRepository.findByPointRencontreAndPointSeparation(pointRencontre, pointSeparation);
    }
    @Override
    public List<Dashboard> getDashboardsWithAvailableSeats() {
        return dashboardRepository.findDashboardsWithAvailableSeats();
    }
    // Shifts that are NOT voyages
    @Override
    public List<Dashboard> getNonVoyagesWithAvailableSeats() {
        return dashboardRepository.findNonVoyagesWithAvailableSeats();
    }

    // Shifts that ARE voyages
    @Override
    public List<Dashboard> getVoyagesWithAvailableSeats() {
        return dashboardRepository.findVoyagesWithAvailableSeats();
    }
    // Method to get shifts created by a specific userId that are NOT voyages
    @Override
    public List<Dashboard> getNonVoyagesByUserId(UUID userId) {
        return dashboardRepository.findNonVoyagesByUserId(userId);
    }

    // Method to get shifts created by a specific userId that ARE voyages
    @Override
    public List<Dashboard> getVoyagesByUserId(UUID userId) {
        return dashboardRepository.findVoyagesByUserId(userId);
    }

    @Override
    public String getPublicTransportRoute(RouteRequest routeRequest) {
        String url = "https://routes.googleapis.com/directions/v2:computeRoutes";

        // Créer le corps de la requête JSON
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> origin = new HashMap<>();
        origin.put("address", routeRequest.getOrigin());
        Map<String, String> destination = new HashMap<>();
        destination.put("address", routeRequest.getDestination());
        requestBody.put("origin", origin);
        requestBody.put("destination", destination);
        requestBody.put("travelMode", routeRequest.getTravelMode());

        Map<String, Object> transitPreferences = new HashMap<>();
        transitPreferences.put("allowedTravelModes", routeRequest.getAllowedTravelModes());
        requestBody.put("transitPreferences", transitPreferences);

        // Appeler l'API Google Maps avec RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", GOOGLE_MAPS_API_KEY);
        headers.set("X-Goog-FieldMask", "routes.legs.steps.transitDetails");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            // Faire la requête POST à Google Maps
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Error while calling Google Maps API", e);
        }
    }

    @Override
    public Dashboard updateShiftStatus(UUID id, int newStatus) {
        Dashboard dashboard = dashboardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dashboard","id",id));

        dashboard.setStatut(newStatus);
        return dashboardRepository.save(dashboard);  // Sauvegarde et renvoie le shift modifié
    }

    @Override
    public List<TaskCategory> getTaskCategoryByDashboard(UUID dashboardId) {
        return taskCategoryRepository.findByDashboardId(dashboardId);
    }

    @Override
    public List<Tasks> getTasksByCategory(UUID taskCategoryId, String search) {
        Specification<Tasks> tasksSpecification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("taskCategory").get("id"), taskCategoryId);
        if (search != null && !search.isEmpty()) {
            tasksSpecification = tasksSpecification.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + search + "%"));
        }
        return tasksRepository.findAll(tasksSpecification);
    }

    @Override
    public TaskCategory addTaskCategory(UUID dashboardId, TaskCategory taskCategory) {
        Dashboard dashboardDb = dashboardRepository.findById(dashboardId).orElseThrow(() -> new ResourceNotFoundException("Dashboard", "id", dashboardId));
        taskCategory.setDashboard(dashboardDb);
        return taskCategoryRepository.save(taskCategory);
    }

    @Override
    public TaskCategory updateTaskCategory(UUID dashboardId, UUID taskCategoryId, TaskCategory taskCategory) {
        if (!dashboardRepository.existsById(dashboardId)) {
            throw new ResourceNotFoundException("Dashboard", "id", dashboardId);
        }
        TaskCategory taskCategoryDB = taskCategoryRepository.findById(taskCategoryId).orElseThrow(() -> new ResourceNotFoundException("TaskCategory", "id", taskCategoryId));
        taskCategoryDB.setName(taskCategory.getName());
        taskCategoryDB.setIndexColor(taskCategory.getIndexColor());
        return taskCategoryRepository.save(taskCategoryDB);
    }

    @Override
    public ApiResponse deleteTaskCategory(UUID dashboardId, UUID taskCategoryId) {

        if (!taskCategoryRepository.existsById(taskCategoryId)) {
            throw new ResourceNotFoundException("TaskCategory", "id", taskCategoryId);
        }

        if (tasksRepository.countByTaskCategoryId(taskCategoryId) > 0) {
            throw new RequestNotAcceptableException("Can not delete this task category. Please remove all task first");
        }

        taskCategoryRepository.deleteById(taskCategoryId);
        return new ApiResponse(true, "Task category is deleted successfully");
    }

    @Override
    public Tasks addTasks(UUID taskCategoryId, Tasks tasks) {
        TaskCategory taskCategoryDB = taskCategoryRepository.findById(taskCategoryId).orElseThrow(() -> new ResourceNotFoundException("TaskCategory", "id", taskCategoryId));
        tasks.setCreatedBy(loggerUser.getCurrentUser());
        tasks.setTaskCategory(taskCategoryDB);
        tasks.setDashboard(taskCategoryDB.getDashboard());
        return tasksRepository.save(tasks);
    }

    @Override
    public Tasks updateTasks(UUID taskCategoryId, UUID tasksId, Tasks tasks) {
        TaskCategory taskCategoryDB = taskCategoryRepository.findById(taskCategoryId).orElseThrow(() -> new ResourceNotFoundException("TaskCategory", "id", taskCategoryId));
        Tasks taskDb = tasksRepository.findById(tasksId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", tasksId));

        if (taskDb.getTaskCategory().getId() != taskCategoryDB.getId()) {
            taskDb.setTaskCategory(taskCategoryDB);
        }

        taskDb.setTitle(tasks.getTitle());
        taskDb.setImageDescription(tasks.getImageDescription());
        taskDb.setDescription(tasks.getDescription());
        taskDb.setTags(tasks.getTags());
        taskDb.setBadgeColor(tasks.getBadgeColor());
        taskDb.setDeadline(tasks.getDeadline());

        return tasksRepository.save(taskDb);
    }

    @Override
    public ApiResponse deleteTasks(UUID taskCategoryId, UUID tasksId) {
        if (!taskCategoryRepository.existsById(taskCategoryId)) {
            throw new ResourceNotFoundException("TaskCategory", "id", taskCategoryId);
        }

        if (!tasksRepository.existsById(tasksId)) {
            throw new ResourceNotFoundException("Tasks", "id", tasksId);
        }

        // TODO: 8/16/2023 review after implement comment section
        tasksRepository.deleteById(tasksId);

        return new ApiResponse(true, "Tasks is deleted successfully");
    }

    @Override
    public ApiResponse addUserToTheTask(UUID taskId, UUID profileId) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        if (tasks.getProfiles() == null) {
            tasks.setProfiles(new ArrayList<>());
        }

        tasks.getProfiles().add(profile);
        tasksRepository.save(tasks);
        return new ApiResponse(true, profile.getFirstName() + " " + profile.getLastName() + " is successfully add");
    }

    @Override
    public ApiResponse removeUserFromTask(UUID taskId, UUID profileId) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> new ResourceNotFoundException("Profile", "id", profileId));

        tasks.getProfiles().removeIf(profileI -> profileI.getId() == profileId);
        tasksRepository.save(tasks);
        return new ApiResponse(true, profile.getFirstName() + " " + profile.getLastName() + " is successfully remove");
    }

    @Override
    public ApiResponse attachFileToTheTask(UUID taskId, long mediaFileId) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));
        MediaFile mediaFile = mediaFileRepository.findById(mediaFileId).orElseThrow(() -> new ResourceNotFoundException("MediaFile", "id", mediaFileId));

        if (tasks.getMediaFiles() == null) {
            tasks.setMediaFiles(new ArrayList<>());
        }
        tasks.getMediaFiles().add(mediaFile);
        tasksRepository.save(tasks);
        return new ApiResponse(true, mediaFile.getOriginalName() + " is successfully add");
    }

    @Override
    public ApiResponse removeFileFromTask(UUID taskId, long mediaFileId) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));
        MediaFile mediaFile = mediaFileRepository.findById(mediaFileId).orElseThrow(() -> new ResourceNotFoundException("MediaFile", "id", mediaFileId));

        tasks.getMediaFiles().removeIf(mediaFile1 -> mediaFile1.getId() == mediaFileId);
        tasksRepository.save(tasks);
        return new ApiResponse(true, mediaFile.getOriginalName() + " is successfully remove");
    }

    @Override
    public TaskComment addCommentToTheTasks(UUID taskId, TaskComment taskComment) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));

        if (tasks.getTaskComments() == null) {
            tasks.setTaskComments(new ArrayList<>());
        }

        taskComment.setProfile(loggerUser.getCurrentProfile());
        taskComment.setTasks(tasks);
        taskComment = taskCommentRepository.save(taskComment);
        tasks.getTaskComments().add(taskComment);
        tasksRepository.save(tasks);
        return taskComment;
    }

    @Override
    public ApiResponse removeCommentFromTask(UUID taskId, long taskCommentId) {
        Tasks tasks = tasksRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", taskId));
        TaskComment taskComment = taskCommentRepository.findById(taskCommentId).orElseThrow(() -> new ResourceNotFoundException("TaskComment", "id", taskCommentId));

        tasks.getTaskComments().removeIf(taskComment1 -> taskComment1.getId() == taskCommentId);
        tasksRepository.save(tasks);
        return new ApiResponse(true, "Comment is successfully remove");
    }
}
