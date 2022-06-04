package SearchEngineApp.models;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
@Entity
@Table(name = "Page")
public class WebPage implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "path", columnDefinition = "text")
    private String path;

    @Column(name = "code")
    private int code;

    @Column(name = "content", columnDefinition = "mediumtext")
    private String content;

    @Transient
    private CopyOnWriteArraySet<String> urlList = new CopyOnWriteArraySet<>();

    public void setUrlList(String string) {
        urlList.add(string);
    }

    public  WebPage() {}

    public WebPage(String path) {
        this.path = path;
    }
}
