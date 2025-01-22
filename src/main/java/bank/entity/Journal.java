package bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;


@Entity  // JPA
@Getter @ToString @NoArgsConstructor @RequiredArgsConstructor // Lombok
public class Journal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @PositiveOrZero
    @NonNull
    private Integer amount;

    @ManyToOne(optional = false)
    @NonNull
    Account from;

    @ManyToOne(optional = false)
    @NonNull
    Account to;

    private final LocalDateTime time = LocalDateTime.now();
}
