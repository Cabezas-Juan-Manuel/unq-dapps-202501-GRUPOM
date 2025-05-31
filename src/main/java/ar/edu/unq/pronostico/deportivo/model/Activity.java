package ar.edu.unq.pronostico.deportivo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    private String url;
    private String method;
    private String queryParams;
    private LocalDateTime time;

    public Activity(User user, String url, String method, String queryParams, LocalDateTime timeStamp) {
        this.user = user;
        this.method = method;
        this.url = url;
        this.queryParams = queryParams;
        this.time =  timeStamp;
    }
}
