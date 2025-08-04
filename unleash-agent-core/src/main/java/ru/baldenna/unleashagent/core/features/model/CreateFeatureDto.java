package ru.baldenna.unleashagent.core.features.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.baldenna.unleashagent.core.tags.model.Tag;

import java.util.HashSet;
import java.util.Objects;

@Slf4j
@Getter
@AllArgsConstructor
public class CreateFeatureDto {
    private String name;
    private String type;
    private String description;
    private HashSet<Tag> tags;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateFeatureDto that = (CreateFeatureDto) o;
        if (this.hashCode() != that.hashCode())
            return false;
        return flagEquals(this, that);
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    boolean flagEquals(CreateFeatureDto flag1, CreateFeatureDto flag2) {
        boolean nameEquals = flag1.getName().equals(flag2.getName());
        if (!nameEquals) {
            return false;
        }
        boolean typeEquals = (flag1.getType() != null &&  flag2.getType() != null) && flag1.getType().equals(flag2.getType());
        if (!typeEquals) {
            log.info("Type mismatch. {} vs {}", flag1.getType(), flag2.getType());
        }
        boolean descrtiptionEquals = (flag1.getDescription() != null &&  flag2.getDescription() != null) &&  flag1.getDescription().equals(flag2.getDescription());
        if (!descrtiptionEquals) {
            log.info("Description mismatch. {} vs {}", flag1.getDescription(), flag2.getDescription());
        }
        boolean tagsEquals =  (flag1.getTags() != null &&  flag2.getTags() != null) &&  flag1.getTags().containsAll(flag2.getTags()) && flag2.getTags().containsAll(flag1.getTags());
        if (!tagsEquals) {
            log.info("Tags mismatch. {} vs {}", flag1.getTags(), flag2.getTags());
        }
        return  typeEquals && descrtiptionEquals && tagsEquals;
    }
}

