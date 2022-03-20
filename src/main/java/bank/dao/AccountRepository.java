package bank.dao;

import bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called ProductCodeRepository
// CRUD refers Create, Read, Update, Delete

public interface AccountRepository extends JpaRepository<Account, Integer> {

}
