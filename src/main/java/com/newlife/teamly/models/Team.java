package com.newlife.teamly.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long teamId;

    @NotNull(message = "Team name cannot be null")
    public String teamName;

    @NotNull(message = "Team description cannot be null")
    public String teamDescription;

    @CreationTimestamp
    public LocalDate teamCreatedDate;

    @ManyToOne
    @JoinColumn(name = "team_owner_id", unique = true)
    public User teamOwner;

    @OneToMany(mappedBy = "team")
    private List<User> members;
}
