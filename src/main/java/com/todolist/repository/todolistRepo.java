package com.todolist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todolist.model.todolist;

@Repository
public interface todolistRepo extends JpaRepository<todolist, Long> {

}