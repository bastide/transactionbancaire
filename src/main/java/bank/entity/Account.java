package bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity  // JPA
@Getter @Setter @ToString @NoArgsConstructor // Lombok
/**
 * Un compte bancaire.
 */
public class Account  {
	@Id // La clé
	private Integer id;
	@PositiveOrZero // Contrainte de validation JSR 303
	private Integer balance;
}
