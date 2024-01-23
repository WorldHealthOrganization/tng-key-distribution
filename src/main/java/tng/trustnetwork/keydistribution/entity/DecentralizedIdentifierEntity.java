package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@Entity
@Table(name = "decentralized_identifier")
@AllArgsConstructor
@NoArgsConstructor
public class DecentralizedIdentifierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private String id;

    @Column(name = "did_id", nullable = true, length = 100)
    private String didId;

    @Column(name = "context", nullable = true, length = 100)
    private String context;

    @Column(name = "controller", nullable = true, length = 100)
    private String controller;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "decentralized_identifier_id")
    private List<VerificationMethodEntity> verificationMethod;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proof_id")
    private ProofEntity proof;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

}
