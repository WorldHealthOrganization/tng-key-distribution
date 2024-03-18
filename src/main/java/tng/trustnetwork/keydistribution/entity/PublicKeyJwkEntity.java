package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "public_key_jwk")
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyJwkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id;

    @Column(name = "crv", length = 100)
    private String crv;

    @Column(name = "kty", length = 100)
    private String kty;

    @Column(name = "x", length = 100)
    private String xvalue;

    @Column(name = "y", length = 100)
    private String yvalue;

    @Column(name = "x5c", length = 7000)
    private String x5c;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

}
