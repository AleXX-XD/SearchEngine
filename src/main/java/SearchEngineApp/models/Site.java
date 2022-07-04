package SearchEngineApp.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Site")
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "last_error")
    @Type(type = "text")
    private String lastError;

    @Column(name = "url")
    private String url;

    @Column(name = "name")
    private String name;

    public Site(){}

    public Site(String url, String name){
        this.url = url;
        this.name = name;
    }

    public void setAllParameters(Status status, Date statusTime, String lastError) {
        this.status = status;
        this.statusTime = statusTime;
        this.lastError = lastError;
    }

}
