package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("RSA")
public class RsaPublicKeyJwkEntity extends PublicKeyJwkEntity {

    @Column(name = "n", length = 1000)
    private String nvalue;

    @Column(name = "e", length = 1000)
    private String evalue;

}
