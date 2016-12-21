package com.durrutia.twinpic.domain;

import com.durrutia.twinpic.Database;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Twin: Relacion entre dos pics (Uno local con remoto).
 *
 * @author Rodrigo Alejandro Pizarro Zapata.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        database = Database.class,
        cachingEnabled = false,
        orderedCursorLookUp = true, // https://github.com/Raizlabs/DBFlow/blob/develop/usage2/Retrieval.md#faster-retrieval
        cacheSize = Database.CACHE_SIZE
)
public class Twin extends BaseModel {

    /**
     * Pic local.
     */
    @Getter
    @Setter
    @PrimaryKey
    @ForeignKey(tableClass = Pic.class)
    Pic local;

    /**
     * Pic desde el servidor.
     */
    @Getter
    @Setter
    @PrimaryKey
    @ForeignKey(tableClass = Pic.class)
    Pic remote;

    /**
     * Marcador que indica que el usuario ya dio like a la foto remota.
     */
    @Getter
    @Setter
    @Column
    boolean dioLike;

    /**
     * Marcador que indica que el usuario ya dio dislike a la foto remota.
     */
    @Getter
    @Setter
    @Column
    boolean dioDislike;

    /**
     * Marcador que indica que el usuario ya dio warning a la foto remota.
     */
    @Getter
    @Setter
    @Column
    boolean dioWarning;

}
