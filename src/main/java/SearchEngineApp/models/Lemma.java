package SearchEngineApp.models;

import lombok.Data;
import org.hibernate.annotations.SQLInsert;
import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "Lemma")
//@SQLInsert(sql="insert into Lemma (frequency, lemma, site_id) values (?, ?, ?) on duplicate key update frequency = frequency + 1" )
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
