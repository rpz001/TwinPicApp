package com.durrutia.twinpic.domain;

import com.durrutia.twinpic.Database;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Pic
 *
 * @author Diego P. Urrutia Astorga
 * @version 20161102
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        database = Database.class,
        cachingEnabled = true,
        orderedCursorLookUp = true, // https://github.com/Raizlabs/DBFlow/blob/develop/usage2/Retrieval.md#faster-retrieval
        cacheSize = Database.CACHE_SIZE
)
public class Pic extends BaseModel {

    /**
     * Identificador unico
     */
    @Setter
    @Getter
    @PrimaryKey(autoincrement = true)
    Long id;

    /**
     * Identificador del dispositivo
     */
    @Setter
    @Getter
    @Column
    String deviceId;

    /**
     * Fecha de la foto
     */
    @Setter
    @Getter
    @Column
    Long date;

    /**
     * URL de la foto
     */
    @Setter
    @Getter
    @Column
    String url;

    /**
     * Latitud
     */
    @Setter
    @Getter
    @Column
    Double latitude;

    /**
     * Longitud
     */
    @Setter
    @Getter
    @Column
    Double longitude;

    /**
     * Numero de likes
     */
    @Setter
    @Getter
    @Column
    Integer positive;

    /**
     * Numero de dis-likes
     */
    @Setter
    @Getter
    @Column
    Integer negative;

    /**
     * Numero de warnings
     */
    @Setter
    @Getter
    @Column
    Integer warning;

}
