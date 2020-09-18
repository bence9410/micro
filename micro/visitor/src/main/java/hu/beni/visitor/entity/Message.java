package hu.beni.visitor.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "visitor_message")
public class Message implements Serializable {

	private static final long serialVersionUID = -2472933862639238778L;

	@Id
	private Long id;

	private String content;

}