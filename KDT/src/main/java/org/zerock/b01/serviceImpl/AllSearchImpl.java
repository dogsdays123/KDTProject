package org.zerock.b01.serviceImpl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.*;
import org.zerock.b01.dto.PlanListAllDTO;
import org.zerock.b01.dto.ProductListAllDTO;
import org.zerock.b01.dto.SupplierAllDTO;
import org.zerock.b01.dto.UserByAllDTO;
import org.zerock.b01.service.AllSearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class AllSearchImpl extends QuerydslRepositorySupport implements AllSearch {

    public AllSearchImpl() {
        super(ProductionPlan.class);  // QuerydslRepositorySupport에 전달할 엔티티 클래스
    }

    // Product와 ProductionPlan을 조인하는 예시
    public List<ProductionPlan> searchPlans() {
        QProductionPlan plan = QProductionPlan.productionPlan;
        QProduct product = QProduct.product;

        JPAQueryFactory queryFactory = new JPAQueryFactory(getEntityManager());  // QuerydslRepositorySupport에서 EntityManager를 가져옵니다

        return queryFactory
                .selectFrom(plan)
                .join(plan.product, product)
                .where(plan.ppNum.gt(0))  // 예시 조건
                .fetch();
    }

    @Override
    public Page<ProductListAllDTO> productSearchWithAll(String[] types, String keyword, String pCode, String pName, String uName, LocalDate regDate, Pageable pageable) {

        QProduct product = QProduct.product;
        JPQLQuery<Product> query = from(product);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            booleanBuilder.and(product.pName.contains(keyword));
        }

        if (pName != null && !pName.equals("all")) {
            booleanBuilder.and(product.pName.contains(pName));
        }

        if (pCode != null && !pCode.isEmpty()) {
            booleanBuilder.and(product.pCode.contains(pCode));
        }

        if (uName != null && !uName.isEmpty()) {
            booleanBuilder.and(product.pCode.contains(uName));
        }

// 데이터 조회용 query
        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<Product> resultList = query.fetch();

        log.info(">>> 검색 조건{}", regDate);

        // DTO로 변환
        List<ProductListAllDTO> dtoList = resultList.stream()
                .map(prod -> ProductListAllDTO.builder()
                        .pCode(prod.getPCode())
                        .pName(prod.getPName())
                        .pReg(prod.getRegDate() != null ? prod.getRegDate().toLocalDate() : null)
                        .uName(prod.getUserBy().getUName())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<Product> countQuery = from(product).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<PlanListAllDTO> planSearchWithAll(String[] types, String keyword,  String pName, String ppState, LocalDate ppStart, LocalDate ppEnd, Pageable pageable) {

        QProductionPlan productPlan = QProductionPlan.productionPlan;
        QProduct product = QProduct.product;
        JPQLQuery<ProductionPlan> query = from(productPlan);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if((types != null && types.length > 0) && keyword != null) {


            for(String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(productPlan.pName.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(productPlan.ppCode.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(productPlan.product.pCode.contains(keyword));
                        break;
                }
            }
            query.where(booleanBuilder);

        }

        if (ppStart != null && ppEnd != null) {
            booleanBuilder.and(productPlan.ppStart.goe(ppStart));
            booleanBuilder.and(productPlan.ppEnd.loe(ppEnd));
        } else if (ppStart != null) {
            booleanBuilder.and(productPlan.ppStart.eq(ppStart));
        } else if (ppEnd != null) {
            booleanBuilder.and(productPlan.ppEnd.eq(ppEnd));
        }

        if (pName != null && !pName.isEmpty()) {
            booleanBuilder.and(productPlan.pName.contains(pName));
        }

        if (ppState != null && !ppState.isEmpty() && !ppState.equals("전체")) {
            try {
                CurrentStatus status = CurrentStatus.valueOf(ppState); // 문자열 → Enum
                booleanBuilder.and(productPlan.ppState.eq(status));    // Querydsl 조건
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status value: " + ppState);
                // 유효하지 않은 Enum 값 처리 (필요 시 무시하거나 예외 던질 수 있음)
            }
        }
        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<ProductionPlan> resultList = query.fetch();

        List<PlanListAllDTO> dtoList = resultList.stream()
                .map(plan -> PlanListAllDTO.builder()
                        .ppCode(plan.getPpCode())
                        .pCode(plan.getProduct().getPCode())
                        .pName(plan.getPName())
                        .ppNum(plan.getPpNum())
                        .ppState(plan.getPpState().toString())
                        .ppStart(plan.getPpStart())
                        .ppEnd(plan.getPpEnd())
                        .uName(plan.getUserBy().getUName())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<ProductionPlan> countQuery = from(productPlan).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<UserByAllDTO> userBySearchWithAll(String[] types, String keyword, String uName,
                                                  String userJob, String userRank, LocalDateTime modDate,
                                                  String status, String uId,  Pageable pageable){
        QUserBy userBy = QUserBy.userBy;
        JPQLQuery<UserBy> query = from(userBy);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (uName != null && !uName.isEmpty()) {
            booleanBuilder.and(userBy.uName.contains(uName));
        }

        if (userJob != null && !userJob.isEmpty()) {
            booleanBuilder.and(userBy.userJob.contains(userJob));
        }

        BooleanExpression rankCondition = userBy.userRank.isNull()
                .or(userBy.userRank.isEmpty());

        BooleanExpression jobCondition = userBy.userJob.isNotNull()
                .and(userBy.userType.ne("other"));

        BooleanExpression statusCondition =
                userBy.status.eq("대기중")
                        .or(userBy.status.isNull());

        // 전부 and로 묶기
        booleanBuilder.and(rankCondition)
                .and(jobCondition)
                .and(statusCondition);

        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<UserBy> resultList = query.fetch();

        List<UserByAllDTO> dtoList = resultList.stream()
                .map(user -> UserByAllDTO.builder()
                        .uName(user.getUName())
                        .userJob(user.getUserJob())
                        .userRank(user.getUserRank())
                        .modDate(user.getModDate())
                        .status(user.getStatus())
                        .uId(user.getUId())
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<UserBy> countQuery = from(userBy).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }

    @Override
    public Page<SupplierAllDTO> supplierSearchWithAll(String[] types, String keyword, String sName, String sRegNum, String sBusinessType, LocalDateTime sRegDate, String sStatus, Pageable pageable){
        QSupplier supplier = QSupplier.supplier;
        JPQLQuery<Supplier> query = from(supplier);
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sName != null && !sName.isEmpty()) {
            booleanBuilder.and(supplier.sName.contains(sName));
        }

        if (sRegNum != null && !sRegNum.isEmpty()) {
            booleanBuilder.and(supplier.sRegNum.contains(sRegNum));
        }

        booleanBuilder.and(
                supplier.sStatus.isNull()
                        .or(supplier.sStatus.isEmpty())
        );


        query.where(booleanBuilder);
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());
        List<Supplier> resultList = query.fetch();

        List<SupplierAllDTO> dtoList = resultList.stream()
                .map(sup -> SupplierAllDTO.builder()
                        .uId(sup.getUserBy().getUId())
                        .sName(sup.getSName())
                        .sRegNum(sup.getSRegNum())
                        .sBusinessType(sup.getSBusinessType())
                        .sManager(sup.getSManager())
                        .sPhone(sup.getSPhone())
                        .regDate(sup.getRegDate())
                        .sStatus(sStatus)
                        .build())
                .collect(Collectors.toList());

        // 전체 개수
        // 카운트용 별도 쿼리 생성
        JPQLQuery<Supplier> countQuery = from(supplier).where(booleanBuilder);
        long total = countQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, total);
    }
}
