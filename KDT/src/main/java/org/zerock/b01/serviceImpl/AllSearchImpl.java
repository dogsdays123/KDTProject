package org.zerock.b01.serviceImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Product;
import org.zerock.b01.domain.QProduct;
import org.zerock.b01.domain.QUserBy;
import org.zerock.b01.dto.ProductListAllDTO;
import org.zerock.b01.service.AllSearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AllSearchImpl extends QuerydslRepositorySupport implements AllSearch {
    public AllSearchImpl() {
        super(Product.class); // Product만 사용
    }

    @Override
    public Page<ProductListAllDTO> searchWithAll(String[] types, String keyword, String pCode, String pName, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        QProduct product = QProduct.product;
        QUserBy userBy = QUserBy.userBy;
        JPQLQuery<Product> query = from(product);
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        LocalDate localDateStart;

        if(startDate != null) {
            localDateStart = startDate;
        } else {
            //날짜받기
            DateTimePath<LocalDateTime> dateTimePath = Expressions.dateTimePath(LocalDateTime.class, "regDate");
            // LocalDateTime에서 LocalDate로 변환
            LocalDateTime localDateTimeStart = LocalDateTime.now();
            localDateStart = localDateTimeStart.toLocalDate();
        }

        if(keyword != null) {
            booleanBuilder.and(product.pName.contains(keyword));
        }

        if(pName != null){
            booleanBuilder.and(product.pName.contains(pName));
        }

        if(pCode != null) {
            booleanBuilder.and(product.pCode.contains(pCode));
        }

        query.where(booleanBuilder);
        query.groupBy(product);

        this.getQuerydsl().applyPagination(pageable, query);

        // 조회된 Product 엔티티 리스트
        List<Product> resultList = query.fetch();

        // DTO로 변환
        if(startDate != null && endDate != null) {
            List<ProductListAllDTO> dtoList = resultList.stream()
                    .map(prod -> ProductListAllDTO.builder()
                            .pId(prod.getPId())
                            .pCode(prod.getPCode())
                            .pName(prod.getPName())
                            .StartDate(startDate)
                            .build())
                    .collect(Collectors.toList());

            // 전체 개수
            long total = query.fetchCount();

            return new PageImpl<>(dtoList, pageable, total);
        } else {
            List<ProductListAllDTO> dtoList = resultList.stream()
                    .map(prod -> ProductListAllDTO.builder()
                            .pId(prod.getPId())
                            .pCode(prod.getPCode())
                            .pName(prod.getPName())
                            .StartDate(localDateStart)
                            .build())
                    .collect(Collectors.toList());

            // 전체 개수
            long total = query.fetchCount();

            return new PageImpl<>(dtoList, pageable, total);
        }
    }
}
