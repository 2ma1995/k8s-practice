package ordersystem.ordersystem.member.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import ordersystem.ordersystem.common.auth.JwtTokenProvider;
import ordersystem.ordersystem.member.domain.Member;
import ordersystem.ordersystem.member.dto.LoginDto;
import ordersystem.ordersystem.member.dto.MemberRefreshDto;
import ordersystem.ordersystem.member.dto.MemberResDto;
import ordersystem.ordersystem.member.dto.MemberSaveReqDto;
import ordersystem.ordersystem.member.service.MemberService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @Qualifier("rtdb")
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, RedisTemplate<String, Object> redisTemplate) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        Long memberId = memberService.create(memberSaveReqDto);
        return new ResponseEntity<>(memberId, HttpStatus.CREATED);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")// 가장편한방법, ROLE_ 붙일필요 없음. 예외는 filter레벨에서 발생.
    public ResponseEntity<?> list() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
//                throw new AccessDeniedException("권한없음");
//        }
        List<MemberResDto> memberResDto = memberService.findAll();
        return new ResponseEntity<>(memberResDto, HttpStatus.OK);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo(){
       MemberResDto dto = memberService.myInfo();
    return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody LoginDto dto) {
//        email, password검증
        Member member = memberService.login(dto);
//        토큰 생성 및 return
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        String refreshtoken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole().toString());

//          redis 에 rt저장
        redisTemplate.opsForValue().set(member.getEmail(), refreshtoken, 200, TimeUnit.DAYS);//200일 tiemtolive

//          사용자에게 at,rt지급
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", token);
        loginInfo.put("refreshToken", refreshtoken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> generateNewAt(@RequestBody MemberRefreshDto dto) {
//        rt디코딩 후 email추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyRt)
                .build()
                .parseClaimsJws(dto.getRefreshToken())
                .getBody();
//        rt를 redis의 rt와 비교 검증
        Object rt = redisTemplate.opsForValue().get(claims.getSubject());
        if (rt == null || !rt.toString().equals(dto.getRefreshToken())){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

//        at생성하여 지급
        String token = jwtTokenProvider.createToken(claims.getSubject(), claims.get("role").toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("token", token);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);

    }

}
