package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "verification_method")
@AllArgsConstructor
@NoArgsConstructor
public class VerificationMethodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "vm_id", length = 100)
    private String vmId;

    @Column(name = "type", length = 100)
    private String type;

    @Column(name = "controller", length = 100)
    private String controller;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "public_key_jwk_id")
    private PublicKeyJwkEntity publicKeyJwk;

    @ManyToOne
    @JoinColumn(name = "parent_document_id")
    private DecentralizedIdentifierEntity parentDocument;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

}
