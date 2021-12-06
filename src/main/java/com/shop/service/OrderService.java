package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderReporsitory;
    private final ItemImgRepository itemImgRepository;
    private final ItemTagRepository itemTagRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())       // 주문할 상품 조회
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);        //이메일을 통해 정보 조회

        //Tag별 누적 판매 증가
        List<ItemTag> itemTag = itemTagRepository.findByItem_Id(item.getId());

        for(ItemTag itemtag : itemTag) {
            itemtag.getTag().addTotalSell();
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList); // 회원 정보와 주문할 상품 리스트 정보
        orderReporsitory.save(order); //생성한 주문 엔티티 저장

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderReporsitory.findOrders(email, pageable);//유저의 아이디와 페이징 조건을 이용해 주문 목록을 조회
        Long totalCount = orderReporsitory.countOrder(email);//유저의 주문 총 개수를 구함

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {    //주문리스트를 순회하면서 구매 이력 페이지에 전달항 DTO를 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y"); //주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount); //페이지 구현 객체를 생성해 반환
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){   //로그인한 사용자와 주문데이터 생성 사용자가 같은지 검사
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderReporsitory.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member saveMember = order.getMember();

        if (!StringUtils.equals(curMember.getEmail(), saveMember.getEmail())){
            return false;       //같지 않음 false
        }
        return true;        //같음 true
    }

    public void cancelOrder(Long orderId){
        Order order = orderReporsitory.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();        //주문 취소 상태 변경 감지시 트랜잭션이 끝날 때 update 쿼리 실행
    }

    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {  //주문할 상품 리스트 생성
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        //현재 로그인한 회원과 주문 상품 목록을 이용해 주문 엔티티 생성
        Order order = Order.createOrder(member, orderItemList);
        orderReporsitory.save(order);  //주문 데이터 저장
        return order.getId();
    }
}
