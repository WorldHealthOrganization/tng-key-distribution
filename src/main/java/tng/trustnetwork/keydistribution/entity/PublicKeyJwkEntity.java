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
@Table(name = "public_key_jwk")
@AllArgsConstructor
@NoArgsConstructor
public class PublicKeyJwkEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id",columnDefinition="BIGINT")
	private String id;
	
	@Column(name = "crv", nullable = true, length = 100)
	private String crv;
	
	@Column(name = "kty", nullable = true, length = 100)
	private String kty;
	
	@Column(name = "x", nullable = true, length = 100)
	private String xvalue;
	
	@Column(name = "y", nullable = true, length = 100)
	private String yvalue;
	
	@Column(name = "x5c", nullable = true, length = 7000)
	private String x5c;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verification_method_id")
    private VerificationMethodEntity verificationMethodEntity;
	
}
