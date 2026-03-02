package com.armydev.selfdev.domain.record;

import java.io.Serializable;
import java.util.Objects;

public class RecordTagId implements Serializable {
    private Long record;
    private Long tag;

    public RecordTagId() {}

    public RecordTagId(Long record, Long tag) {
        this.record = record;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecordTagId that)) return false;
        return Objects.equals(record, that.record) && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(record, tag);
    }
}
