package hu.beni.amusementpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hu.beni.amusementpark.entity.GuestBook;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBook, Long>{

}
