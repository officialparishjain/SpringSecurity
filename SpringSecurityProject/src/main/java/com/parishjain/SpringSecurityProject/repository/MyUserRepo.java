package com.parishjain.SpringSecurityProject.repository;

import com.parishjain.SpringSecurityProject.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepo extends JpaRepository<MyUser,Integer> {
    MyUser findByEmail(String email);

    MyUser findByVerificationCode(String code);

    @Query(value = "update ss_project.my_user set failed_attempt=:attempt where email=:email ",nativeQuery = true)
    @Modifying
    public void updateFailedAttemptByEmail(String email,Integer attempt);
}
