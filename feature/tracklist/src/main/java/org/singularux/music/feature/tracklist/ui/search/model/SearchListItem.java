package org.singularux.music.feature.tracklist.ui.search.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@EqualsAndHashCode
public class SearchListItem {
    private final long id;
}
