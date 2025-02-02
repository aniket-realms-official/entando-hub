package com.entando.hub.catalog.persistence.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Setter
@Getter
public class Bundle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    //private String descriptionImage;
    private String gitRepoAddress;
    //comma separated bundle ids
    private String dependencies;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<BundleGroup> bundleGroups;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bundle bundle = (Bundle) o;
        return Objects.equals(id, bundle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bundle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", gitRepoAddress='" + gitRepoAddress + '\'' +
                ", dependencies='" + dependencies + '\'' +
                '}';
    }
}
