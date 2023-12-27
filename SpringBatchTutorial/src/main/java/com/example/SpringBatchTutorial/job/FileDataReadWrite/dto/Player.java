package com.example.SpringBatchTutorial.job.FileDataReadWrite.dto;

import lombok.Data;
import lombok.ToString;

/**
 * Player
 *
 * @author squirMM
 * @date 2023/12/27
 */
@Data
@ToString
public class Player {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
