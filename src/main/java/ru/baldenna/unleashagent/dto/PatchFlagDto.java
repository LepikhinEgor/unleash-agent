package ru.baldenna.unleashagent.dto;

import lombok.Data;

@Data
public class PatchFlagDto {

    private String op;
    private String path;
    private String value;

}
