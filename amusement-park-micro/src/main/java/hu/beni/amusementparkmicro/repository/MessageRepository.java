package hu.beni.amusementparkmicro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.amusementparkmicro.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{

}
