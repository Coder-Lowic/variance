package com.lowic.data.analysis.export.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Lowic
 */
@Getter
@Setter
@ToString
public class SbCampAndSpOrSdCombineBr implements Serializable {
    private LocalDateTime date;

    private Long impressions;

    private Long clicks;

    private BigDecimal spend;

    private BigDecimal sales;

    private Integer orders;

    private Integer units;

    private String categoryOne;

    private Integer sessionsTotal;

    private Integer unitsOrder;

    private BigDecimal productSales;
}
