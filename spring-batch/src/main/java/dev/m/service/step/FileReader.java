package dev.m.service.step;


import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class FileReader implements ItemReader<T> {


    @Override
    public Object read() {
        return new FlatFileItemReaderBuilder<>()
                .name("orderItemReader")
                .resource(new ClassPathResource("orders.csv"))
                .delimited()
                .names("CustomerId", "ItemId", "ItemPrice", "ItemName", "PurchaseDate")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Object>() {{
                    setTargetType(String.class);
                }})
                .build();
    }

}
