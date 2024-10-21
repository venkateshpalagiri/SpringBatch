package com.venkatesh.springbatch.repository;

import com.venkatesh.springbatch.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface EmployeeRepository extends JpaRepository<Employee, Serializable> {
}
