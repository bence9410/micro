package hu.beni.visitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.visitor.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
