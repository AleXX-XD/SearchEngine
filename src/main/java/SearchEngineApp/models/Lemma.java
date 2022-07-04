package SearchEngineApp.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

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

    @Column(name = "site_id")
    private int siteId;

    public Lemma(){}

    public Lemma(String lemma, int frequency, int siteId){
        this.frequency = frequency;
        this.lemma = lemma;
        this.siteId = siteId;
    }
}
