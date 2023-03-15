package com.rabbit.dayfilm.user.controller;

import com.rabbit.dayfilm.common.CodeSet;
import com.rabbit.dayfilm.user.dto.AddressCreateDto;
import com.rabbit.dayfilm.user.dto.AddressDto;
import com.rabbit.dayfilm.user.dto.OrderListResDto;
import com.rabbit.dayfilm.user.service.UserAddressService;
import com.rabbit.dayfilm.user.service.UserService;
import com.rabbit.dayfilm.common.EndPoint;
import com.rabbit.dayfilm.common.response.SuccessResponse;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "회원")
@RequestMapping(EndPoint.USER)
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserAddressService userAddressService;

    @GetMapping(EndPoint.CHECK_NICKNAME)
    @Operation(summary = "닉네임 중복 검사", description = "입력한 닉네임을 갖고 있는 회원(가게X)을 찾아, 중복된 닉네임이 아니면 OK를 반환합니다.")
    public ResponseEntity<SuccessResponse> checkNickname(@RequestParam("nickname") String nickname) {
        return ResponseEntity.ok().body(new SuccessResponse(userService.checkNickname(nickname)));
    }

    @PostMapping(EndPoint.ADDRESS)
    @Operation(summary = "회원 주소 추가", description = "회원의 주소를 추가합니다.\n첫 번째 주소는 무조건 default이고, 그 다음부터는 isDefault가 true인 경우에 기본 선택된 주소가 됩니다.")
    public ResponseEntity<List<AddressDto>> addAddress(@RequestBody AddressCreateDto addressCreateDto) {
        return ResponseEntity.ok(userAddressService.createAddress(addressCreateDto));
    }

    @GetMapping(EndPoint.ADDRESS)
    @Operation(summary = "회원 주소 목록 조회", description = "회원의 주소 목록 반환")
    public ResponseEntity<List<AddressDto>> getAddresses(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok().body(userAddressService.getAddresses(userId));
    }

    @PostMapping(EndPoint.ADDRESS_DELETE)
    @Operation(summary = "회원 주소 삭제", description = "기본 배송지는 삭제할 수 없습니다.")
    public ResponseEntity<SuccessResponse> deleteAddress(@RequestParam("addressId") Long addressId) {
        userAddressService.deleteAddress(addressId);
        return ResponseEntity.ok().body(new SuccessResponse(CodeSet.OK));
    }

    /*마이페이지*/
    @GetMapping(EndPoint.ITEM)
    @Operation(summary = "회원 주문 목록 조회", description = "회원 주문 목록 조회\n회원 번호 넘겨주시면 됩니다.")
    public ResponseEntity<OrderListResDto> getOrderList(@RequestParam("userId") Long userId, Pageable pageable) {
        return ResponseEntity.ok(userService.getOrderList(userId, pageable));
    }
}
