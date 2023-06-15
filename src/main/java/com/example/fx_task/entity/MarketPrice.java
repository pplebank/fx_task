package com.example.fx_task.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "market_price")
public class MarketPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instrument_name")
    private String instrumentName;

    @Column(name = "bid_price", precision = 10, scale = 4)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", precision = 10, scale = 4)
    private BigDecimal askPrice;

    @Column(name = "time")
    private LocalDateTime time;

    public MarketPrice(Builder builder) {
        this.instrumentName = builder.instrumentName;
        this.bidPrice = builder.bidPrice;
        this.askPrice = builder.askPrice;
        this.time = builder.time;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String instrumentName;
        private BigDecimal bidPrice;
        private BigDecimal askPrice;
        private LocalDateTime time;

        private Builder() {
        }

        public Builder instrumentName(String instrumentName) {
            this.instrumentName = instrumentName;
            return this;
        }

        public Builder bidPrice(BigDecimal bidPrice) {
            this.bidPrice = bidPrice;
            return this;
        }

        public Builder askPrice(BigDecimal askPrice) {
            this.askPrice = askPrice;
            return this;
        }

        public Builder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public MarketPrice build() {
            return new MarketPrice(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MarketPrice that = (MarketPrice) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}