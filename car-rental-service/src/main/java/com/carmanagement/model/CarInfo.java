package com.carmanagement.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Model class representing a car in the rental fleet.
 */
@Entity()
@Table(name="car_info")
public class CarInfo extends PanacheEntity {
    public String make;
    public String model;
    public Integer year;
    public String condition;
    public LocalDate dispositionDate;
    
    @Enumerated(EnumType.STRING)
    public CarStatus status;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CarInfo{");
        sb.append("make='").append(make).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", year=").append(year);
        sb.append(", status=").append(status);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}


