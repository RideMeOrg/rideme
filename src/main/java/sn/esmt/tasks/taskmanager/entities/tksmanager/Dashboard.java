package sn.esmt.tasks.taskmanager.entities.tksmanager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import sn.esmt.tasks.taskmanager.entities.BaseEntity;
import sn.esmt.tasks.taskmanager.entities.Profile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID id;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date date;

    private String heure;

    private float tarif;

    private String pointRencontre;

    private String pointSeparation;

    private int nombrePlace;

    private boolean allerRetour;

    private boolean prive;
    @JsonProperty("statut")
    private int statut;

    private boolean voyage;
    private int nombrePlaceOccupee = 0;  // Nouveau champ avec valeur par défaut à 0
    private UUID userId;

//    @ManyToOne
////    private Profile profile;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

//    public Profile getProfile() {
//        return profile;
//    }
//
//    public void setProfile(Profile profile) {
//        this.profile = profile;
//    }


    public float getTarif() {
        return tarif;
    }

    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public String getPointRencontre() {
        return pointRencontre;
    }

    public void setPointRencontre(String pointRencontre) {
        this.pointRencontre = pointRencontre;
    }

    public String getPointSeparation() {
        return pointSeparation;
    }

    public void setPointSeparation(String pointSeparation) {
        this.pointSeparation = pointSeparation;
    }

    public int getNombrePlace() {
        return nombrePlace;
    }

    public void setNombrePlace(int nombrePlace) {
        this.nombrePlace = nombrePlace;
    }

    public boolean isAllerRetour() {
        return allerRetour;
    }

    public void setAllerRetour(boolean allerRetour) {
        this.allerRetour = allerRetour;
    }

    public boolean isPrive() {
        return prive;
    }

    public void setPrive(boolean prive) {
        this.prive = prive;
    }

    public int isStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public boolean isVoyage() {
        return voyage;
    }

    public void setVoyage(boolean voyage) {
        this.voyage = voyage;
    }
    public int getNombrePlaceOccupee() {
        return nombrePlaceOccupee;
    }

    public void setNombrePlaceOccupee(int nombrePlaceOccupee) {
        this.nombrePlaceOccupee = nombrePlaceOccupee;
    }


    public Dashboard(Date date, String heure, float tarif, String pointRencontre, String pointSeparation, int nombrePlace, boolean allerRetour, boolean prive, int statut, boolean voyage, int nombrePlaceOccupee, UUID userId) {
        this.date = date;
        this.heure = heure;
        this.tarif = tarif;
        this.pointRencontre = pointRencontre;
        this.pointSeparation = pointSeparation;
        this.nombrePlace = nombrePlace;
        this.allerRetour = allerRetour;
        this.prive = prive;
        this.statut = statut;
        this.voyage = voyage;
        this.nombrePlaceOccupee = nombrePlaceOccupee;
        this.userId = userId;
    }
}
