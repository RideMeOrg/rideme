package sn.esmt.tasks.taskmanager.repositories.tksmanager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.esmt.tasks.taskmanager.entities.tksmanager.Dashboard;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    List<Dashboard> findByPointRencontre(String pointRencontre);

    boolean existsByPointRencontre(String pointRencontre);
    boolean existsByPointSeparation(String pointSeparation);
    boolean existsByPointRencontreAndPointSeparationAndUserId(String pointRencontre, String pointSeparation, UUID userId);
    List<Dashboard> findByPointRencontreAndPointSeparation(String pointRencontre, String pointSeparation);
    // Méthode pour trouver les shifts avec au moins une place libre
    // JPQL query to find all dashboards where available seats > 0
    @Query("SELECT d FROM Dashboard d WHERE d.nombrePlace > d.nombrePlaceOccupee")
    List<Dashboard> findDashboardsWithAvailableSeats();
    // Shifts that are NOT voyages and have available seats
    @Query("SELECT d FROM Dashboard d WHERE d.voyage = false AND d.nombrePlace > d.nombrePlaceOccupee")
    List<Dashboard> findNonVoyagesWithAvailableSeats();
    // Shifts that ARE voyages and have available seats
    @Query("SELECT d FROM Dashboard d WHERE d.voyage = true AND d.nombrePlace > d.nombrePlaceOccupee")
    List<Dashboard> findVoyagesWithAvailableSeats();
    // Shifts created by a specific userId that are NOT voyages
    @Query("SELECT d FROM Dashboard d WHERE d.userId = :userId AND d.voyage = false")
    List<Dashboard> findNonVoyagesByUserId(@Param("userId") UUID userId);

    // Shifts created by a specific userId that ARE voyages
    @Query("SELECT d FROM Dashboard d WHERE d.userId = :userId AND d.voyage = true")
    List<Dashboard> findVoyagesByUserId(@Param("userId") UUID userId);

}
