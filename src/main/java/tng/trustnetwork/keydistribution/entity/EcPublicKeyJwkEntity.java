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
@DiscriminatorValue("EC")
public class EcPublicKeyJwkEntity extends PublicKeyJwkEntity {

    @Column(name = "crv", length = 100)
    private String crv;

    @Column(name = "x", length = 100)
    private String xvalue;

    @Column(name = "y", length = 100)
    private String yvalue;

}
