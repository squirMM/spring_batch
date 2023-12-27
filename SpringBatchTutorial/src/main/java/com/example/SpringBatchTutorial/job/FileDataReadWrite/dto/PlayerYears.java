package com.example.SpringBatchTutorial.job.FileDataReadWrite.dto;

import lombok.Data;
import lombok.ToString;

import java.time.Year;

/**
 * PlayerYears
 *
 * @author squirMM
 * @date 2023/12/27
 */
@Data
@ToString
public class PlayerYears {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int yearsExperience;

    public PlayerYears(Player player) {
        this.ID = player.getID();
        this.lastName = player.getLastName();
        this.firstName = player.getFirstName();
        this.position = player.getPosition();
        this.birthYear = player.getBirthYear();
        this.debutYear = player.getDebutYear();
        this.yearsExperience = Year.now().getValue() - player.getDebutYear();
    }
}
