package tng.trustnetwork.keydistribution.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "proof")
@AllArgsConstructor
@NoArgsConstructor
public class ProofEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition="BIGINT")
	private String id;
	
	@Column(name = "type", nullable = true, length = 100)
	private String type;
	
	@Column(name = "created", nullable = true, length = 100)
	private String created;
	
	@Column(name = "nonce", nullable = true, length = 100)
	private String nonce;
	
	@Column(name = "proof_purpose", nullable = true, length = 100)
	private String proofPurpose;
	
	@Column(name = "verification_method", nullable = true, length = 100)
	private String verificationMethod;
	
	@Column(name = "jws", nullable = false, length = 500)
	private String jws;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decentralized_identifier_id")
    private DecentralizedIdentifierEntity decentralizedIdentifierEntity;
}
