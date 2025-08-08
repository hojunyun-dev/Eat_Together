package com.example.eat_together.domain.store.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "store")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori") // 한글 분석기 적용
    private String normalizationName;

    public static StoreDocument from(Store store) {
        return StoreDocument.builder()
                .id(String.valueOf(store.getStoreId()))
                .normalizationName(store.getNormalizationName())
                .build();
    }
}
