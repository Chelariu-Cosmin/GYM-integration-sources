package com.example.gym.view;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@Data
@Builder
@XmlRootElement(name = "customerView")
public class CustomerView {

    private Integer idclient;
    private String nume;
    private String prenume;
    private LocalDate dataNastere;
    @Email
    private String email;
    private String nrTel;
}
