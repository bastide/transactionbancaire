package bank.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@Data
public class Account implements Serializable {
	private static final long serialVersionUID = 2341696775275913561L;

	@Id
	private Integer id;

	private Integer total;

	public void credit(int amount) {
		total += amount;
	}
	
	public void debit(int amount) {
		total -= amount;
	}

}
