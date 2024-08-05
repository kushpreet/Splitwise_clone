package com.example.spliwise.splitwisee.repository;

import com.example.spliwise.splitwisee.entity.Group;
import com.example.spliwise.splitwisee.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByUsers(User user);

}