package com.example.SpringBatchTutorial.job.DbDataReadWrite;

import com.example.SpringBatchTutorial.core.domain.account.Accounts;
import com.example.SpringBatchTutorial.core.domain.account.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.order.Orders;
import com.example.SpringBatchTutorial.core.domain.order.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TrMigrationConfig
 *
 * @author squirMM
 * @date 2023/12/27
 */

/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {
    private final OrdersRepository ordersRepository;
    private final AccountsRepository accountsRepository;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                .incrementer(new RunIdIncrementer())
                .start(trMigrationStep)
                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrderReader, ItemProcessor trOrderProcessor, ItemWriter trOrderWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                /*
                * 1. Reader Type : Order
                * 2. Writer Type : Accounts
                * 3. Size : 5 -> 5개 단위(트랜젝션 개수/chunk=트랜잭션)로 가져올 것 이다.*/
                .<Orders, Accounts>chunk(5)
                .reader(trOrderReader)
                .processor(trOrderProcessor)
                .writer(trOrderWriter)
                .build();
    }

    /*
    * 1. RepositoryItemWriter : 레포지토리를 명시하여 사용
    * 2. ItemWriter : 레포지토리에서 바로 함수를 호출하여 사용
    * 둘의 결과는 같다. 단 사용할 수 있는게 조금씩 다르니 확인해서 사용할 것.*/
    @StepScope
    @Bean
    public RepositoryItemWriter<Accounts> trOrderWriter(){
        return new RepositoryItemWriterBuilder<Accounts>()
                .repository(accountsRepository)
                .methodName("save")
                .build();
    }

    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrderWriter_(){
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor(){
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                /*실무에서는 보다 복잡한 로직이 필요하다.*/
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrderReader(){
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrderReader")
                .repository(ordersRepository)
                .methodName("findAll")
                /*chunkSize 와 동일하게 가져감*/
                .pageSize(5)
                .arguments(Arrays.asList())
                /*
                * 보통 정렬은 Map type 으로 가져가며
                * 상세하게 정의 해 줄 수 있다.*/
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
