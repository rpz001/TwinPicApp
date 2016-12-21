package com.durrutia.twinpic.domain;

import com.durrutia.twinpic.Database;
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
 * PicReportada: Contiene una Pic que tiene más de 3 advertencias.
 * @author Rodrigo Alejandro Pizarro Zapata.
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
public class PicReportada extends BaseModel {

    /**
     * Identificador único.
     */
    @Getter
    @Setter
    @PrimaryKey(autoincrement = true)
    Long id;

    /**
     * Pic que fue advertida.
     */
    @Getter
    @Setter
    @ForeignKey(tableClass = Pic.class)
    Pic picReportada;

}
