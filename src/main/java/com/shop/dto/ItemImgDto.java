package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

        private static ModelMapper modelMapper = new ModelMapper();
//    private static ItemImgMapStruct itemImgMapStruct= new ItemImgMapStructImpl(); //mapstruct 과제

    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg,ItemImgDto.class);
//        return itemImgMapStruct.toDto(itemImg); //mapstruct 과제
    }

}