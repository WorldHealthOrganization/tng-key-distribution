package tng.trustnetwork.keydistribution.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trustlist")
public class TrustList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "url", nullable = false, length = 1024)
    private String url;

    @Column(name = "country", nullable = false, length = 2)
    private String country;

    @Column(name = "name", nullable = false, length = 512)
    private String name;
}

