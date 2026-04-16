package com.chaman.measurement.dto;

public class QuantityInputDTO {

    private QuantityDTO thisQuantityDTO;
    private QuantityDTO thatQuantityDTO;

    public QuantityDTO getThisQuantityDTO()              { return thisQuantityDTO; }
    public void setThisQuantityDTO(QuantityDTO q)        { this.thisQuantityDTO = q; }
    public QuantityDTO getThatQuantityDTO()              { return thatQuantityDTO; }
    public void setThatQuantityDTO(QuantityDTO q)        { this.thatQuantityDTO = q; }
}
