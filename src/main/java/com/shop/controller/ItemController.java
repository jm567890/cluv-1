package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.Tag;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          @RequestParam("tags[]") List<String> tags){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList,tags);
        } catch (Exception e){
            System.out.println(e);
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    //상품 수정 불러오기
    @GetMapping(value = "/admin/item/{itemId}")
    public String ItemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);   //조회한 상품 데이터를 모델에 담아서 뷰로 전달
            List<Tag> tag = itemService.getTags(itemId);    //태그 조회
            List<String> tagList = new ArrayList<>();

            for(Tag t : tag) {
                tagList.add(t.getId().toString());
            }

            model.addAttribute("tags",tagList);
            model.addAttribute("itemFormDto", itemFormDto);
        }catch (EntityNotFoundException e){     //상품 엔티티가 존재하지 않을 경우 에러메시지를 담아 상품 등로 페이지로 이동
            model.addAttribute("errorMessage","존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    //상품 수정 내용 전송
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                           @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model,@RequestParam("tags[]") List<String> tags){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList,tags);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);  //페이징을 위해 PageRequest.of메소드를 통해 Pageable 객체를 생성
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);//조회 조건과 페이징 정보를 파라미터로 넘겨 Page<Item>객체 반환

        model.addAttribute("items", items);     //조회한 상품 데이터 및 페이징 정보를 뷰에 전달
        model.addAttribute("itemSearchDto", itemSearchDto);     //페이지 전환시, 기존 검색 조건 유지한 채 이동할 수 있도록 뷰에 다시 전달
        model.addAttribute("maxPage", 5);       //페이지 번호의 최대 개수

        return "item/itemMng";
    }
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
//        return "item/itemDtlAjax";  //과제
    }

    //과제
//    @GetMapping(value = "/item/{itemId}/api")
//    public @ResponseBody
//    ResponseEntity itemDtlAjax(@PathVariable("itemId") Long itemId) {
//        // Jackson 객체를 생성
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // DTO 객체를 가져오고 이름과 상세 설명에 `with AJAX` 문자열 추가
//        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
//        itemFormDto.setItemNm(itemFormDto.getItemNm() + " with AJAX");
//        itemFormDto.setItemDetail(itemFormDto.getItemDetail() + " with AJAX");
//
//        // Jackson 객체를 사용하여 DTO 객체를 Json 형식으로 변환
//        String json;
//
//        try {
//            json = objectMapper.writeValueAsString(itemFormDto);
//        } catch(Exception e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//
//        // 성공적으로 변환되면 Json 반환
//        return new ResponseEntity<String>(json, HttpStatus.OK);
//    }
}