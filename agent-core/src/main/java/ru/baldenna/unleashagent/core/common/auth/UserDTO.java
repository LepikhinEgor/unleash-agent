package ru.baldenna.unleashagent.core.common.auth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String username;
    private String imageUrl;
    private String inviteLink;
    private Integer loginAttempts;
    private Boolean emailSent;
    private Integer rootRole;
    private ZonedDateTime seenAt;
    private ZonedDateTime createdAt;
    private String accountType;
    private List<String> permissions;
    private String scimId;
    private Integer activeSessions;
    private Integer deletedSessions;

}