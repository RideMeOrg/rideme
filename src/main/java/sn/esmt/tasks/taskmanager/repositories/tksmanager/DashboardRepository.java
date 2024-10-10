package sn.esmt.tasks.taskmanager.repositories.tksmanager;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.esmt.tasks.taskmanager.entities.tksmanager.Dashboard;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    List<Dashboard> findByPointRencontre(String pointRencontre);

    boolean existsByPointRencontre(String pointRencontre);
    boolean existsByPointSeparation(String pointSeparation);
}
