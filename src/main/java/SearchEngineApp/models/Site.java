package SearchEngineApp.models;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
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

//    @OneToMany(mappedBy = "site")
//    private List<WebPage> pages;

    public Site(){}

    public Site(String url, String name){
        this.url = url;
        this.name = name;
    }

}
