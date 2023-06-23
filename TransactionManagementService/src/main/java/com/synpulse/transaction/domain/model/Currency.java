package com.synpulse.transaction.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Currency")
public class Currency {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "currency")
    private String currency;

}
