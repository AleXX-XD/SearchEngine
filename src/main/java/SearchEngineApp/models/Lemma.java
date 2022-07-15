package SearchEngineApp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Lemma")
public class Lemma implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "lemma")
    private String lemma;

    @Column(name = "frequency")
    private int frequency;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    @JsonIgnore
    private Site site;

    public Lemma(){}

    public Lemma(String lemma, int frequency, Site site){
        this.frequency = frequency;
        this.lemma = lemma;
        this.site = site;
    }
}
