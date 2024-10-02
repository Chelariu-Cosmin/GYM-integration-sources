package com.example.gym.view;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@Getter
@Setter
public class SaliView implements Serializable {

    private Integer idsala;
    private String nume_sala;
    private String address_id;
}
