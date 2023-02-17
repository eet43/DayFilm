package com.rabbit.dayfilm.item.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rabbit.dayfilm.item.dto.ItemSearchCondition;
import com.rabbit.dayfilm.item.dto.SelectAllItemsDto;
import com.rabbit.dayfilm.item.dto.SelectDetailImageDto;
import com.rabbit.dayfilm.item.dto.SelectDetailItemDto;
import com.rabbit.dayfilm.item.entity.Category;
import com.rabbit.dayfilm.item.entity.Item;
import com.rabbit.dayfilm.item.entity.QItem;
import com.rabbit.dayfilm.item.entity.QItemImage;
import com.rabbit.dayfilm.store.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.rabbit.dayfilm.item.entity.QItem.*;
import static com.rabbit.dayfilm.item.entity.QItemImage.*;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SelectAllItemsDto> selectAllItems(Category category, Pageable pageable) {
        List<SelectAllItemsDto> content = queryFactory.
                select(Projections.constructor(SelectAllItemsDto.class,
                        item.id.as("itemId"),
                        item.storeName,
                        item.title,
                        item.method,
                        item.pricePerOne,
                        itemImage.imagePath))
                .from(item)
                .innerJoin(item.itemImages, itemImage)
                .where(useCategoryEq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .innerJoin(item.itemImages, itemImage)
                .where(useCategoryEq(category));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public SelectDetailItemDto selectItem(Long id) {
        List<SelectDetailItemDto> itemDto = queryFactory
                .select(Projections.constructor(SelectDetailItemDto.class,
                        item.title,
                        item.category,
                        item.detail,
                        item.pricePerOne,
                        item.pricePerFive,
                        item.pricePerTen,
                        item.brandName,
                        item.modelName,
                        item.method,
                        item.quantity,
                        Projections.list(Projections.constructor(SelectDetailImageDto.class,
                                itemImage.id.as("imageId"),
                                itemImage.imageName,
                                itemImage.imagePath,
                                itemImage.orderNumber))
                ))
                .from(item)
                .leftJoin(item.itemImages, itemImage)
                .where(item.id.eq(id))
                .fetch();

        return itemDto.stream()
                .findFirst()
                .map(item -> new SelectDetailItemDto(item.getTitle(), item.getCategory(), item.getDetail(), item.getPricePerOne(), item.getPricePerFive(),
                        item.getPricePerTen(), item.getBrandName(), item.getModelName(), item.getMethod(), item.getQuantity(),
                        itemDto.stream().flatMap(i -> i.getImages().stream()).collect(Collectors.toList())))
                .orElse(null);
    }

    @Override
    public Page<SelectAllItemsDto> selectWriteItems(Store store, Pageable pageable) {
        List<SelectAllItemsDto> content = queryFactory.
                select(Projections.constructor(SelectAllItemsDto.class,
                        item.id.as("itemId"),
                        item.storeName,
                        item.title,
                        item.method,
                        item.pricePerOne,
                        itemImage.imagePath))
                .from(item)
                .innerJoin(item.itemImages, itemImage)
                .where(item.store.eq(store))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .innerJoin(item.itemImages, itemImage)
                .where(item.store.eq(store));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression useCategoryEq(Category category) {
        return category == null ? null : item.category.eq(category).and(itemImage.orderNumber.eq(1).and(item.use_yn.eq(Boolean.TRUE)));
    }
}
