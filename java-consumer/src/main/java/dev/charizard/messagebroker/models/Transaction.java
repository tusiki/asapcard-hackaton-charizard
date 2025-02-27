package dev.charizard.messagebroker.models;

import dev.charizard.messagebroker.exceptions.EntityValidationException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity(name = "transaction")
public class Transaction {
	@Id
	@Column(name = "id", length = 36)
	private String id; //comes from outside

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "person_id")
	private Person person;

	@Column(name = "transaction_date")
	private Instant transactionDate;

	@Column(name = "amount")
	private Double amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 1)
	private TransactionStatus status;

	@OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Installment> installments;

	// NOTE: -> PLEASE USE 'TransactionFactory' TO CREATE! <-

	public Transaction() {
	}

	public Transaction(String id, Person person, Instant transactionDate, Double amount, TransactionStatus status, Set<Installment> installments) {
		this.id = id;
		this.person = person;
		this.transactionDate = transactionDate;
		this.amount = amount;
		this.status = status;
		this.installments = installments;
	}

	public static Transaction create(
					String id,
					Person person,
					Instant transactionDate,
					Double amount
	) {
		var transaction = new Transaction(
						id.trim(),
						person,
						transactionDate,
						amount,
						TransactionStatus.P,
						null


		);
		var errors = transaction.validate();
		if (!errors.isEmpty()) {
			throw new EntityValidationException(errors);
		}
		return transaction;
	}


	public Set<String> validate() {
		var errors = new HashSet<String>();
		if (id == null || id.length() != 36) { //UUID
			errors.add("Invalid id:" + id);
		}
		if (transactionDate == null || transactionDate.isAfter(Instant.now())) {
			errors.add("Invalid transaction date:" + transactionDate);
		}
		if (amount == null || amount <= 0) {
			errors.add("Invalid amount:" + amount);
		}
		if (status == null || !status.equals(TransactionStatus.P) && !status.equals(TransactionStatus.C) && !status.equals(TransactionStatus.N)) {
			errors.add("Invalid status:" + status);
		}
		return errors;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Instant getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Instant transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Set<Installment> getInstallments() {
		return installments;
	}

	public void setInstallments(Set<Installment> installments) {
		this.installments = installments;
	}
}
