package com.gsitm.intern.shop.entity;

import com.gsitm.intern.shop.constant.ItemSellStatus;
import com.gsitm.intern.shop.constant.UseItemStatus;
import com.gsitm.intern.shop.dto.ItemFormDto;
import com.gsitm.intern.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                                //상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm;                          //상품명

    @Column(name = "price", nullable = false)
    private int price;                              //가격

    @Column(nullable = false)
    private int stockNumber;                        //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;                      //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;          //상품 판매 상태


    @Enumerated(EnumType.STRING)
    private UseItemStatus useItemStatus;               // 중고 상품 체크

    private Date start_day;                         // 게시글 작성 날

    private Date end_day;                         // 게시글 끝 날

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.useItemStatus = itemFormDto.getUseItemStatus();
        this.start_day = itemFormDto.getStart_day();
        this.end_day = itemFormDto.getEnd_day();
    }

    public void removeStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber) {  // 상품의 재고를 증가시키는 메소드
        this.stockNumber += stockNumber;
    }

}

