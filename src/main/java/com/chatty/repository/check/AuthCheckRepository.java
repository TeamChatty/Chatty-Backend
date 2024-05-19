package com.chatty.repository.check;

import com.chatty.entity.check.AuthCheck;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthCheckRepository extends JpaRepository<AuthCheck, Long> {

   Optional<AuthCheck> findAuthCheckByUserId(Long userId);
   void deleteAuthCheckByUserId(Long id);

   @Query("select ac " +
           "from AuthCheck ac " +
           "where ac.registeredTime <= :cutoffDate")
   List<AuthCheck> findAllByCutoffDate(@RequestParam("cutoffDate") LocalDate cutoffDate);
}
