package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(name = "verification_method")
@AllArgsConstructor
@NoArgsConstructor
public class VerificationMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private String id;

    @Column(name = "vm_id", nullable = true, length = 100)
    private String vmId;

    @Column(name = "type", nullable = true, length = 100)
    private String type;

    @Column(name = "controller", nullable = true, length = 100)
    private String controller;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private PublicKeyJwkEntity publicKeyJwk;

}
