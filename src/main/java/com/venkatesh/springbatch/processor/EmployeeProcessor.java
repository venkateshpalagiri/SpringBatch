package com.venkatesh.springbatch.processor;

import com.venkatesh.springbatch.entity.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeProcessor implements ItemProcessor<Employee,Employee> {
    @Override
    public Employee process(Employee item) throws Exception {
        if(item.getCountry().equals("Canada")){
            return item;
        }
        return null;
    }
}
