package com.venkatesh.springbatch.config;

import com.venkatesh.springbatch.entity.Employee;
import com.venkatesh.springbatch.processor.EmployeeProcessor;
import com.venkatesh.springbatch.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public FlatFileItemReader<Employee> itemReader(){
        FlatFileItemReader<Employee> itemReader=new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/employees.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    private LineMapper<Employee> lineMapper() {
        DefaultLineMapper<Employee> lineMapper=new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");

        BeanWrapperFieldSetMapper<Employee> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Employee.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
    //Item Processor
    @Bean
    public EmployeeProcessor employeeProcessor(){
        return new EmployeeProcessor();
    }

    //Item Writer
    @Bean
    public RepositoryItemWriter<Employee> itemWriter(){
        RepositoryItemWriter<Employee> itemWriter=new RepositoryItemWriter<>();
        itemWriter.setRepository(employeeRepository);
        itemWriter.setMethodName("save");
        return itemWriter;
    }

    //Step
    @Bean
    public Step step1(){
        return new StepBuilder("csvStep1",jobRepository)
                .<Employee,Employee>chunk(10,platformTransactionManager)
                .reader(itemReader())
                .processor(employeeProcessor())
                .writer(itemWriter())
                .build();
    }
}
