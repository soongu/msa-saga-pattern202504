package com.example.departmentservice.service;

import com.example.departmentservice.entity.Department;

import java.util.Optional;

public interface DepartmentService {

    Optional<Department> saveDepartment(Department department);
    Optional<Department> getDepartmentById(Long id);
}
